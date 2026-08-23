CREATE DATABASE IF NOT EXISTS student_manager
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE student_manager;

CREATE TABLE IF NOT EXISTS student (
  id VARCHAR(32) PRIMARY KEY,
  name VARCHAR(64) NOT NULL,
  sex VARCHAR(8) NOT NULL,
  age INT NOT NULL,
  dept VARCHAR(64) NOT NULL,
  address VARCHAR(128) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO student (id, name, sex, age, dept, address) VALUES
  ('2022001', '张三', '男', 20, '电子信息工程', '1号宿舍楼'),
  ('2022002', '李四', '女', 19, '计算机科学与技术', '2号宿舍楼'),
  ('2022003', '王五', '男', 17, '电子信息工程', '3号宿舍楼')
ON DUPLICATE KEY UPDATE name = VALUES(name);
