-- Order queries
-- @name: findAllOrders
SELECT id, user_id, username, total_amount, status, correlation_id, created_at 
FROM orders 
ORDER BY created_at DESC;

-- @name: findOrderById
SELECT id, user_id, username, total_amount, status, correlation_id, created_at 
FROM orders 
WHERE id = ?::uuid;

-- @name: findOrdersByUserId
SELECT id, user_id, username, total_amount, status, correlation_id, created_at 
FROM orders 
WHERE user_id = ?::uuid 
ORDER BY created_at DESC;

-- @name: insertOrder
INSERT INTO orders (user_id, username, total_amount, status, correlation_id) 
VALUES (?::uuid, ?, ?, ?, ?::uuid) 
RETURNING id, user_id, username, total_amount, status, correlation_id, created_at;

-- @name: updateOrderStatus
UPDATE orders 
SET status = ? 
WHERE id = ?::uuid 
RETURNING id, user_id, username, total_amount, status, correlation_id, created_at;

-- Product queries
-- @name: findAllProducts
SELECT id, name, description, price, stock 
FROM products 
ORDER BY name;

-- @name: findProductById
SELECT id, name, description, price, stock 
FROM products 
WHERE id = ?::uuid;

-- Order Items queries (Many-to-Many relationship)
-- @name: findOrderWithItems
SELECT o.id as order_id, o.user_id, o.username, o.total_amount, o.status, o.correlation_id, o.created_at,
       oi.id as item_id, oi.product_id, oi.quantity, oi.price as item_price,
       p.name as product_name, p.description as product_description
FROM orders o
LEFT JOIN order_items oi ON o.id = oi.order_id
LEFT JOIN products p ON oi.product_id = p.id
WHERE o.id = ?::uuid;

-- @name: findItemsByOrderId
SELECT oi.id, oi.order_id, oi.product_id, oi.quantity, oi.price,
       p.name as product_name, p.description as product_description
FROM order_items oi
JOIN products p ON oi.product_id = p.id
WHERE oi.order_id = ?::uuid;

-- @name: insertOrderItem
INSERT INTO order_items (order_id, product_id, quantity, price) 
VALUES (?::uuid, ?::uuid, ?, ?) 
RETURNING id, order_id, product_id, quantity, price;

-- @name: updateProductStock
UPDATE products 
SET stock = stock - ? 
WHERE id = ?::uuid AND stock >= ?;
