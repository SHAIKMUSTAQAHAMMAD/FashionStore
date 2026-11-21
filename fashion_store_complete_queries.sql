-- FashionStore: reconstructed, reusable database setup from supplied screenshots.
-- NOT an exact log of the original SQL queries.
-- PUBLIC-SAFE: contains product/catalog demo data only, no customer accounts or orders.
-- IMPORTANT: Run only on a NEW/EMPTY fashion_store database. Do NOT run on your existing database.
-- Back up your existing database before importing anything.

-- 1. CREATE DATABASE
CREATE DATABASE IF NOT EXISTS fashion_store
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE fashion_store;

-- 2. CREATE TABLES (8 tables, in foreign-key-safe order)
CREATE TABLE users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  mobile VARCHAR(20),
  address VARCHAR(255),
  city VARCHAR(100),
  state VARCHAR(100),
  pincode VARCHAR(12),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE categories (
  category_id INT AUTO_INCREMENT PRIMARY KEY,
  category_name VARCHAR(100) NOT NULL,
  description VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE products (
  product_id INT AUTO_INCREMENT PRIMARY KEY,
  category_id INT NOT NULL,
  product_name VARCHAR(150) NOT NULL,
  description TEXT,
  price DECIMAL(10,2) NOT NULL,
  image_url VARCHAR(500),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(category_id)
) ENGINE=InnoDB;

CREATE TABLE product_sizes (
  size_id INT AUTO_INCREMENT PRIMARY KEY,
  product_id INT NOT NULL,
  size VARCHAR(20) NOT NULL,
  stock_quantity INT NOT NULL DEFAULT 0,
  CONSTRAINT uq_product_size UNIQUE (product_id, size),
  CONSTRAINT fk_sizes_product FOREIGN KEY (product_id) REFERENCES products(product_id)
) ENGINE=InnoDB;

CREATE TABLE carts (
  cart_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT uq_cart_user UNIQUE (user_id),
  CONSTRAINT fk_carts_user FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB;

CREATE TABLE cart_items (
  cart_item_id INT AUTO_INCREMENT PRIMARY KEY,
  cart_id INT NOT NULL,
  size_id INT NOT NULL,
  quantity INT NOT NULL DEFAULT 1,
  CONSTRAINT uq_cart_size UNIQUE (cart_id, size_id),
  CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts(cart_id),
  CONSTRAINT fk_cart_items_size FOREIGN KEY (size_id) REFERENCES product_sizes(size_id)
) ENGINE=InnoDB;

CREATE TABLE orders (
  order_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  total_amount DECIMAL(10,2) NOT NULL,
  delivery_address VARCHAR(255),
  delivery_city VARCHAR(100),
  delivery_state VARCHAR(100),
  delivery_pincode VARCHAR(12),
  payment_method VARCHAR(30),
  payment_status VARCHAR(30) DEFAULT 'PENDING',
  order_status VARCHAR(30) DEFAULT 'PLACED',
  order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB;

CREATE TABLE order_items (
  order_item_id INT AUTO_INCREMENT PRIMARY KEY,
  order_id INT NOT NULL,
  product_id INT NOT NULL,
  product_name VARCHAR(150) NOT NULL,
  size VARCHAR(20),
  quantity INT NOT NULL,
  unit_price DECIMAL(10,2) NOT NULL,
  CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(order_id),
  CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(product_id)
) ENGINE=InnoDB;

-- 3. INSERT PUBLIC CATALOG DATA (visible in screenshots)
INSERT INTO categories (category_id, category_name, description) VALUES
(1,'Mens Clothing','Clothing collection for men'),
(2,'Womens Clothing','Clothing collection for women'),
(3,'Footwear','Shoes and sandals'),
(4,'Watches','Fashion and wrist watches'),
(5,'Accessories','Belts, handbags and other accessories');

INSERT INTO products (product_id, category_id, product_name, description, price, image_url) VALUES
(1,1,'Black Cotton T-Shirt','Regular fit black cotton T-shirt',599.00,'assets/images/products/black-tshirt.jpg'),
(2,1,'White Cotton T-Shirt','Regular fit white cotton T-shirt',599.00,'assets/images/products/white-tshirt.jpg'),
(3,1,'Blue Denim Jeans','Slim fit blue denim jeans',1299.00,'assets/images/products/blue-jeans.jpg'),
(4,1,'Grey Hoodie','Comfortable grey winter hoodie',1599.00,'assets/images/products/grey-hoodie.jpg'),
(5,2,'Floral Summer Dress','Beautiful floral printed summer dress',1799.00,'assets/images/products/floral-dress.jpg'),
(6,2,'Black Cotton Kurti','Elegant black cotton kurti',999.00,'assets/images/products/black-kurti.jpg'),
(7,2,'Pink Casual Top','Stylish pink casual top',799.00,'assets/images/products/pink-top.jpg'),
(8,3,'White Sneakers','Casual white sneakers',2499.00,'assets/images/products/white-sneakers.jpg'),
(9,3,'Brown Casual Sandals','Comfortable brown sandals',899.00,'assets/images/products/brown-sandals.jpg'),
(10,4,'Silver Analog Watch','Classic silver stainless steel watch',2999.00,'assets/images/products/silver-watch.jpg'),
(11,4,'Black Digital Watch','Modern black digital watch',3499.00,'assets/images/products/black-watch.jpg'),
(12,5,'Brown Leather Belt','Classic brown leather belt',499.00,'assets/images/products/brown-belt.jpg'),
(13,5,'Tan Handbag','Stylish tan handbag',1899.00,'assets/images/products/tan-handbag.jpg');

-- 4. INSERT 65 SIZE/STOCK ROWS (5 sizes per product as shown)
-- In the screenshots, some L sizes have stock 9 after purchases; most stock is 10.
INSERT INTO product_sizes (product_id, size, stock_quantity)
SELECT p.product_id, s.size,
       CASE WHEN p.product_id IN (1,3,9,10,12) AND s.size='L' THEN 9 ELSE 10 END
FROM products AS p
CROSS JOIN (
  SELECT 'XXL' AS size UNION ALL SELECT 'XL' UNION ALL SELECT 'L'
  UNION ALL SELECT 'M' UNION ALL SELECT 'S'
) AS s;

-- 5. SENSITIVE TABLES: intentionally NO real customer data is inserted.
-- users: personal names, email addresses, password hashes, phones, addresses.
-- carts/cart_items: customer shopping activity.
-- orders/order_items: addresses, payment status, purchase history.
-- Register a test account through your application to populate these tables safely.

-- 6. READ/VERIFY DATA (safe queries)
SHOW TABLES;
SELECT * FROM categories;
SELECT product_id, category_id, product_name, price, image_url FROM products ORDER BY product_id;
SELECT product_id, size, stock_quantity FROM product_sizes ORDER BY product_id, size;
SELECT p.product_name, c.category_name, p.price
FROM products p JOIN categories c ON c.category_id=p.category_id
ORDER BY p.product_id;
SELECT p.product_name, s.size, s.stock_quantity
FROM product_sizes s JOIN products p ON p.product_id=s.product_id
ORDER BY p.product_id, s.size;
SELECT p.product_name, SUM(s.stock_quantity) AS total_stock
FROM products p JOIN product_sizes s ON s.product_id=p.product_id
GROUP BY p.product_id, p.product_name ORDER BY p.product_id;

-- 7. OPTIONAL EXAMPLES (COMMENTED OUT: do not execute accidentally)
-- Example UPDATE: change stock for one product/size:
-- UPDATE product_sizes SET stock_quantity=10 WHERE product_id=1 AND size='M';
-- Example UPDATE: change product image path:
-- UPDATE products SET image_url='assets/images/products/black-tshirt.jpg' WHERE product_id=1;
-- Example DELETE: remove a cart item by ID (only after a cart exists):
-- DELETE FROM cart_items WHERE cart_item_id=1;
-- Example ORDER queries (may return no rows until an order is placed):
-- SELECT * FROM orders ORDER BY order_date DESC;
-- SELECT * FROM order_items ORDER BY order_item_id;
-- Example JOIN for order history:
-- SELECT o.order_id, o.order_date, o.total_amount, oi.product_name, oi.size, oi.quantity, oi.unit_price
-- FROM orders o JOIN order_items oi ON oi.order_id=o.order_id ORDER BY o.order_id, oi.order_item_id;
