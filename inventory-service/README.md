# inventory-service
This repository contains the source code of inventory management service.

### Environment variables

To run this project you must pass the following environment variables:

- `APPLICATION_PORT` -> port where application will run
- `JDBC_DATABASE_URL` -> JDBC connection string to PostgreSQL database
- `JDBC_DATABASE_USERNAME` -> username to connect to database
- `JDBC_DATABASE_PASSWORD` -> password to authenticate database connection
- `PRODUCT_SERVICE_URL` -> http address to product-service 