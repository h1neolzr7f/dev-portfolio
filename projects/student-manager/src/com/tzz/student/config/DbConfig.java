package com.tzz.student.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class DbConfig {
    private final String url;
    private final String user;
    private final String password;

    public DbConfig(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public static DbConfig load() {
        Properties properties = new Properties();
        Path localFile = Paths.get("db.properties");
        if (Files.exists(localFile)) {
            try (InputStream input = Files.newInputStream(localFile)) {
                properties.load(input);
            } catch (IOException exception) {
                throw new IllegalStateException("无法读取 db.properties", exception);
            }
        } else {
            try (InputStream input = DbConfig.class.getResourceAsStream("/db.properties")) {
                if (input != null) {
                    properties.load(input);
                }
            } catch (IOException exception) {
                throw new IllegalStateException("无法读取默认数据库配置", exception);
            }
        }

        String url = firstNonBlank(
                System.getenv("STUDENT_DB_URL"),
                properties.getProperty("jdbc.url"),
                "jdbc:mysql://localhost:3306/student_manager?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8"
        );
        String user = firstNonBlank(
                System.getenv("STUDENT_DB_USER"),
                properties.getProperty("jdbc.user"),
                "root"
        );
        String password = firstNonBlank(
                System.getenv("STUDENT_DB_PASSWORD"),
                properties.getProperty("jdbc.password"),
                "root"
        );
        return new DbConfig(url, user, password);
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }
        return "";
    }
}
