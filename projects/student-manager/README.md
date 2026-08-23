# 学生信息管理系统

课程实验整理后的桌面小工具：用 Java Swing 做界面，JDBC + MySQL 做增删改查。适合作为简历里的课程项目，不建议和另外两个完整系统并列成主项目。

## 功能

- 新增、修改、按学号删除
- 查询全部、查询成年学生
- 表单校验（必填、年龄范围）
- SQL 全部使用 `PreparedStatement`，避免拼接注入
- 数据库连接从 `db.properties` 或环境变量读取，不再写死在代码里

## 运行

1. 安装 JDK 8+ 和 MySQL，导入 `schema.sql`。
2. 复制 `db.properties.example` 为 `db.properties`，改成你的账号。
3. 把 MySQL Connector/J 放到 `lib/mysql-connector-j.jar`。
4. 编译并启动：

```powershell
.\run.ps1
```

或手动执行：

```powershell
javac -encoding UTF-8 -cp "lib/*" -d out src/com/tzz/student/**/*.java src/com/tzz/student/*.java
java -cp "out;lib/*" com.tzz.student.StudentManagerApp
```

## 技术栈

Java、Swing、JDBC、MySQL
