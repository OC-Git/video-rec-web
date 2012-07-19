# --- First database schema
 
# --- !Ups
 
CREATE TABLE Client (
  id			VARCHAR(255) NOT NULL, 
  created		TIMESTAMP NOT NULL, 
  ytUser		VARCHAR(255) NOT NULL, 
  ytPwd 		VARCHAR(255) NOT NULL, 
);

INSERT INTO Client VALUES ('videorec', SYSDATE(), 'joerg.viola@gmail.com', 'yDYf8PfWczURuKUPiuliUjPjZXO3uLIVP');
 
# --- !Downs
 
DROP TABLE IF EXISTS Client;
