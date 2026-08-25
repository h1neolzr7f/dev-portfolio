# 高校体育馆场地预约与器材管理系统

用户端提供场地预约、支付、签到、结算、评价及器材借用。管理端维护场地、时段与订单，并提供数据报表。

## 用户端

- 注册与登录，浏览公告及教学视频
- 场地检索、时段预约、支付、签到、离场结算与评价
- 器材浏览、借用与归还，库存随操作更新

## 管理端

- 用户、场地、订单、评价、公告、轮播图
- 时段、维护计划、支付方式
- 器材库存与借用记录
- 报表：统计卡片、状态分布、场地热度、最近订单

认证采用 BCrypt 校验密码，登录后签发 JWT。菜单与按钮权限按角色分配。前端以 Pinia 保存 token 与权限，后台路由在登录后动态注册。

## 技术栈

Spring Boot 2.7、MyBatis-Plus、Sa-Token、MySQL / MariaDB  
Vue 3、Vite、Pinia、Element Plus、ECharts、Axios

独立仓库：[campus-sport](https://github.com/h1neolzr7f/campus-sport)

## 运行环境

JDK 8、Maven、Node.js，以及 MySQL 或 MariaDB。

1. 创建数据库 `sport-manage` 并导入 SQL。若缺少器材或活动相关表，执行 `scripts/install-equipment-module.ps1` 等补丁脚本。
2. 将 `admin/src/main/resources/application.yml.example` 复制为 `application.yml`，填写本地数据库账号。请勿提交真实口令。
3. 场地图片存放于本地 `files/`，该目录不纳入版本库。
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

- 前端：`http://localhost:7000`
- 后端：`http://localhost:9090`
- 演示账号：管理员 `admin / admin`，普通用户 `zhangsan / 123`

亦可使用 `scripts/start-db.ps1`、`scripts/start-backend.ps1`、`scripts/start-frontend.ps1`。数据库服务需已启动。`scripts/Resolve-Mysql.ps1` 将优先使用 PATH 中的 `mysql` / `mariadb`。

操作步骤见 `演示手册.md`。
