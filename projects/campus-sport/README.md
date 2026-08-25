# 高校体育馆场地预约与器材管理系统

学生订场地、付钱、签到、结算、评价，也能借器材。后台改场地、时段和订单，还有一页报表。

## 学生端

- 注册登录，看公告和教学视频
- 搜场地、约时段、支付、签到、离场结算、评价
- 借器材、还器材，库存跟着变

## 管理端

- 用户、场地、订单、评价、公告、轮播图
- 时段、维护、支付方式
- 器材库存和借用记录
- 报表：数字卡片、状态分布、热度、最近订单

登录用 BCrypt 校验密码，再发 JWT。菜单和按钮按角色给。前端把 token 放 Pinia，后台路由登录后再挂上去。

## 技术

Spring Boot 2.7、MyBatis-Plus、Sa-Token、MySQL / MariaDB  
Vue 3、Vite、Pinia、Element Plus、ECharts、Axios

单独仓库：[campus-sport](https://github.com/h1neolzr7f/campus-sport)

## 怎么跑

需要 JDK 8、Maven、Node.js，以及 MySQL 或 MariaDB。

1. 建库 `sport-manage`，导入 SQL。器材、活动那些表不够的话，再跑 `scripts/install-equipment-module.ps1` 这类补丁。
2. 把 `admin/src/main/resources/application.yml.example` 复制成 `application.yml`，改成你的数据库账号。真密码别提交。
3. 场地图片放本地 `files/`，这个目录不进 Git。
4. 后端：

```powershell
cd admin
mvn -DskipTests package
java -jar target/boot.jar
```

5. 前端：

```powershell
cd front
npm install
npm run dev
```

- 前端：`http://localhost:7000`
- 后端：`http://localhost:9090`
- 演示账号：`admin / admin`，普通用户 `zhangsan / 123`

也可以用 `scripts/start-db.ps1`、`scripts/start-backend.ps1`、`scripts/start-frontend.ps1`。本机得先有数据库。`scripts/Resolve-Mysql.ps1` 会找 PATH 里的 `mysql` / `mariadb`。

点哪里、看什么，写在 `演示手册.md`。
