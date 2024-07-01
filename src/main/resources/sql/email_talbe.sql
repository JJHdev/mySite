CREATE TABLE email_verification (
    email_id NUMBER PRIMARY KEY,
    email VARCHAR2(255) UNIQUE NOT NULL,
    email_status NUMBER(1) DEFAULT 0 NOT NULL,
    code VARCHAR2(6),
    expiration TIMESTAMP
);

CREATE SEQUENCE email_verification_seq START WITH 1 INCREMENT BY 1;