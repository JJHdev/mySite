CREATE SEQUENCE token_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE token (
                       tokenId NUMBER(19) PRIMARY KEY,
                       userId VARCHAR2(255) NOT NULL UNIQUE,
                       token VARCHAR2(255) NOT NULL,
                       expiration TIMESTAMP
);