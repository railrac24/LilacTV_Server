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
    name VARCHAR(80),
    email VARCHAR(80),
    mobile VARCHAR(30),
    password VARCHAR(80),
    reset_token VARCHAR(36),
    id TINYINT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    INDEX(email)
) engine=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS items (
    macaddeth0 VARCHAR(30),
    macaddwlan VARCHAR(30),
    ipadd VARCHAR(30),
    online BOOLEAN NOT NULL DEFAULT FALSE,
    tvheadend BOOLEAN NOT NULL DEFAULT FALSE,
    seqindex TINYINT(4),
    id TINYINT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    INDEX (macaddeth0),
    owner_id TINYINT(4) UNSIGNED NOT NULL DEFAULT 1,
    FOREIGN KEY (owner_id) REFERENCES users(id)
) engine=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS questions (
    writer_id TINYINT(4) UNSIGNED NOT NULL DEFAULT 1,
    FOREIGN KEY (writer_id) REFERENCES users(id),
    title VARCHAR(80),
    content TEXT(65535),
    create_date DATETIME,
    count_of_answers TINYINT(4) NOT NULL DEFAULT 0,
    id TINYINT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    INDEX(id)
) engine=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS answers (
    replier_id TINYINT(4) UNSIGNED NOT NULL DEFAULT 1,
    FOREIGN KEY (replier_id) REFERENCES users(id),
    question_id TINYINT(4) UNSIGNED NOT NULL DEFAULT 1,
    FOREIGN KEY (question_id) REFERENCES questions(id),
    content TEXT(65535),
    create_date DATETIME,
    id TINYINT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    INDEX(id)
) engine=InnoDB DEFAULT CHARSET=utf8;
