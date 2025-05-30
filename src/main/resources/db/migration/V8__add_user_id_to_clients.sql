ALTER TABLE clients ADD COLUMN user_id BIGINT;
ALTER TABLE clients ADD CONSTRAINT fk_client_user FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE clients ADD CONSTRAINT uq_client_user UNIQUE (user_id);
