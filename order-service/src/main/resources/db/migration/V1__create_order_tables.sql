-- Create orders table
CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT uuidv7(),
    user_id UUID NOT NULL,
    username VARCHAR(100),
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    correlation_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create products table
CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT uuidv7(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0
);

-- Create order_items table (many-to-many between orders and products)
CREATE TABLE order_items (
    id UUID PRIMARY KEY DEFAULT uuidv7(),
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Insert sample products
INSERT INTO products (name, description, price, stock) VALUES 
    ('Laptop', 'High-performance laptop', 1299.99, 50),
    ('Mouse', 'Wireless gaming mouse', 79.99, 200),
    ('Keyboard', 'Mechanical keyboard', 149.99, 150),
    ('Monitor', '27-inch 4K monitor', 449.99, 75),
    ('Headphones', 'Noise-cancelling headphones', 299.99, 100);

-- Create indexes
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_correlation_id ON orders(correlation_id);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);
