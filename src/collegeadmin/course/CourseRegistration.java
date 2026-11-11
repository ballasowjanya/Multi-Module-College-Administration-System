
package collegeadmin.course;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import collegeadmin.DatabaseConnection;

public class CourseRegistration extends JFrame {

    private JTextField studentIdField;
    private JTextField courseIdField;
    private JTextArea resultArea;

    public CourseRegistration() {

        setTitle("Course Registration");
        setSize(500, 400);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        inputPanel.add(new JLabel("Student ID:"));
        studentIdField = new JTextField();
        inputPanel.add(studentIdField);

        inputPanel.add(new JLabel("Course ID:"));
        courseIdField = new JTextField();
        inputPanel.add(courseIdField);

        JButton registerButton = new JButton("Register");
        JButton viewButton = new JButton("View Registrations");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(registerButton);
        buttonPanel.add(viewButton);

        resultArea = new JTextArea();
        resultArea.setEditable(false);

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        registerButton.addActionListener(e -> registerStudent());
        viewButton.addActionListener(e -> viewRegistrations());

        setVisible(true);
    }

    private void registerStudent() {

        String studentId = studentIdField.getText().trim();
        String courseId = courseIdField.getText().trim();

        if (studentId.isEmpty() || courseId.isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Please enter all details!");
            return;
        }

        try (Connection con = DatabaseConnection.getConnection()) {

            // Check whether student exists
            String studentSql =
                    "SELECT student_name FROM students WHERE student_id = ?";

            String studentName;

            try (PreparedStatement ps = con.prepareStatement(studentSql)) {

                ps.setString(1, studentId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        resultArea.setText("Student ID Not Found!");
                        return;
                    }

                    studentName = rs.getString("student_name");
                }
            }

            // Check whether course exists
            String courseSql =
                    "SELECT course_name FROM courses WHERE course_id = ?";

            String courseName;

            try (PreparedStatement ps = con.prepareStatement(courseSql)) {

                ps.setString(1, courseId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        resultArea.setText("Course ID Not Found!");
                        return;
                    }

                    courseName = rs.getString("course_name");
                }
            }

            // Save registration
            String insertSql =
                    "INSERT INTO course_registration "
                    + "(student_id, course_id, registration_date) "
                    + "VALUES (?, ?, CURRENT_DATE)";

            try (PreparedStatement ps = con.prepareStatement(insertSql)) {

                ps.setString(1, studentId);
                ps.setString(2, courseId);

                ps.executeUpdate();

                resultArea.setText(
                        "Student Registered Successfully!\n\n"
                        + "Student ID: " + studentId + "\n"
                        + "Student Name: " + studentName + "\n"
                        + "Course ID: " + courseId + "\n"
                        + "Course Name: " + courseName
                );

                JOptionPane.showMessageDialog(this,
                        "Student Registered Successfully!");

                studentIdField.setText("");
                courseIdField.setText("");
            }

        } catch (SQLException ex) {

            if ("23000".equals(ex.getSQLState())) {

                resultArea.setText("Student Already Registered!");

                JOptionPane.showMessageDialog(this,
                        "Student Already Registered for this Course!");

            } else {

                JOptionPane.showMessageDialog(this,
                        "Database Error: " + ex.getMessage());
            }
        }
    }

    private void viewRegistrations() {

        String sql =
                "SELECT cr.student_id, s.student_name, "
                + "cr.course_id, c.course_name, cr.registration_date "
                + "FROM course_registration cr "
                + "JOIN students s ON cr.student_id = s.student_id "
                + "JOIN courses c ON cr.course_id = c.course_id "
                + "ORDER BY cr.registration_date DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder details = new StringBuilder();

            details.append("STUDENT REGISTRATION DETAILS\n");
            details.append("------------------------------------\n\n");

            boolean found = false;

            while (rs.next()) {

                found = true;

                details.append("Student ID: ")
                        .append(rs.getString("student_id")).append("\n");

                details.append("Student Name: ")
                        .append(rs.getString("student_name")).append("\n");

                details.append("Course ID: ")
                        .append(rs.getString("course_id")).append("\n");

                details.append("Course Name: ")
                        .append(rs.getString("course_name")).append("\n");

                details.append("Registration Date: ")
                        .append(rs.getDate("registration_date")).append("\n");

                details.append("------------------------------------\n");
            }

            if (found) {
                resultArea.setText(details.toString());
            } else {
                resultArea.setText("No Registrations Found!");
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(this,
                    "Database Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new CourseRegistration());
    }
}
