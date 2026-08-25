# 高校体育馆场地预约与器材管理系统

校园体育馆业务系统。用户端完成场地浏览、预约、签到、结算、评价和器材借用；管理端维护场地、时段、订单、公告、教学内容和实时数据报表。

## 功能

用户端

- 注册登录、公告、运动教学、场地搜索与详情
- 时段预约、支付、签到、离场结算、评价
- 器材浏览、借用、归还，库存联动

管理端

- 用户、场地、预约订单、评价、公告、轮播图
- 时段与维护、支付方式
- 器材库存与借用记录
- 实时数据报表（统计卡片、状态分布、热度、最近订单）

权限

- 登录后 BCrypt 校验密码
- Sa-Token / JWT 签发 token
- 角色菜单与按钮权限；前端 Pinia 保存 token、菜单、权限，动态挂载后台路由

## 技术栈

Spring Boot 2.7、MyBatis-Plus、Sa-Token、MySQL / MariaDB  
Vue 3、Vite、Pinia、Element Plus、ECharts、Axios

独立开源仓：[campus-sport](https://github.com/h1neolzr7f/campus-sport)

## 本地运行

需要 JDK 8、Maven、Node.js、MySQL 或 MariaDB。

1. 创建数据库 `sport-manage`，导入 SQL 后按需执行 `scripts/install-equipment-module.ps1` 等补丁脚本。
2. 复制 `admin/src/main/resources/application.yml.example` 为 `application.yml`（若还没有），改成本机数据库账号。不要提交真实密码。
3. 场地图片放在本地 `files/`，该目录不进 Git。
4. 启动后端：

```powershell
cd admin
mvn -DskipTests package
java -jar target/boot.jar
```

5. 启动前端：

```powershell
cd front
npm install
npm run dev
```

默认地址：

- 前端：`http://localhost:7000`
- 后端：`http://localhost:9090`
- 演示账号：`admin / admin`，普通用户 `zhangsan / 123`

也可用 `scripts/start-db.ps1`、`scripts/start-backend.ps1`、`scripts/start-frontend.ps1`。数据库需本机已启动；`scripts/Resolve-Mysql.ps1` 会优先使用 PATH 里的 `mysql` / `mariadb`。

更细的点击路径见 `演示手册.md`。
