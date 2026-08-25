# 学生信息管理系统

Java 课设。Swing 做窗口，JDBC 连 MySQL，增删改查。简历上当课程作业写就行，不必跟另外两个系统抢位置。

## 能做什么

- 按学号增、改、删
- 查全部，或只看成学年纪
- 学号必填，年龄有范围
- SQL 都走 `PreparedStatement`，不拼字符串
- 库账号放 `db.properties` 或环境变量，没写死在代码里

## 运行

1. JDK 8+、MySQL，导入 `schema.sql`。
2. 复制 `db.properties.example` 为 `db.properties`，改成你的账号。
3. MySQL Connector/J 放到 `lib/mysql-connector-j.jar`。
4. 运行：

```powershell
.\run.ps1
```

或：

```powershell
javac -encoding UTF-8 -cp "lib/*" -d out src/com/tzz/student/**/*.java src/com/tzz/student/*.java
java -cp "out;lib/*" com.tzz.student.StudentManagerApp
```

Java、Swing、JDBC、MySQL

单独仓库：[student-manager](https://github.com/h1neolzr7f/student-manager)
