
package com.fashionstore.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.fashionstore.dao.OrderDAO;
import com.fashionstore.dao.impl.OrderDAOImpl;
import com.fashionstore.model.Order;
import com.fashionstore.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/order-success")
public class OrderSuccessServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private OrderDAO orderDAO;

    @Override
    public void init() throws ServletException {

        orderDAO = new OrderDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        // Step 1: Check user login

        HttpSession session = request.getSession(false);

        if (session == null) {

            response.sendRedirect(
                request.getContextPath() + "/login"
            );

            return;
        }

        User user = (User) session.getAttribute(
            "loggedInUser"
        );

        if (user == null) {

            response.sendRedirect(
                request.getContextPath() + "/login"
            );

            return;
        }

        // Step 2: Read order ID

        String orderIdParameter =
            request.getParameter("id");

        if (orderIdParameter == null
                || orderIdParameter.trim().isEmpty()) {

            response.sendRedirect(
                request.getContextPath() + "/orders"
            );

            return;
        }

        int orderId;

        try {

            orderId = Integer.parseInt(
                orderIdParameter.trim()
            );

        } catch (NumberFormatException e) {

            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Invalid Order ID"
            );

            return;
        }

        if (orderId <= 0) {

            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Invalid Order ID"
            );

            return;
        }

        // Step 3: Verify the order was just placed

        Object lastOrder =
            session.getAttribute("lastPlacedOrderId");

        if (!(lastOrder instanceof Integer)
                || ((Integer) lastOrder).intValue() != orderId) {

            response.sendError(
                HttpServletResponse.SC_FORBIDDEN,
                "Order confirmation is not available."
            );

            return;
        }

        try {

            // Step 4: Get orders belonging to this user

            List<Order> orders =
                orderDAO.getOrdersByUserId(
                    user.getUserId()
                );

            Order selectedOrder = null;

            if (orders != null) {

                for (Order order : orders) {

                    if (order.getOrderId() == orderId) {

                        selectedOrder = order;

                        break;
                    }
                }
            }

            // Step 5: Check whether order exists

            if (selectedOrder == null) {

                response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "Order not found"
                );

                return;
            }

            // Step 6: Send order to JSP

            request.setAttribute(
                "order",
                selectedOrder
            );

            response.setHeader(
                "Cache-Control",
                "no-store, no-cache, must-revalidate"
            );

            request.getRequestDispatcher(
                "/WEB-INF/views/order-success.jsp"
            ).forward(request, response);

        } catch (SQLException e) {

            throw new ServletException(
                "Unable to load order confirmation",
                e
            );
        }
    }
}
