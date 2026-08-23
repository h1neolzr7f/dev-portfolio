package com.tzz.student.dao;

import com.tzz.student.config.DbConfig;
import com.tzz.student.model.Student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDao {
    private final DbConfig config;

    public StudentDao(DbConfig config) {
        this.config = config;
    }

    public void insert(Student student) throws SQLException {
        String sql = "INSERT INTO student (id, name, sex, age, dept, address) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindStudent(statement, student);
            statement.executeUpdate();
        }
    }

    public void update(Student student) throws SQLException {
        String sql = "UPDATE student SET name = ?, sex = ?, age = ?, dept = ?, address = ? WHERE id = ?";
        try (Connection connection = open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, student.getName());
            statement.setString(2, student.getSex());
            statement.setInt(3, student.getAge());
            statement.setString(4, student.getDept());
            statement.setString(5, student.getAddress());
            statement.setString(6, student.getId());
            if (statement.executeUpdate() == 0) {
                throw new SQLException("未找到学号：" + student.getId());
            }
        }
    }

    public void deleteById(String id) throws SQLException {
        String sql = "DELETE FROM student WHERE id = ?";
        try (Connection connection = open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            if (statement.executeUpdate() == 0) {
                throw new SQLException("未找到学号：" + id);
            }
        }
    }

    public List<Student> findAll() throws SQLException {
        return query("SELECT id, name, sex, age, dept, address FROM student ORDER BY id", null);
    }

    public List<Student> findOlderThan(int age) throws SQLException {
        return query("SELECT id, name, sex, age, dept, address FROM student WHERE age > ? ORDER BY id", age);
    }

    private List<Student> query(String sql, Integer age) throws SQLException {
        List<Student> students = new ArrayList<>();
        try (Connection connection = open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (age != null) {
                statement.setInt(1, age);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    students.add(new Student(
                            resultSet.getString("id"),
                            resultSet.getString("name"),
                            resultSet.getString("sex"),
                            resultSet.getInt("age"),
                            resultSet.getString("dept"),
                            resultSet.getString("address")
                    ));
                }
            }
        }
        return students;
    }

    private Connection open() throws SQLException {
        return DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
    }

    private static void bindStudent(PreparedStatement statement, Student student) throws SQLException {
        statement.setString(1, student.getId());
        statement.setString(2, student.getName());
        statement.setString(3, student.getSex());
        statement.setInt(4, student.getAge());
        statement.setString(5, student.getDept());
        statement.setString(6, student.getAddress());
    }
}
