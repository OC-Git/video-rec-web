# --- First database schema
 
# --- !Ups
 
CREATE TABLE Client (
  id			VARCHAR(255) NOT NULL, 
  created		TIMESTAMP NOT NULL, 
  username		VARCHAR(255) NOT NULL, 
  password		VARCHAR(255) NOT NULL,
  initial_state	VARCHAR(255) NOT NULL,
  ytUser		VARCHAR(255) NOT NULL, 
  ytPwd 		VARCHAR(255) NOT NULL
);

INSERT INTO Client VALUES ('videorec', SYSDATE(), NULL, NULL, NULL, 'joerg.viola@gmail.com', 'yDYf8PfWczURuKUPiuliUjPjZXO3uLIVP');
INSERT INTO Client VALUES ('66and33', SYSDATE(), NULL, NULL, NULL, 'joerg.viola@gmail.com', 'yDYf8PfWczURuKUPiuliUjPjZXO3uLIVP');
 
# --- !Downs
 
DROP TABLE IF EXISTS Client;
