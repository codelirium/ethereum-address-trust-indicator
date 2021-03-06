-- Requires PostgreSQL 9.5

CREATE TABLE IF NOT EXISTS ETHEREUM_BLOCK_TRANSACTION (
	ID                      BIGSERIAL PRIMARY KEY CONSTRAINT no_null NOT NULL,
	BLOCK_ID                BIGINT                CONSTRAINT no_null NOT NULL,
	SOURCE_ADDRESS          TEXT                  CONSTRAINT no_null NOT NULL,
	DESTINATION_ADDRESS     TEXT                  CONSTRAINT no_null NOT NULL,
	IS_SOURCE_CONTRACT      BOOLEAN               CONSTRAINT no_null NOT NULL,
	IS_DESTINATION_CONTRACT BOOLEAN               CONSTRAINT no_null NOT NULL
);
