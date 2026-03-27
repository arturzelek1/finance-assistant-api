#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE app_db;
    CREATE USER app_user WITH ENCRYPTED PASSWORD 'app_pass';
    GRANT ALL PRIVILEGES ON DATABASE app_db TO app_user;
EOSQL
