
package com.fashionstore.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fashionstore.dao.CartDAO;
import com.fashionstore.dao.CartItemDAO;
import com.fashionstore.dao.ProductDAO;
import com.fashionstore.dao.ProductSizeDAO;

import com.fashionstore.dao.impl.CartDAOImpl;
import com.fashionstore.dao.impl.CartItemDAOImpl;
import com.fashionstore.dao.impl.ProductDAOImpl;
import com.fashionstore.dao.impl.ProductSizeDAOImpl;

import com.fashionstore.model.Cart;
import com.fashionstore.model.CartItem;
import com.fashionstore.model.Product;
import com.fashionstore.model.ProductSize;
import com.fashionstore.model.User;

import com.fashionstore.service.OrderService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private CartDAO cartDAO;
    private CartItemDAO cartItemDAO;
    private ProductDAO productDAO;
    private ProductSizeDAO productSizeDAO;
    private OrderService orderService;

    @Override
    public void init() throws ServletException {

        cartDAO = new CartDAOImpl();
        cartItemDAO = new CartItemDAOImpl();
        productDAO = new ProductDAOImpl();
        productSizeDAO = new ProductSizeDAOImpl();
        orderService = new OrderService();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        User user = getLoggedInUser(request);

        if (user == null) {
            response.sendRedirect(
                request.getContextPath() + "/login"
            );
            return;
        }

        try {

            boolean hasItems =
                loadCartDetails(request, user);

            if (!hasItems) {
                response.sendRedirect(
                    request.getContextPath() + "/cart"
                );
                return;
            }

            request.getRequestDispatcher(
                "/WEB-INF/views/checkout.jsp"
            ).forward(request, response);

        } catch (SQLException e) {

            throw new ServletException(
                "Unable to load checkout page.", e
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        User user = getLoggedInUser(request);

        if (user == null) {
            response.sendRedirect(
                request.getContextPath() + "/login"
            );
            return;
        }

        String fullName =
            clean(request.getParameter("fullName"));

        String mobile =
            clean(request.getParameter("mobile"));

        String address =
            clean(request.getParameter("address"));

        String city =
            clean(request.getParameter("city"));

        String state =
            clean(request.getParameter("state"));

        String pincode =
            clean(request.getParameter("pincode"));

        String paymentMethod =
            clean(request.getParameter("paymentMethod"));

        if (fullName.isEmpty()
                || mobile.isEmpty()
                || address.isEmpty()
                || city.isEmpty()
                || state.isEmpty()
                || pincode.isEmpty()) {

            showCheckoutError(
                request,
                response,
                user,
                "Please fill in all delivery details."
            );
            return;
        }

        if (fullName.length() > 100
                || mobile.length() != 10
                || address.length() > 500
                || city.length() > 100
                || state.length() > 100) {

            showCheckoutError(
                request,
                response,
                user,
                "Please check your delivery details."
            );
            return;
        }

        if (!mobile.matches("[0-9]{10}")) {

            showCheckoutError(
                request,
                response,
                user,
                "Enter a valid 10-digit mobile number."
            );
            return;
        }

        if (!pincode.matches("[0-9]{6}")) {

            showCheckoutError(
                request,
                response,
                user,
                "Enter a valid 6-digit PIN code."
            );
            return;
        }

        if (!paymentMethod.equals("COD")
                && !paymentMethod.equals("UPI")
                && !paymentMethod.equals("CARD")) {

            showCheckoutError(
                request,
                response,
                user,
                "Please select a valid payment method."
            );
            return;
        }

        try {

            boolean hasItems =
                loadCartDetails(request, user);

            if (!hasItems) {
                response.sendRedirect(
                    request.getContextPath() + "/cart"
                );
                return;
            }

            int orderId = orderService.placeOrder(
                user.getUserId(),
                fullName,
                mobile,
                address,
                city,
                state,
                pincode,
                paymentMethod
            );

            /*
             * The order service commits the order
             * before returning its generated ID.
             *
             * We keep this ID in the session so the
             * success page can verify the redirect.
             */
            HttpSession session =
                request.getSession();

            session.setAttribute(
                "lastPlacedOrderId", orderId
            );

            response.sendRedirect(
                request.getContextPath()
                + "/order-success?id=" + orderId
            );

        } catch (SQLException e) {

            /*
             * Do not show raw database error details
             * to the customer.
             */
            getServletContext().log(
                "Checkout failed for user "
                + user.getUserId(), e
            );

            showCheckoutError(
                request,
                response,
                user,
                "Unable to place your order. "
                + "Please check your cart and "
                + "try again."
            );
        }
    }

    private boolean loadCartDetails(
            HttpServletRequest request,
            User user) throws SQLException {

        List<Map<String, Object>> cartDetails =
            new ArrayList<>();

        BigDecimal cartTotal = BigDecimal.ZERO;

        int totalItems = 0;

        Cart cart = cartDAO.getCartByUserId(
            user.getUserId()
        );

        if (cart == null) {

            request.setAttribute(
                "cartDetails", cartDetails
            );

            request.setAttribute(
                "cartTotal", cartTotal
            );

            request.setAttribute(
                "totalItems", totalItems
            );

            return false;
        }

        List<CartItem> cartItems =
            cartItemDAO.getCartItemsByCartId(
                cart.getCartId()
            );

        for (CartItem cartItem : cartItems) {

            if (cartItem.getQuantity() <= 0) {
                continue;
            }

            ProductSize productSize =
                productSizeDAO.getProductSizeById(
                    cartItem.getSizeId()
                );

            if (productSize == null) {
                throw new SQLException(
                    "Invalid product size in cart."
                );
            }

            Product product =
                productDAO.getProductById(
                    productSize.getProductId()
                );

            if (product == null) {
                throw new SQLException(
                    "A product in the cart no longer exists."
                );
            }

            int quantity = cartItem.getQuantity();

            BigDecimal itemTotal =
                product.getPrice().multiply(
                    BigDecimal.valueOf(quantity)
                );

            Map<String, Object> item =
                new HashMap<>();

            item.put("cartItem", cartItem);
            item.put("product", product);
            item.put("productSize", productSize);
            item.put("quantity", quantity);
            item.put("itemTotal", itemTotal);

            cartDetails.add(item);

            cartTotal = cartTotal.add(itemTotal);

            totalItems += quantity;
        }

        request.setAttribute(
            "cartDetails", cartDetails
        );

        request.setAttribute(
            "cartTotal", cartTotal
        );

        request.setAttribute(
            "totalItems", totalItems
        );

        return !cartDetails.isEmpty();
    }

    private void showCheckoutError(
            HttpServletRequest request,
            HttpServletResponse response,
            User user,
            String message)
            throws ServletException, IOException {

        try {

            boolean hasItems =
                loadCartDetails(request, user);

            if (!hasItems) {
                response.sendRedirect(
                    request.getContextPath() + "/cart"
                );
                return;
            }

            request.setAttribute("error", message);

            request.getRequestDispatcher(
                "/WEB-INF/views/checkout.jsp"
            ).forward(request, response);

        } catch (SQLException e) {

            throw new ServletException(
                "Unable to reload checkout.", e
            );
        }
    }

    private User getLoggedInUser(
            HttpServletRequest request) {

        HttpSession session =
            request.getSession(false);

        if (session == null) {
            return null;
        }

        return (User) session.getAttribute(
            "loggedInUser"
        );
    }

    private String clean(String value) {

        return value == null
            ? ""
            : value.trim();
    }
}
