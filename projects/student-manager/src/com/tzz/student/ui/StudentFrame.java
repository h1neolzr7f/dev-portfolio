package com.tzz.student.ui;

import com.tzz.student.dao.StudentDao;
import com.tzz.student.model.Student;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;

public class StudentFrame extends JFrame {
    private final StudentDao studentDao;
    private final JTextField idField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField sexField = new JTextField();
    private final JTextField ageField = new JTextField();
    private final JTextField deptField = new JTextField();
    private final JTextField addressField = new JTextField();
    private final JTextArea resultArea = new JTextArea(12, 40);

    public StudentFrame(StudentDao studentDao) {
        super("学生信息管理");
        this.studentDao = studentDao;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 520);
        setLocationRelativeTo(null);
        buildLayout();
    }

    private void buildLayout() {
        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        addField(form, "学号", idField);
        addField(form, "姓名", nameField);
        addField(form, "性别", sexField);
        addField(form, "年龄", ageField);
        addField(form, "院系", deptField);
        addField(form, "地址", addressField);

        JPanel actions = new JPanel();
        JButton insertButton = new JButton("新增");
        JButton updateButton = new JButton("修改");
        JButton deleteButton = new JButton("删除");
        JButton queryAllButton = new JButton("查询全部");
        JButton queryAdultButton = new JButton("查询成年学生");
        insertButton.addActionListener(event -> insert());
        updateButton.addActionListener(event -> update());
        deleteButton.addActionListener(event -> delete());
        queryAllButton.addActionListener(event -> showStudents(() -> studentDao.findAll()));
        queryAdultButton.addActionListener(event -> showStudents(() -> studentDao.findOlderThan(17)));
        actions.add(insertButton);
        actions.add(updateButton);
        actions.add(deleteButton);
        actions.add(queryAllButton);
        actions.add(queryAdultButton);

        resultArea.setEditable(false);
        resultArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(form, BorderLayout.NORTH);
        add(actions, BorderLayout.CENTER);
        add(new JScrollPane(resultArea), BorderLayout.SOUTH);
    }

    private void insert() {
        try {
            studentDao.insert(readStudent());
            append("新增成功");
        } catch (Exception exception) {
            showError("新增失败", exception);
        }
    }

    private void update() {
        try {
            studentDao.update(readStudent());
            append("修改成功");
        } catch (Exception exception) {
            showError("修改失败", exception);
        }
    }

    private void delete() {
        try {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                throw new IllegalArgumentException("删除时必须填写学号");
            }
            studentDao.deleteById(id);
            append("删除成功");
        } catch (Exception exception) {
            showError("删除失败", exception);
        }
    }

    private void showStudents(Query query) {
        try {
            List<Student> students = query.run();
            resultArea.setText("");
            if (students.isEmpty()) {
                append("没有符合条件的记录");
                return;
            }
            for (Student student : students) {
                append(student.toString());
            }
        } catch (Exception exception) {
            showError("查询失败", exception);
        }
    }

    private Student readStudent() {
        String id = required(idField, "学号");
        String name = required(nameField, "姓名");
        String sex = required(sexField, "性别");
        String dept = required(deptField, "院系");
        String address = required(addressField, "地址");
        int age;
        try {
            age = Integer.parseInt(ageField.getText().trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("年龄必须是整数");
        }
        if (age <= 0 || age > 120) {
            throw new IllegalArgumentException("年龄超出合理范围");
        }
        return new Student(id, name, sex, age, dept, address);
    }

    private static String required(JTextField field, String label) {
        String value = field.getText().trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException(label + "不能为空");
        }
        return value;
    }

    private void append(String message) {
        resultArea.append(message + System.lineSeparator());
    }

    private void showError(String title, Exception exception) {
        JOptionPane.showMessageDialog(this, exception.getMessage(), title, JOptionPane.ERROR_MESSAGE);
        append(title + "：" + exception.getMessage());
    }

    private static void addField(JPanel form, String label, JTextField field) {
        form.add(new JLabel(label));
        form.add(field);
    }

    @FunctionalInterface
    private interface Query {
        List<Student> run() throws SQLException;
    }
}
