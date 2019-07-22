# CREATE DATABASE IF NOT EXISTS users;
#
# ALTER DATABASE users
#   DEFAULT CHARACTER SET utf8
#   DEFAULT COLLATE utf8_general_ci;
#
# GRANT ALL PRIVILEGES ON users.* TO railrac@'%' IDENTIFIED BY 'railrac';
#
USE LilacTVDB;

CREATE TABLE IF NOT EXISTS users (
  first_name VARCHAR(30),
  last_name VARCHAR(30),
  email VARCHAR(255),
  password VARCHAR(80),
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  INDEX(email)
) engine=InnoDB;
