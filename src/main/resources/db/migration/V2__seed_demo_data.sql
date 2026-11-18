INSERT INTO trading_instruments (seccode, type, lot_size, min_price_step, currency, active)
VALUES ('SBER', 'STOCK', 10, 0.01, 'RUB', TRUE),
       ('OFZ26238', 'BOND', 1, 0.01, 'RUB', TRUE);

INSERT INTO accounts (user_id, currency, balance, reserved_balance)
VALUES (1001, 'RUB', 1000000.00, 0),
       (1002, 'RUB', 500000.00, 0);

INSERT INTO positions (account_id, instrument_id, quantity, reserved_quantity)
VALUES ((SELECT id FROM accounts WHERE user_id = 1001 AND currency = 'RUB'),
        (SELECT id FROM trading_instruments WHERE seccode = 'SBER'),
        1000,
        0),
       ((SELECT id FROM accounts WHERE user_id = 1001 AND currency = 'RUB'),
        (SELECT id FROM trading_instruments WHERE seccode = 'OFZ26238'),
        100,
        0),
       ((SELECT id FROM accounts WHERE user_id = 1002 AND currency = 'RUB'),
        (SELECT id FROM trading_instruments WHERE seccode = 'SBER'),
        500,
        0),
       ((SELECT id FROM accounts WHERE user_id = 1002 AND currency = 'RUB'),
        (SELECT id FROM trading_instruments WHERE seccode = 'OFZ26238'),
        200,
        0);
