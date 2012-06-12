# --- First database schema
 
# --- !Ups
 
CREATE TABLE Video (
  id			SERIAL PRIMARY KEY,
  client		VARCHAR(255) NOT NULL, 
  date			DATETIME NOT NULL, 
  title			VARCHAR(255) NOT NULL, 
  page			VARCHAR(255) NOT NULL, 
  category		VARCHAR(255) NOT NULL, 
  description	VARCHAR(255) NOT NULL, 
  publishedId		VARCHAR(255) NOT NULL
);
 
# --- !Downs
 
DROP TABLE IF EXISTS Video;
