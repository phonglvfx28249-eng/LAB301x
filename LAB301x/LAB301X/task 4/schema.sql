DROP DATABASE IF EXISTS blockchain_trading;

CREATE DATABASE blockchain_trading
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE blockchain_trading;


CREATE TABLE users
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,

    full_name VARCHAR(100),

    role ENUM('ADMIN','USER')
        DEFAULT 'USER',

    status ENUM('ACTIVE','LOCKED')
        DEFAULT 'ACTIVE',

    last_login DATETIME NULL,

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE wallets
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    user_id BIGINT NOT NULL UNIQUE,

    available_balance DECIMAL(28,8)
        DEFAULT 100,

    locked_balance DECIMAL(28,8)
        DEFAULT 0,

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_wallet_user
        FOREIGN KEY(user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);


CREATE TABLE wallet_transactions
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    wallet_id BIGINT NOT NULL,

    order_id BIGINT NULL,

    trade_id BIGINT NULL,

    transaction_type ENUM
    (
        'REGISTER',
        'LOCK',
        'UNLOCK',
        'BUY',
        'SELL',
        'CANCEL'
    ),

    amount DECIMAL(28,8) NOT NULL,

    available_before DECIMAL(28,8),

    available_after DECIMAL(28,8),

    locked_before DECIMAL(28,8),

    locked_after DECIMAL(28,8),

    description VARCHAR(255),

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE orders
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    user_id BIGINT NOT NULL,

    side ENUM('BUY','SELL') NOT NULL,

    order_type ENUM('LIMIT','MARKET') NOT NULL,

    price DECIMAL(28,8),

    quantity DECIMAL(28,8) NOT NULL,

    remaining_quantity DECIMAL(28,8) NOT NULL,

    status ENUM
    (
        'OPEN',
        'PARTIAL',
        'FILLED',
        'CANCELLED'
    )
    DEFAULT 'OPEN',

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_order_user
        FOREIGN KEY(user_id)
        REFERENCES users(id)
);

CREATE TABLE trades
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    buy_order_id BIGINT NOT NULL,

    sell_order_id BIGINT NOT NULL,

    buyer_id BIGINT NOT NULL,

    seller_id BIGINT NOT NULL,

    trade_price DECIMAL(28,8) NOT NULL,

    quantity DECIMAL(28,8) NOT NULL,

    total_amount DECIMAL(28,8) NOT NULL,

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_trade_buy_order
        FOREIGN KEY(buy_order_id)
        REFERENCES orders(id),

    CONSTRAINT fk_trade_sell_order
        FOREIGN KEY(sell_order_id)
        REFERENCES orders(id),

    CONSTRAINT fk_trade_buyer
        FOREIGN KEY(buyer_id)
        REFERENCES users(id),

    CONSTRAINT fk_trade_seller
        FOREIGN KEY(seller_id)
        REFERENCES users(id)
);

CREATE TABLE blocks
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    block_index BIGINT NOT NULL,

    previous_hash VARCHAR(255),

    current_hash VARCHAR(255) NOT NULL,

    merkle_root VARCHAR(255),

    nonce BIGINT DEFAULT 0,

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE block_transactions
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    block_id BIGINT NOT NULL,

    trade_id BIGINT NOT NULL,

    CONSTRAINT fk_block_transaction_block
        FOREIGN KEY(block_id)
        REFERENCES blocks(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_block_transaction_trade
        FOREIGN KEY(trade_id)
        REFERENCES trades(id)
        ON DELETE CASCADE
);

CREATE TABLE audit_logs
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    user_id BIGINT,

    action VARCHAR(100),

    entity_name VARCHAR(100),

    entity_id BIGINT,

    description TEXT,

    ip_address VARCHAR(50),

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_audit_user
        FOREIGN KEY(user_id)
        REFERENCES users(id)
        ON DELETE SET NULL
);


ALTER TABLE wallet_transactions
ADD CONSTRAINT fk_wallet_transaction_wallet
FOREIGN KEY(wallet_id)
REFERENCES wallets(id);

ALTER TABLE wallet_transactions
ADD CONSTRAINT fk_wallet_transaction_order
FOREIGN KEY(order_id)
REFERENCES orders(id);

ALTER TABLE wallet_transactions
ADD CONSTRAINT fk_wallet_transaction_trade
FOREIGN KEY(trade_id)
REFERENCES trades(id);