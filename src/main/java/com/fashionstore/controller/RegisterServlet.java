
package com.fashionstore.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.SQLException;
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

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private UserDAO userDAO;

    private static final int ITERATIONS = 120000;
    private static final int KEY_LENGTH = 256;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAOImpl();
    }

    // Display registration page
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                "/WEB-INF/views/register.jsp"
        ).forward(request, response);
    }

    // Process registration form
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String mobile = request.getParameter("mobile");
        String address = request.getParameter("address");
        String city = request.getParameter("city");
        String state = request.getParameter("state");
        String pincode = request.getParameter("pincode");

        // Validate required fields
        if (isEmpty(name) || isEmpty(email)
                || isEmpty(password) || isEmpty(mobile)) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/register?error=empty"
            );
            return;
        }

        name = name.trim();
        email = email.trim().toLowerCase();
        mobile = mobile.trim();

        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/register?error=email"
            );
            return;
        }

        // Validate password
        if (password.length() < 8) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/register?error=password"
            );
            return;
        }

        // Validate mobile number
        if (!mobile.matches("[0-9]{10,15}")) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/register?error=mobile"
            );
            return;
        }

        try {

            // Check whether email already exists
            if (userDAO.isEmailExists(email)) {

                response.sendRedirect(
                        request.getContextPath()
                        + "/register?error=exists"
                );
                return;
            }

            // Hash password
            String hashedPassword = hashPassword(password);

            // Create User object
            User user = new User();

            user.setName(name);
            user.setEmail(email);
            user.setPassword(hashedPassword);
            user.setMobile(mobile);
            user.setAddress(address);
            user.setCity(city);
            user.setState(state);
            user.setPincode(pincode);

            // Save user in database
            boolean registered = userDAO.registerUser(user);

            if (registered) {

                response.sendRedirect(
                        request.getContextPath()
                        + "/login?registered=true"
                );

            } else {

                response.sendRedirect(
                        request.getContextPath()
                        + "/register?error=failed"
                );
            }

        } catch (SQLException e) {

            throw new ServletException(
                    "Unable to register user", e
            );
        }
    }

    // Check empty values
    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    // Generate secure password hash
    private String hashPassword(String password)
            throws ServletException {

        try {

            byte[] salt = new byte[16];

            SecureRandom random = new SecureRandom();
            random.nextBytes(salt);

            PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    ITERATIONS,
                    KEY_LENGTH
            );

            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256"
                    );

            byte[] hash = factory.generateSecret(spec).getEncoded();

            spec.clearPassword();

            String encodedSalt =
                    Base64.getEncoder().encodeToString(salt);

            String encodedHash =
                    Base64.getEncoder().encodeToString(hash);

            return "PBKDF2$" + ITERATIONS
                    + "$" + encodedSalt
                    + "$" + encodedHash;

        } catch (Exception e) {

            throw new ServletException(
                    "Password hashing failed", e
            );
        }
    }
}
