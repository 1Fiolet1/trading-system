CREATE TABLE trading_instruments
(
    id             BIGSERIAL PRIMARY KEY,
    seccode        VARCHAR(32)    NOT NULL UNIQUE,
    type           VARCHAR(16)    NOT NULL,
    lot_size       BIGINT         NOT NULL,
    min_price_step NUMERIC(19, 8) NOT NULL,
    currency       VARCHAR(3)     NOT NULL,
    active         BOOLEAN        NOT NULL DEFAULT TRUE
);

CREATE TABLE accounts
(
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT         NOT NULL,
    currency         VARCHAR(3)     NOT NULL,
    balance          NUMERIC(19, 8) NOT NULL DEFAULT 0,
    reserved_balance NUMERIC(19, 8) NOT NULL DEFAULT 0
);

CREATE TABLE positions
(
    id                BIGSERIAL PRIMARY KEY,
    account_id        BIGINT NOT NULL,
    instrument_id     BIGINT NOT NULL,
    quantity          BIGINT NOT NULL DEFAULT 0,
    reserved_quantity BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE orders
(
    id                 BIGSERIAL PRIMARY KEY,
    account_id         BIGINT         NOT NULL,
    instrument_id      BIGINT         NOT NULL,
    side               VARCHAR(8)     NOT NULL,
    price              NUMERIC(19, 8) NOT NULL,
    initial_quantity   BIGINT         NOT NULL,
    remaining_quantity BIGINT         NOT NULL,
    status             VARCHAR(32)    NOT NULL,
    created_at         TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE trades
(
    id            BIGSERIAL PRIMARY KEY,
    instrument_id BIGINT         NOT NULL,
    buy_order_id  BIGINT         NOT NULL,
    sell_order_id BIGINT         NOT NULL,
    price         NUMERIC(19, 8) NOT NULL,
    quantity      BIGINT         NOT NULL,
    executed_at   TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
