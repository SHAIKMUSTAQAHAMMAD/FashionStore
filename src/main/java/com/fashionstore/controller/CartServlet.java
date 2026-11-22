
package com.fashionstore.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private CartDAO cartDAO;
    private CartItemDAO cartItemDAO;
    private ProductDAO productDAO;
    private ProductSizeDAO productSizeDAO;

    @Override
    public void init() throws ServletException {

        cartDAO = new CartDAOImpl();
        cartItemDAO = new CartItemDAOImpl();
        productDAO = new ProductDAOImpl();
        productSizeDAO = new ProductSizeDAOImpl();
    }

    // ==========================================
    // GET: DISPLAY CART
    // ==========================================

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("loggedInUser") == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("loggedInUser");

        try {

            displayCart(request, response, user);

        } catch (SQLException e) {

            throw new ServletException("Unable to display cart.", e);
        }
    }

    // ==========================================
    // POST: ADD, UPDATE, REMOVE
    // ==========================================

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("loggedInUser") == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("loggedInUser");

        String action = request.getParameter("action");

        try {

            if ("add".equals(action)) {

                addToCart(request, user);

            } else if ("update".equals(action)) {

                updateCart(request, user);

            } else if ("remove".equals(action)) {

                removeCartItem(request, user);

            } else {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid cart action.");
                return;
            }

            response.sendRedirect(
                    request.getContextPath() + "/cart");

        } catch (SQLException e) {

            throw new ServletException(
                    "Unable to process cart operation.", e);
        }
    }

    // ==========================================
    // DISPLAY CART
    // ==========================================

    private void displayCart(HttpServletRequest request,
                             HttpServletResponse response,
                             User user)
            throws SQLException, ServletException, IOException {

        Cart cart = cartDAO.getCartByUserId(user.getUserId());

        List<Map<String, Object>> cartDetails = new ArrayList<>();

        BigDecimal cartTotal = BigDecimal.ZERO;

        int totalItems = 0;

        if (cart != null) {

            List<CartItem> cartItems =
                    cartItemDAO.getCartItemsByCartId(cart.getCartId());

            for (CartItem cartItem : cartItems) {

                ProductSize productSize =
                        productSizeDAO.getProductSizeById(
                                cartItem.getSizeId());

                if (productSize == null) {
                    continue;
                }

                Product product =
                        productDAO.getProductById(
                                productSize.getProductId());

                if (product == null) {
                    continue;
                }

                BigDecimal subtotal = product.getPrice().multiply(
                        BigDecimal.valueOf(cartItem.getQuantity()));

                Map<String, Object> itemDetails = new HashMap<>();

                itemDetails.put("cartItem", cartItem);
                itemDetails.put("product", product);
                itemDetails.put("productSize", productSize);
                itemDetails.put("subtotal", subtotal);

                cartDetails.add(itemDetails);

                cartTotal = cartTotal.add(subtotal);

                totalItems += cartItem.getQuantity();
            }
        }

        request.setAttribute("cart", cart);
        request.setAttribute("cartDetails", cartDetails);
        request.setAttribute("cartTotal", cartTotal);
        request.setAttribute("totalItems", totalItems);

        request.getRequestDispatcher(
                "/WEB-INF/views/cart.jsp")
                .forward(request, response);
    }

    // ==========================================
    // ADD PRODUCT TO CART
    // ==========================================

    private void addToCart(HttpServletRequest request, User user)
            throws SQLException, ServletException {

        int sizeId;
        int quantity;

        try {

            sizeId = Integer.parseInt(
                    request.getParameter("sizeId"));

            quantity = Integer.parseInt(
                    request.getParameter("quantity"));

        } catch (NumberFormatException e) {

            throw new ServletException("Invalid product size or quantity.");
        }

        if (sizeId <= 0 || quantity <= 0) {

            throw new ServletException("Invalid product size or quantity.");
        }

        ProductSize productSize =
                productSizeDAO.getProductSizeById(sizeId);

        if (productSize == null) {

            throw new ServletException("Selected product size does not exist.");
        }

        if (quantity > productSize.getStockQuantity()) {

            throw new ServletException("Requested quantity exceeds available stock.");
        }

        Cart cart = getOrCreateCart(user.getUserId());

        List<CartItem> existingItems =
                cartItemDAO.getCartItemsByCartId(cart.getCartId());

        int existingQuantity = 0;

        for (CartItem item : existingItems) {

            if (item.getSizeId() == sizeId) {

                existingQuantity = item.getQuantity();
                break;
            }
        }

        if (existingQuantity + quantity >
                productSize.getStockQuantity()) {

            throw new ServletException(
                    "Total requested quantity exceeds available stock.");
        }

        CartItem cartItem = new CartItem();

        cartItem.setCartId(cart.getCartId());
        cartItem.setSizeId(sizeId);
        cartItem.setQuantity(quantity);

        boolean added = cartItemDAO.addCartItem(cartItem);

        if (!added) {

            throw new ServletException("Unable to add product to cart.");
        }
    }

    // ==========================================
    // GET OR CREATE CART
    // ==========================================

    private Cart getOrCreateCart(int userId)
            throws SQLException, ServletException {

        Cart cart = cartDAO.getCartByUserId(userId);

        if (cart == null) {

            boolean created = cartDAO.createCart(userId);

            if (!created) {

                throw new ServletException("Unable to create shopping cart.");
            }

            cart = cartDAO.getCartByUserId(userId);
        }

        if (cart == null) {

            throw new ServletException("Shopping cart not found.");
        }

        return cart;
    }

    // ==========================================
    // UPDATE CART ITEM QUANTITY
    // ==========================================

    private void updateCart(HttpServletRequest request, User user)
            throws SQLException, ServletException {

        int cartItemId;
        int quantity;

        try {

            cartItemId = Integer.parseInt(
                    request.getParameter("cartItemId"));

            quantity = Integer.parseInt(
                    request.getParameter("quantity"));

        } catch (NumberFormatException e) {

            throw new ServletException("Invalid cart item or quantity.");
        }

        if (cartItemId <= 0 || quantity <= 0) {

            throw new ServletException("Quantity must be greater than zero.");
        }

        Cart cart = cartDAO.getCartByUserId(user.getUserId());

        if (cart == null) {

            throw new ServletException("Shopping cart not found.");
        }

        CartItem cartItem =
                findCartItem(cart.getCartId(), cartItemId);

        if (cartItem == null) {

            throw new ServletException("Cart item not found.");
        }

        ProductSize productSize =
                productSizeDAO.getProductSizeById(
                        cartItem.getSizeId());

        if (productSize == null ||
                quantity > productSize.getStockQuantity()) {

            throw new ServletException(
                    "Requested quantity exceeds available stock.");
        }

        boolean updated = cartItemDAO.updateCartItemQuantity(
                cartItemId, quantity);

        if (!updated) {

            throw new ServletException("Unable to update cart quantity.");
        }
    }

    // ==========================================
    // REMOVE CART ITEM
    // ==========================================

    private void removeCartItem(HttpServletRequest request, User user)
            throws SQLException, ServletException {

        int cartItemId;

        try {

            cartItemId = Integer.parseInt(
                    request.getParameter("cartItemId"));

        } catch (NumberFormatException e) {

            throw new ServletException("Invalid cart item.");
        }

        if (cartItemId <= 0) {

            throw new ServletException("Invalid cart item.");
        }

        Cart cart = cartDAO.getCartByUserId(user.getUserId());

        if (cart == null) {

            throw new ServletException("Shopping cart not found.");
        }

        CartItem cartItem =
                findCartItem(cart.getCartId(), cartItemId);

        if (cartItem == null) {

            throw new ServletException("Cart item not found.");
        }

        boolean removed = cartItemDAO.removeCartItem(cartItemId);

        if (!removed) {

            throw new ServletException("Unable to remove cart item.");
        }
    }

    // ==========================================
    // FIND CART ITEM BELONGING TO THIS USER
    // ==========================================

    private CartItem findCartItem(int cartId, int cartItemId)
            throws SQLException {

        List<CartItem> cartItems =
                cartItemDAO.getCartItemsByCartId(cartId);

        for (CartItem item : cartItems) {

            if (item.getCartItemId() == cartItemId) {

                return item;
            }
        }

        return null;
    }
}
