-- ============================================================================
-- Seed script for tuning_prototype test data
-- Insert order respects FK dependencies (parents before children):
--   accounts -> users -> experiments -> samples -> wallets
--   -> decisions -> purchase_lots -> asset_sales
--
-- Enum values below are confirmed against the actual AgentModel, ExperimentStatus,
-- SamplingWindow, SamplingStatus, DecisionType, and LicenseType enum source files.
-- ============================================================================

SET FOREIGN_KEY_CHECKS = 0;

-- Clean slate (children first) so this script is safely re-runnable
DELETE FROM asset_sales;
DELETE FROM purchase_lots;
DELETE FROM decisions;
DELETE FROM wallets;
DELETE FROM samples;
DELETE FROM experiments;
DELETE FROM users;
DELETE FROM accounts;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================================
-- ACCOUNTS
-- ============================================================================
INSERT INTO accounts (id, name, created_time, modified_time) VALUES
                                                             (1, 'Acme Trading Research', '2026-01-01 09:00:00', '2026-01-01 09:00:00'),
                                                             (2, 'Solo Backtest Labs',    '2026-01-05 09:00:00', '2026-01-05 09:00:00');

-- ============================================================================
-- USERS
-- ============================================================================
INSERT INTO users (id, first_name, middle_name, last_name, account_id, license_type, created_time, modified_time) VALUES
                                                                                                                  (1, 'Jane',  NULL,   'Doe',     1, 'ENTERPRISE', '2026-01-01 09:05:00', '2026-01-01 09:05:00'),
                                                                                                                  (2, 'Sam',   'R.',   'Nguyen',  1, 'PREMIUM',    '2026-01-02 10:00:00', '2026-01-02 10:00:00'),
                                                                                                                  (3, 'Alex',  NULL,   NULL,      2, 'FREE',       '2026-01-05 09:10:00', '2026-01-05 09:10:00');

-- ============================================================================
-- EXPERIMENTS
-- ============================================================================
INSERT INTO experiments (
    id, name, agent_model, strategy_prompt, sampling_window,
    experiment_start_time, experiment_end_time, experiment_status,
    created_time, created_user_id, modified_time
) VALUES
      (1, 'Tech Growth Momentum',
       'CLAUDE_OPUS_4_7',
       'You are a stock trader with a focus on the technology sector. You typically trade based off of long-term growth and R&D investment from earnings reports, but make judgments for holding, selling, or buying in through sentiment in current events.',
       'DAYS_1',
       '2025-01-01 00:00:00', '2025-06-30 00:00:00', 'COMPLETED',
       '2026-01-01 10:00:00', 1, '2026-01-01 10:00:00'),

      (2, 'Energy Sector Value Play',
       'CLAUDE_SONNET_4_6',
       'You are a value-oriented trader focused on the energy sector. Prioritize undervalued companies with strong free cash flow and be conservative with position sizing.',
       'HOURS_4',
       '2025-02-01 00:00:00', '2025-08-01 00:00:00', 'IN_PROGRESS',
       '2026-01-02 11:00:00', 2, '2026-01-02 11:00:00'),

      (3, 'Quick Sentiment Scalper',
       'CLAUDE_HAIKU_4_5',
       'You are a short-term trader reacting to breaking news sentiment. Favor small, frequent trades over long holds.',
       'MINUTES_30',
       '2025-03-01 00:00:00', '2025-03-15 00:00:00', 'DRAFT',
       '2026-01-05 09:30:00', 3, '2026-01-05 09:30:00');

-- ============================================================================
-- SAMPLES
-- ============================================================================
INSERT INTO samples (id, experiment_id, market_insights, sampling_time, sampling_status, created_time, modified_time) VALUES
                                                                                                                          (1, 1, 'NVDA reported record data center revenue, up 35% YoY. Analyst sentiment strongly positive on AI infrastructure spend.', '2025-01-15 09:30:00', 'COMPLETED', '2026-01-01 10:05:00', '2026-01-01 10:05:00'),
                                                                                                                          (2, 1, 'MSFT announced increased capex guidance for cloud/AI buildout; stock dipped slightly on near-term margin concerns.', '2025-02-15 09:30:00', 'COMPLETED', '2026-01-01 10:06:00', '2026-01-01 10:06:00'),
                                                                                                                          (3, 1, 'Broader tech sector pullback amid rate hike concerns; no company-specific news of note.', '2025-03-15 09:30:00', 'COMPLETED', '2026-01-01 10:07:00', '2026-01-01 10:07:00'),
                                                                                                                          (4, 2, 'XOM beat earnings on higher refining margins; management raised full-year guidance.', '2025-02-08 09:30:00', 'COMPLETED', '2026-01-02 11:05:00', '2026-01-02 11:05:00'),
                                                                                                                          (5, 2, 'Oil prices dipped on OPEC+ supply increase announcement; energy sector broadly lower.', '2025-02-15 09:30:00', 'COMPLETED', '2026-01-02 11:06:00', '2026-01-02 11:06:00'),
                                                                                                                          (6, 3, 'Breaking: unexpected Fed commentary triggers short-term volatility spike across indices.', '2025-03-01 14:00:00', 'IN_PROGRESS', '2026-01-05 09:35:00', '2026-01-05 09:35:00');

-- ============================================================================
-- WALLETS
-- ============================================================================
INSERT INTO wallets (id, experiment_id, starting_money_amount, opened_time, currency_code, created_time, modified_time) VALUES
                                                                                               (1, 1, 87500.5000,  '2026-01-01 10:20:00', 'USD','2026-01-01 10:05:00', '2026-01-01 10:20:00'),
                                                                                               (2, 2, 51250.0000,  '2026-01-01 10:20:00','USD','2026-01-02 11:05:00', '2026-01-02 11:15:00'),
                                                                                               (3, 3, 10000.0000,  '2026-01-01 10:20:00','USD','2026-01-05 09:30:00', '2026-01-05 09:30:00');

-- ============================================================================
-- DECISIONS
-- ============================================================================
INSERT INTO decisions (id, sample_id, decision_type, ticker, reasoning, decision_time, created_time, modified_time) VALUES
                                                                                                                        (1, 1, 'BUY',  'NVDA', 'Strong data center growth and positive AI infrastructure sentiment support a long-term growth position.', '2025-01-15 09:35:00', '2026-01-01 10:05:30', '2026-01-01 10:05:30'),
                                                                                                                        (2, 2, 'HOLD', 'MSFT', 'Increased capex signals continued AI investment despite short-term margin pressure; maintain existing position.', '2025-02-15 09:35:00', '2026-01-01 10:06:30', '2026-01-01 10:06:30'),
                                                                                                                        (3, 3, 'SELL', 'NVDA', 'Broader macro pullback and rate concerns warrant trimming exposure to lock in gains.', '2025-03-15 09:35:00', '2026-01-01 10:07:30', '2026-01-01 10:07:30'),
                                                                                                                        (4, 4, 'BUY',  'XOM',  'Earnings beat and raised guidance support increasing position at attractive valuation.', '2025-02-08 09:35:00', '2026-01-02 11:05:30', '2026-01-02 11:05:30'),
                                                                                                                        (5, 5, 'HOLD', 'XOM',  'Short-term oil price dip is macro-driven, not company-specific; maintain position.', '2025-02-15 09:35:00', '2026-01-02 11:06:30', '2026-01-02 11:06:30'),
                                                                                                                        (6, 6, 'HOLD', 'SPY',  'Volatility spike appears transient; awaiting confirmation before acting.', '2025-03-01 14:05:00', '2026-01-05 09:35:30', '2026-01-05 09:35:30');

-- ============================================================================
-- PURCHASE_LOTS (only resulting from BUY decisions: decisions 1 and 4)
-- ============================================================================
INSERT INTO purchase_lots (id, purchase_decision_id, wallet_id, ticker, purchase_price, purchase_quantity, purchase_time, created_time, modified_time) VALUES
                                                                                                                                          (1, 1, 1, 'NVDA', 125.4000, 100.0000, '2026-01-01 10:05:35', '2026-01-01 10:05:35', '2026-01-01 10:05:35'),
                                                                                                                                          (2, 4, 2, 'XOM',  108.2000, 50.0000,  '2026-01-02 11:05:35', '2026-01-02 11:05:35', '2026-01-02 11:05:35');

-- ============================================================================
-- ASSET_SALES (resulting from SELL decisions: decision 3 sells part of lot 1)
-- ============================================================================
INSERT INTO asset_sales (id, sale_decision_id, purchase_lot_id, ticker, sale_price, sale_quantity, sale_time, created_time, modified_time) VALUES
    (1, 3, 1, 'NVDA', 142.7500, 60.0000, '2026-01-01 10:07:35', '2026-01-01 10:07:35', '2026-01-01 10:07:35');

-- ============================================================================
-- Sanity check queries (optional — comment out if not needed)
-- ============================================================================
-- SELECT * FROM accounts;
-- SELECT * FROM users;
-- SELECT * FROM experiments;
-- SELECT * FROM samples;
-- SELECT * FROM wallets;
-- SELECT * FROM decisions;
-- SELECT * FROM purchase_lots;
-- SELECT * FROM asset_sales;