
package com.fashionstore.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import com.fashionstore.dao.CategoryDAO;
import com.fashionstore.dao.ProductDAO;
import com.fashionstore.dao.impl.CategoryDAOImpl;
import com.fashionstore.dao.impl.ProductDAOImpl;
import com.fashionstore.model.Category;
import com.fashionstore.model.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/products")
public class ProductsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;

    private static final int PAGE_SIZE = 12;

    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAOImpl();
        categoryDAO = new CategoryDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        try {

            // 1. Read search keyword
            String keyword = request.getParameter("keyword");

            if (keyword != null) {
                keyword = keyword.trim();

                if (keyword.isEmpty()) {
                    keyword = null;
                }
            }

            // 2. Read category ID
            Integer categoryId = parseInteger(
                    request.getParameter("categoryId"));

            // 3. Read minimum and maximum price
            BigDecimal minPrice = parsePrice(
                    request.getParameter("minPrice"));

            BigDecimal maxPrice = parsePrice(
                    request.getParameter("maxPrice"));

            // 4. Read selected size
            String size = request.getParameter("size");

            if (size != null) {
                size = size.trim();

                if (size.isEmpty()) {
                    size = null;
                }
            }

            // 5. Read sorting option
            String requestedSort = request.getParameter("sortBy");

            if ("price_low_to_high".equals(requestedSort)
                    || "price_low".equals(requestedSort)) {

                requestedSort = "price_low_to_high";

            } else if ("price_high_to_low".equals(requestedSort)
                    || "price_high".equals(requestedSort)) {

                requestedSort = "price_high_to_low";

            } else {
                requestedSort = "newest";
            }

            // Convert sorting values to the values used by the DAO
            String daoSort = "newest";

            if ("price_low_to_high".equals(requestedSort)) {
                daoSort = "price_low";
            } else if ("price_high_to_low".equals(requestedSort)) {
                daoSort = "price_high";
            }

            // 6. Read current page
            Integer requestedPage = parseInteger(
                    request.getParameter("page"));

            int currentPage = requestedPage == null
                    ? 1 : requestedPage;

            // 7. Get all categories
            List<Category> categories =
                    categoryDAO.getAllCategories();

            // 8. Get total matching product count
            int totalProducts =
                    productDAO.getFilteredProductCount(
                            keyword,
                            categoryId,
                            minPrice,
                            maxPrice,
                            size);

            // 9. Calculate total pages
            int totalPages = Math.max(
                    1,
                    (int) Math.ceil(
                            (double) totalProducts / PAGE_SIZE));

            if (currentPage > totalPages) {
                currentPage = totalPages;
            }

            // 10. Calculate database offset
            int offset = (currentPage - 1) * PAGE_SIZE;

            // 11. Get filtered products
            List<Product> products =
                    productDAO.filterProducts(
                            keyword,
                            categoryId,
                            minPrice,
                            maxPrice,
                            size,
                            daoSort,
                            PAGE_SIZE,
                            offset);

            // 12. Send data to JSP
            request.setAttribute("products", products);
            request.setAttribute("categories", categories);

            request.setAttribute("keyword", keyword);
            request.setAttribute("categoryId", categoryId);
            request.setAttribute("minPrice", minPrice);
            request.setAttribute("maxPrice", maxPrice);
            request.setAttribute("size", size);
            request.setAttribute("sortBy", requestedSort);

            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalProducts", totalProducts);
            request.setAttribute("pageSize", PAGE_SIZE);

            // 13. Forward to products.jsp
            request.getRequestDispatcher(
                    "/WEB-INF/views/products.jsp")
                    .forward(request, response);

        } catch (SQLException e) {

            throw new ServletException(
                    "Unable to load products from database.", e);
        }
    }

    private Integer parseInteger(String value) {

        try {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }

            int number = Integer.parseInt(value.trim());

            return number > 0 ? number : null;

        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal parsePrice(String value) {

        try {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }

            BigDecimal price = new BigDecimal(value.trim());

            return price.signum() >= 0 ? price : null;

        } catch (NumberFormatException e) {
            return null;
        }
    }
}
