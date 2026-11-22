
package com.fashionstore.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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

@WebServlet("/orders")
public class OrdersServlet extends HttpServlet {

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

        request.setCharacterEncoding("UTF-8");

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

        try {

            /*
             * Retrieve only the orders belonging
             * to the currently logged-in user.
             */
            List<Order> orders =
                orderDAO.getOrdersByUserId(
                    user.getUserId()
                );

            if (orders == null) {
                orders = new ArrayList<>();
            }

            request.setAttribute("orders", orders);

            request.getRequestDispatcher(
                "/WEB-INF/views/orders.jsp"
            ).forward(request, response);

        } catch (SQLException e) {

            getServletContext().log(
                "Unable to load orders for user "
                + user.getUserId(),
                e
            );

            throw new ServletException(
                "Unable to load your orders.",
                e
            );
        }
    }
}
