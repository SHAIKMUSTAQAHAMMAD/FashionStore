
package com.fashionstore.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fashionstore.dao.ProductDAO;
import com.fashionstore.dao.ProductSizeDAO;
import com.fashionstore.dao.impl.ProductDAOImpl;
import com.fashionstore.dao.impl.ProductSizeDAOImpl;

import com.fashionstore.model.Product;
import com.fashionstore.model.ProductSize;

@WebServlet("/product-details")
public class ProductDetailsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ProductDAO productDAO;
    private ProductSizeDAO productSizeDAO;

    @Override
    public void init() throws ServletException {

        productDAO = new ProductDAOImpl();
        productSizeDAO = new ProductSizeDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String id = request.getParameter("id");

        if (id == null || id.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/products");
            return;
        }

        try {

            int productId = Integer.parseInt(id);

            if (productId <= 0) {
                response.sendRedirect(request.getContextPath() + "/products");
                return;
            }

            Product product = productDAO.getProductById(productId);

            if (product == null) {
                response.sendRedirect(request.getContextPath() + "/products");
                return;
            }

            // Retrieve sizes belonging to this product only
            List<ProductSize> productSizes =
                    productSizeDAO.getSizesByProductId(productId);

            if (productSizes == null) {
                throw new ServletException("Unable to retrieve product sizes.");
            }

            int totalStock = 0;

            for (ProductSize size : productSizes) {

                if (size.getStockQuantity() > 0) {
                    totalStock += size.getStockQuantity();
                }
            }

            boolean inStock = totalStock > 0;

            request.setAttribute("product", product);
            request.setAttribute("productSizes", productSizes);
            request.setAttribute("totalStock", totalStock);
            request.setAttribute("inStock", inStock);

            request.getRequestDispatcher(
                    "/WEB-INF/views/product-details.jsp")
                    .forward(request, response);

        } catch (NumberFormatException e) {

            response.sendRedirect(request.getContextPath() + "/products");

        } catch (Exception e) {

            throw new ServletException(
                    "Unable to load product details.", e);
        }
    }
}
