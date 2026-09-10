CREATE DATABASE IF NOT EXISTS strategy_tuning_db;

USE strategy_tuning_db;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS asset_sale;
DROP TABLE IF EXISTS purchase_lot;
DROP TABLE IF EXISTS decisions;
DROP TABLE IF EXISTS wallet;
DROP TABLE IF EXISTS samples;
DROP TABLE IF EXISTS experiments;
DROP TABLE IF EXISTS sessions;
DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE accounts (
                          id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                          name VARCHAR(200) NOT NULL,
                          created_time DATETIME NOT NULL,
                          modified_time DATETIME NOT NULL,
                          PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE users (
                       id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                       first_name VARCHAR(50) NOT NULL,
                       middle_name VARCHAR(50),
                       last_name VARCHAR(50),
                       username VARCHAR(50) NOT NULL,
                       email VARCHAR(255) NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       account_id BIGINT UNSIGNED,
                       license_type VARCHAR(50) NOT NULL,
                       created_time DATETIME NOT NULL,
                       modified_time DATETIME NOT NULL,
                       PRIMARY KEY (id),
                       CONSTRAINT fk_users_accounts
                           FOREIGN KEY (account_id) REFERENCES accounts (id),
                       CONSTRAINT uq_users_username
                           UNIQUE (username),
                       CONSTRAINT uq_users_email
                           UNIQUE (email)
) ENGINE = InnoDB;

CREATE TABLE sessions (
                          id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                          user_id BIGINT UNSIGNED NOT NULL,
                          session_token VARCHAR(255) NOT NULL,
                          created_time DATETIME NOT NULL,
                          expires_time DATETIME NOT NULL,
                          modified_time DATETIME NOT NULL,
                          PRIMARY KEY (id),
                          CONSTRAINT fk_sessions_users
                              FOREIGN KEY (user_id) REFERENCES users (id),
                          CONSTRAINT uq_sessions_session_token
                              UNIQUE (session_token)
) ENGINE = InnoDB;

CREATE TABLE experiments (
                             id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                             name VARCHAR(200) NOT NULL,
                             agent_model VARCHAR(50) NOT NULL,
                             strategy_prompt VARCHAR(10000) NOT NULL,
                             sampling_window VARCHAR(50) NOT NULL,
                             experiment_start_time DATETIME NOT NULL,
                             experiment_end_time DATETIME NOT NULL,
                             experiment_status VARCHAR(50) NOT NULL,
                             created_time DATETIME NOT NULL,
                             created_user_id BIGINT UNSIGNED NOT NULL,
                             modified_time DATETIME NOT NULL,
                             PRIMARY KEY (id),
                             CONSTRAINT fk_experiments_created_user
                                 FOREIGN KEY (created_user_id) REFERENCES users (id)
) ENGINE = InnoDB;

CREATE TABLE samples (
                         id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                         experiment_id BIGINT UNSIGNED NOT NULL,
                         market_insights VARCHAR(10000),
                         sampling_time DATETIME NOT NULL,
                         sampling_status VARCHAR(50) NOT NULL,
                         created_time DATETIME NOT NULL,
                         modified_time DATETIME NOT NULL,
                         PRIMARY KEY (id),
                         CONSTRAINT fk_samples_experiment
                             FOREIGN KEY (experiment_id) REFERENCES experiments (id)
) ENGINE = InnoDB;

CREATE TABLE wallets (
                         id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                         experiment_id BIGINT UNSIGNED NOT NULL,
                         starting_money_amount DECIMAL(19, 4) NOT NULL,
                         opened_time DATETIME NOT NULL,
                         currency_code VARCHAR(3) NOT NULL,
                         created_time DATETIME NOT NULL,
                         modified_time DATETIME NOT NULL,
                         PRIMARY KEY (id),
                         CONSTRAINT fk_wallet_experiment
                             FOREIGN KEY (experiment_id) REFERENCES experiments (id),
                         CONSTRAINT uq_wallets_experiments_currency
                             UNIQUE (experiment_id, currency_code)
) ENGINE = InnoDB;

CREATE TABLE decisions (
                           id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                           sample_id BIGINT UNSIGNED NOT NULL,
                           decision_type VARCHAR(10) NOT NULL,
                           ticker VARCHAR(10) NOT NULL,
                           reasoning VARCHAR(10000) NOT NULL,
                           decision_time DATETIME NOT NULL,
                           created_time DATETIME NOT NULL,
                           modified_time DATETIME NOT NULL,
                           PRIMARY KEY (id),
                           CONSTRAINT fk_decisions_sample
                               FOREIGN KEY (sample_id) REFERENCES samples (id)
) ENGINE = InnoDB;

CREATE TABLE purchase_lots (
                               id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                               purchase_decision_id BIGINT UNSIGNED NOT NULL,
                               wallet_id BIGINT UNSIGNED NOT NULL,
                               ticker VARCHAR(10) NOT NULL,
                               purchase_price DECIMAL(19, 4) NOT NULL,
                               purchase_quantity DECIMAL(19, 4) NOT NULL,
                               purchase_time DATETIME NOT NULL,
                               created_time DATETIME NOT NULL,
                               modified_time DATETIME NOT NULL,
                               PRIMARY KEY (id),
                               CONSTRAINT fk_purchase_lot_purchase_decision
                                   FOREIGN KEY (purchase_decision_id) REFERENCES decisions (id),
                               CONSTRAINT fk_purchase_lot_wallet
                                   FOREIGN KEY (wallet_id) REFERENCES wallets (id)
) ENGINE = InnoDB;

CREATE TABLE asset_sales (
                             id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                             sale_decision_id BIGINT UNSIGNED NOT NULL,
                             purchase_lot_id BIGINT UNSIGNED NOT NULL,
                             ticker VARCHAR(10) NOT NULL,
                             sale_price DECIMAL(19, 4) NOT NULL,
                             sale_quantity DECIMAL(19, 4) NOT NULL,
                             sale_time DATETIME NOT NULL,
                             created_time DATETIME NOT NULL,
                             modified_time DATETIME NOT NULL,
                             PRIMARY KEY (id),
                             CONSTRAINT fk_asset_sale_sale_decision
                                 FOREIGN KEY (sale_decision_id) REFERENCES decisions (id),
                             CONSTRAINT fk_asset_sale_purchase_lot
                                 FOREIGN KEY (purchase_lot_id) REFERENCES purchase_lots (id)
) ENGINE = InnoDB;
