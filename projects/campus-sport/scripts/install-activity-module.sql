USE `sport-manage`;
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `activity` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `title` varchar(255) NOT NULL COMMENT '活动名称',
  `content` text DEFAULT NULL COMMENT '活动内容',
  `location` varchar(255) DEFAULT NULL COMMENT '活动地点',
  `activity_time` varchar(255) DEFAULT NULL COMMENT '活动时间',
  `deadline` varchar(255) DEFAULT NULL COMMENT '报名截止时间',
  `capacity` int(11) DEFAULT 0 COMMENT '人数上限',
  `state_radio` varchar(50) DEFAULT '已发布' COMMENT '活动状态',
  `publisher_id` int(11) DEFAULT NULL COMMENT '发布人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='活动管理';

CREATE TABLE IF NOT EXISTS `activity_signup` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `activity_id` int(11) NOT NULL COMMENT '活动',
  `user_id` int(11) NOT NULL COMMENT '报名用户',
  `apply_time` varchar(50) DEFAULT NULL COMMENT '报名时间',
  `state_radio` varchar(50) DEFAULT '待审核' COMMENT '审核状态',
  `reviewer_id` int(11) DEFAULT NULL COMMENT '审核人',
  `review_time` varchar(50) DEFAULT NULL COMMENT '审核时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '申请备注',
  `review_remark` varchar(500) DEFAULT NULL COMMENT '审核意见',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_activity_signup_activity` (`activity_id`),
  KEY `idx_activity_signup_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='活动报名';

INSERT INTO `activity` (`id`, `title`, `content`, `location`, `activity_time`, `deadline`, `capacity`, `state_radio`, `publisher_id`, `remark`, `create_time`, `update_time`)
SELECT 1, '校园篮球友谊赛', '面向全校学生开放报名，审核通过后按照通知时间到场参加。', '体育馆篮球场', '2026-05-12 15:00:00', '2026-05-10 18:00:00', 30, '已发布', (SELECT id FROM sys_user WHERE username='teacher' LIMIT 1), '教师发布的演示活动', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `activity` WHERE id = 1);

INSERT INTO `activity` (`id`, `title`, `content`, `location`, `activity_time`, `deadline`, `capacity`, `state_radio`, `publisher_id`, `remark`, `create_time`, `update_time`)
SELECT 2, '羽毛球公开体验课', '由场馆主管统一组织，学生提交参与申请后等待审核。', '综合训练馆', '2026-05-15 19:00:00', '2026-05-14 12:00:00', 24, '已发布', (SELECT id FROM sys_user WHERE username='venue_manager' LIMIT 1), '场馆主管发布的演示活动', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `activity` WHERE id = 2);

INSERT INTO `activity_signup` (`activity_id`, `user_id`, `apply_time`, `state_radio`, `reviewer_id`, `review_time`, `remark`, `review_remark`, `create_time`, `update_time`)
SELECT 1, (SELECT id FROM sys_user WHERE username='zhangsan' LIMIT 1), NOW(), '待审核', NULL, NULL, '希望参加篮球赛', NULL, NOW(), NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM `activity_signup`
  WHERE activity_id = 1 AND user_id = (SELECT id FROM sys_user WHERE username='zhangsan' LIMIT 1)
);

INSERT INTO `sys_permission` (`id`, `name`, `path`, `orders`, `icon`, `page`, `auth`, `pid`, `deleted`, `create_time`, `update_time`, `type`, `hide`) VALUES
(1151, '活动管理', 'activity', 1, 'calendar', 'Activity', NULL, NULL, 0, NOW(), NOW(), 2, 0),
(1152, '活动查询', NULL, 1, 'grid', NULL, 'activity.list', 1151, 0, NOW(), NOW(), 3, 0),
(1153, '活动新增', NULL, 1, 'grid', NULL, 'activity.add', 1151, 0, NOW(), NOW(), 3, 0),
(1154, '活动导入', NULL, 1, 'grid', NULL, 'activity.import', 1151, 0, NOW(), NOW(), 3, 0),
(1155, '活动导出', NULL, 1, 'grid', NULL, 'activity.export', 1151, 0, NOW(), NOW(), 3, 0),
(1156, '批量删除', NULL, 1, 'grid', NULL, 'activity.deleteBatch', 1151, 0, NOW(), NOW(), 3, 0),
(1157, '活动编辑', NULL, 1, 'grid', NULL, 'activity.edit', 1151, 0, NOW(), NOW(), 3, 0),
(1158, '活动删除', NULL, 1, 'grid', NULL, 'activity.delete', 1151, 0, NOW(), NOW(), 3, 0),
(1159, '活动发布', NULL, 1, 'grid', NULL, 'activity.publish', 1151, 0, NOW(), NOW(), 3, 0),
(1160, '活动审核', NULL, 1, 'grid', NULL, 'activity.review', 1151, 0, NOW(), NOW(), 3, 0)
ON DUPLICATE KEY UPDATE
  `name` = VALUES(`name`),
  `path` = VALUES(`path`),
  `page` = VALUES(`page`),
  `auth` = VALUES(`auth`),
  `pid` = VALUES(`pid`),
  `deleted` = 0,
  `type` = VALUES(`type`),
  `hide` = VALUES(`hide`),
  `update_time` = NOW();

SET @admin_role := (SELECT id FROM sys_role WHERE flag = 'ADMIN' LIMIT 1);
SET @venue_role := (SELECT id FROM sys_role WHERE flag = 'venue_manager' LIMIT 1);
SET @teacher_role := (SELECT id FROM sys_role WHERE flag = 'teacher' LIMIT 1);
SET @member_role := (SELECT id FROM sys_role WHERE flag = 'members' LIMIT 1);

DELETE FROM sys_role_permission
WHERE permission_id BETWEEN 1151 AND 1160;

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT role_id, permission_id
FROM (
  SELECT @admin_role AS role_id, id AS permission_id FROM sys_permission WHERE id BETWEEN 1151 AND 1160
  UNION ALL
  SELECT @venue_role AS role_id, id AS permission_id FROM sys_permission WHERE id BETWEEN 1151 AND 1160
  UNION ALL
  SELECT @teacher_role AS role_id, id AS permission_id FROM sys_permission WHERE id BETWEEN 1151 AND 1160
  UNION ALL
  SELECT @member_role AS role_id, id AS permission_id FROM sys_permission WHERE id IN (1151, 1152)
) t
WHERE role_id IS NOT NULL;
