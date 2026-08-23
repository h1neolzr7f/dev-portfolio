package com.tzz.student.model;

public class Student {
    private final String id;
    private final String name;
    private final String sex;
    private final int age;
    private final String dept;
    private final String address;

    public Student(String id, String name, String sex, int age, String dept, String address) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.dept = dept;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public int getAge() {
        return age;
    }

    public String getDept() {
        return dept;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return String.format("ID: %s, 姓名: %s, 性别: %s, 年龄: %d, 院系: %s, 地址: %s",
                id, name, sex, age, dept, address);
    }
}
