CREATE TABLE IF NOT EXISTS traders
(
    trader_id  VARCHAR(64) PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS portfolio_positions
(
    id        BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    trader_id VARCHAR(64) NOT NULL,
    stock     VARCHAR(16) NOT NULL,
    sector    VARCHAR(32) NOT NULL,
    quantity  BIGINT      NOT NULL,
    CONSTRAINT uq_position_trader_stock UNIQUE (trader_id, stock),
    CONSTRAINT fk_positions_traders FOREIGN KEY (trader_id) REFERENCES traders (trader_id),
    CONSTRAINT chk_positions_quantity CHECK (quantity >= 0)
);

CREATE TABLE IF NOT EXISTS orders
(
    id         BINARY(16) PRIMARY KEY, -- MySQL does not have a native UUID type; handled via standard string representation
    trader_id  VARCHAR(64) NOT NULL,
    stock      VARCHAR(16) NOT NULL,
    sector     VARCHAR(32) NOT NULL,
    quantity   BIGINT      NOT NULL,
    side       VARCHAR(4)  NOT NULL,
    status     VARCHAR(12) NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_traders FOREIGN KEY (trader_id) REFERENCES traders (trader_id),
    CONSTRAINT chk_orders_quantity CHECK (quantity > 0),
    CONSTRAINT chk_orders_side CHECK (side IN ('BUY', 'SELL')),
    CONSTRAINT chk_orders_status CHECK (status IN ('PENDING', 'FILLED', 'CANCELLED'))
);

CREATE INDEX idx_orders_trader_status ON orders (trader_id, status);
CREATE INDEX idx_orders_trader_stock_status ON orders (trader_id, stock, status);
CREATE INDEX idx_positions_trader ON portfolio_positions (trader_id);
