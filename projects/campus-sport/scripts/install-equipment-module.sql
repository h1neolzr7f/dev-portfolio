SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS equipment (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  type VARCHAR(255) DEFAULT NULL,
  total_stock INT DEFAULT 0,
  available_stock INT DEFAULT 0,
  location VARCHAR(255) DEFAULT NULL,
  state_radio VARCHAR(50) DEFAULT '正常',
  remark VARCHAR(500) DEFAULT NULL,
  create_time DATETIME DEFAULT NULL,
  update_time DATETIME DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS equipment_borrow (
  id INT AUTO_INCREMENT PRIMARY KEY,
  equipment_id INT NOT NULL,
  user_id INT NOT NULL,
  borrow_quantity INT DEFAULT 1,
  borrow_time VARCHAR(255) DEFAULT NULL,
  return_time VARCHAR(255) DEFAULT NULL,
  state_radio VARCHAR(50) DEFAULT '借用中',
  remark VARCHAR(500) DEFAULT NULL,
  create_time DATETIME DEFAULT NULL,
  update_time DATETIME DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO equipment(name,type,total_stock,available_stock,location,state_radio,remark)
SELECT '篮球','球类',20,20,'体育馆器材室A','正常','日常教学与课外活动借用'
WHERE NOT EXISTS (SELECT 1 FROM equipment WHERE name='篮球');

INSERT INTO equipment(name,type,total_stock,available_stock,location,state_radio,remark)
SELECT '羽毛球拍','球拍类',30,30,'体育馆器材室B','正常','配套羽毛球场使用'
WHERE NOT EXISTS (SELECT 1 FROM equipment WHERE name='羽毛球拍');

INSERT INTO equipment(name,type,total_stock,available_stock,location,state_radio,remark)
SELECT '足球','球类',15,15,'南校区田径场器材柜','正常','足球场训练使用'
WHERE NOT EXISTS (SELECT 1 FROM equipment WHERE name='足球');

INSERT INTO equipment(name,type,total_stock,available_stock,location,state_radio,remark)
SELECT '秒表','计时器材',10,10,'田径场器材室','正常','田径训练和比赛计时'
WHERE NOT EXISTS (SELECT 1 FROM equipment WHERE name='秒表');

UPDATE sys_permission
SET name='数据报表', path='dashboard', page='Dashboard', orders=1, icon='data-line',
    auth=NULL, pid=NULL, type=2, deleted=0, hide=0
WHERE id=186;

INSERT INTO sys_permission(id,name,path,orders,icon,page,auth,pid,type,deleted,hide)
SELECT 186,'数据报表','dashboard',1,'data-line','Dashboard',NULL,NULL,2,0,0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE id=186);

SET @maxId = (SELECT GREATEST(IFNULL(MAX(id), 1136), 1136) FROM sys_permission);

INSERT INTO sys_permission(id,name,path,orders,icon,page,auth,pid,type,deleted,hide)
SELECT @maxId + 1,'体育器材管理','equipment',1,'grid','Equipment',NULL,NULL,2,0,0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE path='equipment');

SET @equipmentPid = (SELECT id FROM sys_permission WHERE path='equipment' LIMIT 1);

INSERT INTO sys_permission(id,name,path,orders,icon,page,auth,pid,type,deleted,hide)
SELECT @maxId + 2,'体育器材查询',NULL,1,'grid',NULL,'equipment.list',@equipmentPid,3,0,0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE auth='equipment.list');
INSERT INTO sys_permission(id,name,path,orders,icon,page,auth,pid,type,deleted,hide)
SELECT @maxId + 3,'体育器材新增',NULL,1,'grid',NULL,'equipment.add',@equipmentPid,3,0,0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE auth='equipment.add');
INSERT INTO sys_permission(id,name,path,orders,icon,page,auth,pid,type,deleted,hide)
SELECT @maxId + 4,'体育器材导入',NULL,1,'grid',NULL,'equipment.import',@equipmentPid,3,0,0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE auth='equipment.import');
INSERT INTO sys_permission(id,name,path,orders,icon,page,auth,pid,type,deleted,hide)
SELECT @maxId + 5,'体育器材导出',NULL,1,'grid',NULL,'equipment.export',@equipmentPid,3,0,0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE auth='equipment.export');
INSERT INTO sys_permission(id,name,path,orders,icon,page,auth,pid,type,deleted,hide)
SELECT @maxId + 6,'批量删除',NULL,1,'grid',NULL,'equipment.deleteBatch',@equipmentPid,3,0,0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE auth='equipment.deleteBatch');
INSERT INTO sys_permission(id,name,path,orders,icon,page,auth,pid,type,deleted,hide)
SELECT @maxId + 7,'体育器材编辑',NULL,1,'grid',NULL,'equipment.edit',@equipmentPid,3,0,0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE auth='equipment.edit');
INSERT INTO sys_permission(id,name,path,orders,icon,page,auth,pid,type,deleted,hide)
SELECT @maxId + 8,'体育器材删除',NULL,1,'grid',NULL,'equipment.delete',@equipmentPid,3,0,0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE auth='equipment.delete');

SET @maxId2 = (SELECT GREATEST(IFNULL(MAX(id), 1136), 1136) FROM sys_permission);

INSERT INTO sys_permission(id,name,path,orders,icon,page,auth,pid,type,deleted,hide)
SELECT @maxId2 + 1,'器材借用管理','equipmentBorrow',1,'grid','EquipmentBorrow',NULL,NULL,2,0,0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE path='equipmentBorrow');

SET @borrowPid = (SELECT id FROM sys_permission WHERE path='equipmentBorrow' LIMIT 1);

INSERT INTO sys_permission(id,name,path,orders,icon,page,auth,pid,type,deleted,hide)
SELECT @maxId2 + 2,'器材借用查询',NULL,1,'grid',NULL,'equipmentBorrow.list',@borrowPid,3,0,0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE auth='equipmentBorrow.list');
INSERT INTO sys_permission(id,name,path,orders,icon,page,auth,pid,type,deleted,hide)
SELECT @maxId2 + 3,'器材借用导入',NULL,1,'grid',NULL,'equipmentBorrow.import',@borrowPid,3,0,0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE auth='equipmentBorrow.import');
INSERT INTO sys_permission(id,name,path,orders,icon,page,auth,pid,type,deleted,hide)
SELECT @maxId2 + 4,'器材借用导出',NULL,1,'grid',NULL,'equipmentBorrow.export',@borrowPid,3,0,0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE auth='equipmentBorrow.export');
INSERT INTO sys_permission(id,name,path,orders,icon,page,auth,pid,type,deleted,hide)
SELECT @maxId2 + 5,'器材归还',NULL,1,'grid',NULL,'equipmentBorrow.return',@borrowPid,3,0,0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE auth='equipmentBorrow.return');
INSERT INTO sys_permission(id,name,path,orders,icon,page,auth,pid,type,deleted,hide)
SELECT @maxId2 + 6,'器材借用删除',NULL,1,'grid',NULL,'equipmentBorrow.delete',@borrowPid,3,0,0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE auth='equipmentBorrow.delete');

INSERT INTO sys_role_permission(role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r
JOIN sys_permission p ON (
  p.id = 186
  OR p.path IN ('equipment','equipmentBorrow')
  OR p.auth LIKE 'equipment.%'
  OR p.auth LIKE 'equipmentBorrow.%'
)
WHERE r.flag IN ('ADMIN', 'venue_manager', 'teacher')
  AND p.deleted = 0
  AND NOT EXISTS (
    SELECT 1
    FROM sys_role_permission rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );
