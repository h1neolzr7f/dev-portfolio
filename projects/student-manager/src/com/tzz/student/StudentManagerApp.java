package com.tzz.student;

import com.tzz.student.config.DbConfig;
import com.tzz.student.dao.StudentDao;
import com.tzz.student.ui.StudentFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class StudentManagerApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // 使用默认外观即可
            }
            StudentDao studentDao = new StudentDao(DbConfig.load());
            StudentFrame frame = new StudentFrame(studentDao);
            frame.setVisible(true);
        });
    }
}
