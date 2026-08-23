CREATE DATABASE IF NOT EXISTS mydb DEFAULT CHARACTER SET utf8mb4;
USE mydb;

CREATE TABLE IF NOT EXISTS student (
    id VARCHAR(32) PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    sex VARCHAR(16) NOT NULL,
    age INT NOT NULL,
    dept VARCHAR(64) NOT NULL,
    address VARCHAR(255) DEFAULT ''
);

INSERT INTO student (id, name, sex, age, dept, address) VALUES
('S2026001', '示例学生', '男', 21, '电子信息工程', '连云港')
ON DUPLICATE KEY UPDATE name = VALUES(name);
