
package com.fashionstore.controller;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.fashionstore.dao.UserDAO;
import com.fashionstore.dao.impl.UserDAOImpl;
import com.fashionstore.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAOImpl();
    }

    // Display login page
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                "/WEB-INF/views/login.jsp"
        ).forward(request, response);
    }

    // Process login form
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Validate input
        if (email == null || email.trim().isEmpty()
                || password == null || password.isEmpty()) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/login?error=empty"
            );
            return;
        }

        email = email.trim().toLowerCase();

        try {

            // Find user using email
            User user = userDAO.getUserByEmail(email);

            // Check whether user exists
            if (user == null) {

                response.sendRedirect(
                        request.getContextPath()
                        + "/login?error=invalid"
                );
                return;
            }

            // Verify entered password
            boolean passwordMatches =
                    verifyPassword(password, user.getPassword());

            if (!passwordMatches) {

                response.sendRedirect(
                        request.getContextPath()
                        + "/login?error=invalid"
                );
                return;
            }

            // Create session after successful login
            HttpSession session = request.getSession();

            // Prevent session fixation
            request.changeSessionId();

            // Do not keep the password hash in the session object
            user.setPassword(null);

            session.setAttribute("loggedInUser", user);

            // Redirect to home page
            response.sendRedirect(
                    request.getContextPath() + "/home"
            );

        } catch (Exception e) {

            throw new ServletException(
                    "Unable to login user", e
            );
        }
    }

    // Verify PBKDF2 password
    private boolean verifyPassword(String enteredPassword,
                                   String storedPassword)
            throws Exception {

        if (storedPassword == null) {
            return false;
        }

        String[] parts = storedPassword.split("\\$");

        if (parts.length != 4 || !"PBKDF2".equals(parts[0])) {
            return false;
        }

        int iterations;
        byte[] salt;
        byte[] storedHash;

        try {

            iterations = Integer.parseInt(parts[1]);

            if (iterations <= 0 || iterations > 1000000) {
                return false;
            }

            salt = Base64.getDecoder().decode(parts[2]);
            storedHash = Base64.getDecoder().decode(parts[3]);

            if (salt.length != 16 || storedHash.length != 32) {
                return false;
            }

        } catch (IllegalArgumentException e) {
            return false;
        }

        PBEKeySpec spec = new PBEKeySpec(
                enteredPassword.toCharArray(),
                salt,
                iterations,
                storedHash.length * 8
        );

        try {

            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256"
                    );

            byte[] enteredHash =
                    factory.generateSecret(spec).getEncoded();

            return MessageDigest.isEqual(
                    storedHash,
                    enteredHash
            );

        } finally {
            spec.clearPassword();
        }
    }
}
