CREATE DATABASE IF NOT EXISTS strategy_tuning_db;

USE strategy_tuning_db;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS asset_sale;
DROP TABLE IF EXISTS purchase_lot;
DROP TABLE IF EXISTS decisions;
DROP TABLE IF EXISTS wallet;
DROP TABLE IF EXISTS samples;
DROP TABLE IF EXISTS experiments;
DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE accounts (
                          id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                          name VARCHAR(200) NOT NULL,
                          created_at DATETIME NOT NULL,
                          modified_at DATETIME NOT NULL,
                          PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE users (
                       id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                       first_name VARCHAR(50) NOT NULL,
                       middle_name VARCHAR(50),
                       last_name VARCHAR(50),
                       account_id BIGINT UNSIGNED NOT NULL,
                       license_type VARCHAR(50) NOT NULL,
                       created_at DATETIME NOT NULL,
                       modified_at DATETIME NOT NULL,
                       PRIMARY KEY (id),
                       CONSTRAINT fk_users_accounts
                           FOREIGN KEY (account_id) REFERENCES users (id)
) ENGINE = InnoDB;

CREATE TABLE experiments (
                             id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                             name VARCHAR(200) NOT NULL,
                             agent_model VARCHAR(50) NOT NULL,
                             strategy_prompt VARCHAR(10000) NOT NULL,
                             sampling_window VARCHAR(50) NOT NULL,
                             starting_money_amount DECIMAL(19, 4) NOT NULL,
                             currency_code VARCHAR(3) NOT NULL,
                             experiment_start_time DATETIME NOT NULL,
                             experiment_end_time DATETIME NOT NULL,
                             experiment_status VARCHAR(50) NOT NULL,
                             created_time DATETIME NOT NULL,
                             created_by_user_id BIGINT UNSIGNED NOT NULL,
                             modified_time DATETIME NOT NULL,
                             PRIMARY KEY (id),
                             CONSTRAINT fk_experiments_created_by_user
                                 FOREIGN KEY (created_by_user_id) REFERENCES users (id)
) ENGINE = InnoDB;

CREATE TABLE samples (
                         id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                         experiment_id BIGINT UNSIGNED NOT NULL,
                         market_insights VARCHAR(10000) NOT NULL,
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
                         current_money_amount DECIMAL(19, 4) NOT NULL,
                         created_time DATETIME NOT NULL,
                         modified_time DATETIME NOT NULL,
                         PRIMARY KEY (id),
                         CONSTRAINT fk_wallet_experiment
                             FOREIGN KEY (experiment_id) REFERENCES experiments (id)
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
                               purchase_amount DECIMAL(19, 4) NOT NULL,
                               created_time DATETIME NOT NULL,
                               modified_time DATETIME NOT NULL,
                               PRIMARY KEY (id),
                               CONSTRAINT fk_purchase_lot_purchase_decision
                                   FOREIGN KEY (purchase_decision_id) REFERENCES decisions (id),
                               CONSTRAINT fk_purchase_lot_wallet
                                   FOREIGN KEY (wallet_id) REFERENCES wallet (id)
) ENGINE = InnoDB;

CREATE TABLE asset_sales (
                             id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                             sale_decision_id BIGINT UNSIGNED NOT NULL,
                             purchase_lot_id BIGINT UNSIGNED NOT NULL,
                             ticker VARCHAR(10) NOT NULL,
                             sale_price DECIMAL(19, 4) NOT NULL,
                             sale_amount DECIMAL(19, 4) NOT NULL,
                             created_time DATETIME NOT NULL,
                             modified_time DATETIME NOT NULL,
                             PRIMARY KEY (id),
                             CONSTRAINT fk_asset_sale_sale_decision
                                 FOREIGN KEY (sale_decision_id) REFERENCES decisions (id),
                             CONSTRAINT fk_asset_sale_purchase_lot
                                 FOREIGN KEY (purchase_lot_id) REFERENCES purchase_lot (id)
) ENGINE = InnoDB;
