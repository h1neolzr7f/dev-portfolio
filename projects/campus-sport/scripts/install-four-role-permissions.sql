USE `sport-manage`;

SET NAMES utf8mb4;

UPDATE sys_role SET name = '系统管理员', deleted = 0 WHERE flag = 'ADMIN';
UPDATE sys_role SET name = '普通学生或教职工', deleted = 0 WHERE flag = 'members';

INSERT INTO sys_role (name, flag, deleted, create_time)
SELECT '场馆主管', 'venue_manager', 0, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE flag = 'venue_manager');

INSERT INTO sys_role (name, flag, deleted, create_time)
SELECT '体育教师或社团负责人', 'teacher', 0, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE flag = 'teacher');

UPDATE sys_role SET name = '场馆主管', deleted = 0 WHERE flag = 'venue_manager';
UPDATE sys_role SET name = '体育教师或社团负责人', deleted = 0 WHERE flag = 'teacher';

DELETE rp
FROM sys_role_permission rp
LEFT JOIN sys_role r ON r.id = rp.role_id
WHERE r.id IS NULL;

SET @admin_role := (SELECT id FROM sys_role WHERE flag = 'ADMIN' LIMIT 1);
SET @venue_role := (SELECT id FROM sys_role WHERE flag = 'venue_manager' LIMIT 1);
SET @teacher_role := (SELECT id FROM sys_role WHERE flag = 'teacher' LIMIT 1);
SET @member_role := (SELECT id FROM sys_role WHERE flag = 'members' LIMIT 1);

DELETE FROM sys_role_permission
WHERE role_id IN (@admin_role, @venue_role, @teacher_role, @member_role);

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT @admin_role, id
FROM sys_permission
WHERE deleted = 0;

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT @venue_role, id
FROM sys_permission
WHERE deleted = 0
  AND (
    id IN (12, 186, 505, 1041, 1049, 1057, 1065, 1073, 1089, 1097, 1105, 1113, 1137, 1145, 1151)
    OR pid IN (505, 1041, 1049, 1057, 1065, 1073, 1089, 1097, 1105, 1113, 1137, 1145, 1151)
  );

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT @teacher_role, id
FROM sys_permission
WHERE deleted = 0
  AND id IN (
    12,
    186,
    1041, 1042,
    1049, 1050, 1051, 1053, 1055,
    1081, 1082, 1083, 1085, 1087,
    1089, 1090, 1091, 1093, 1095,
    1097, 1098,
    1105, 1106,
    1137, 1138,
    1145, 1146, 1148, 1149,
    1151, 1152, 1153, 1154, 1155, 1156, 1157, 1158, 1159, 1160
  );

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT @member_role, id
FROM sys_permission
WHERE deleted = 0
  AND id IN (
    12,
    505, 506,
    1041, 1042,
    1049, 1050,
    1065, 1066,
    1081, 1082,
    1137, 1138,
    1145, 1146, 1149,
    1151, 1152
  );

INSERT INTO sys_user (username, password, name, email, address, uid, deleted, create_time, role, score)
SELECT 'venue_manager', '$2a$10$sz5GIsQI162NS90.iuF6KuqUq6VeUb4uXztDDMDlry44Hazid/lhm', '场馆主管', 'venue_manager@example.com', '体育场馆中心', REPLACE(UUID(), '-', ''), 0, NOW(), 'venue_manager', 0
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'venue_manager');

INSERT INTO sys_user (username, password, name, email, address, uid, deleted, create_time, role, score)
SELECT 'teacher', '$2a$10$sz5GIsQI162NS90.iuF6KuqUq6VeUb4uXztDDMDlry44Hazid/lhm', '体育教师', 'teacher@example.com', '体育教学部', REPLACE(UUID(), '-', ''), 0, NOW(), 'teacher', 0
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'teacher');

UPDATE sys_user
SET password = '$2a$10$sz5GIsQI162NS90.iuF6KuqUq6VeUb4uXztDDMDlry44Hazid/lhm',
    name = '场馆主管',
    email = 'venue_manager@example.com',
    address = '体育场馆中心',
    role = 'venue_manager',
    deleted = 0
WHERE username = 'venue_manager';

UPDATE sys_user
SET password = '$2a$10$sz5GIsQI162NS90.iuF6KuqUq6VeUb4uXztDDMDlry44Hazid/lhm',
    name = '体育教师',
    email = 'teacher@example.com',
    address = '体育教学部',
    role = 'teacher',
    deleted = 0
WHERE username = 'teacher';

UPDATE sys_user SET name = '系统管理员', password = '$2a$10$sz5GIsQI162NS90.iuF6KuqUq6VeUb4uXztDDMDlry44Hazid/lhm', role = 'ADMIN', deleted = 0 WHERE username = 'admin';
UPDATE sys_user SET name = '普通学生或教职工', password = '$2a$10$sz5GIsQI162NS90.iuF6KuqUq6VeUb4uXztDDMDlry44Hazid/lhm', role = 'members', deleted = 0 WHERE username = 'zhangsan';
