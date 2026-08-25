# 学生信息管理系统

Java 课程实验。使用 Swing 构建桌面界面，通过 JDBC 访问 MySQL，完成学生记录的增删改查。可作为简历中的课程项目，不必与另外两个完整系统并列。

## 功能

- 按学号新增、修改、删除
- 查询全部记录，或按成年条件筛选
- 学号必填，年龄范围校验
- SQL 均使用 `PreparedStatement`，避免字符串拼接
- 数据库连接信息从 `db.properties` 或环境变量读取

## 运行

1. 安装 JDK 8+ 与 MySQL，导入 `schema.sql`。
2. 将 `db.properties.example` 复制为 `db.properties` 并填写账号。
3. 将 MySQL Connector/J 置于 `lib/mysql-connector-j.jar`。
4. 启动：

```powershell
.\run.ps1
```

或：

```powershell
javac -encoding UTF-8 -cp "lib/*" -d out src/com/tzz/student/**/*.java src/com/tzz/student/*.java
java -cp "out;lib/*" com.tzz.student.StudentManagerApp
```

**技术栈** Java、Swing、JDBC、MySQL

独立仓库：[student-manager](https://github.com/h1neolzr7f/student-manager)
