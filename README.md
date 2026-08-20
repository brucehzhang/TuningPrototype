# Model Strategy Tuning Prototype

The purpose of this project is to create a platform for testing out AI agents that have been given a main strategy
prompt that dictates the trading strategy that it will try to execute against market data.

The primary execution context is an 'Experiment', which details the necessary details associated with the backtest. 

Ex: strategy, type of model, start and end timeframe, sampling rate, and more.

Work in progress ERD: https://lucid.app/lucidchart/8230a088-eed6-40c0-974c-9f7a30209e6b/edit?beaconFlowId=999610A4F87F935C&invitationId=inv_a412ad2a-3bfc-434f-a4de-720365221466&page=0_0#

# How to run locally

Ensure your environment has the following variables:

    - STRATEGY_TUNING_DB_ADMIN_USER
    - STRATEGY_TUNING_DB_ADMIN_PASS
    - APCA_TRADING_KEY_ID
    - APCA_TRADING_SECRET_KEY
    - SAMPLING_SCHEDULER_QUEUE_ARN
    - SCHEDULER_EXECUTION_ROLE_ARN
    - AWS_REGION

Make sure Docker installed so docker-compose can be used to create and run the image and container. 

Use AWS CLI v2 before running to make sure session is refreshed: aws login

To run:

    - docker-compose up --build

To stop:

    - docker-compose down