
package collegeadmin.examination;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

import collegeadmin.DatabaseConnection;

public class MarksEntry extends JFrame {

    JTextField studentIdField, subjectField, marksField;
    JButton submitButton;

    public MarksEntry() {

        setTitle("Examination - Marks Entry");
        setSize(400, 250);
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel("Student ID:"));
        studentIdField = new JTextField();
        add(studentIdField);

        add(new JLabel("Subject:"));
        subjectField = new JTextField();
        add(subjectField);

        add(new JLabel("Marks (0-100):"));
        marksField = new JTextField();
        add(marksField);

        submitButton = new JButton("Submit Marks");
        add(new JLabel(""));
        add(submitButton);

        submitButton.addActionListener(e -> submitMarks());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void submitMarks() {

        String studentId = studentIdField.getText().trim();
        String subject = subjectField.getText().trim();
        String marksText = marksField.getText().trim();

        if (studentId.isEmpty() || subject.isEmpty() || marksText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill all fields!");
            return;
        }

        int marks;

        try {
            marks = Integer.parseInt(marksText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numeric marks!");
            return;
        }

        if (marks < 0 || marks > 100) {
            JOptionPane.showMessageDialog(this,
                    "Marks must be between 0 and 100!");
            return;
        }

        String grade;

        if (marks >= 90) {
            grade = "A";
        } else if (marks >= 80) {
            grade = "B";
        } else if (marks >= 70) {
            grade = "C";
        } else if (marks >= 60) {
            grade = "D";
        } else {
            grade = "F";
        }

        String checkSql =
                "SELECT student_id FROM students WHERE student_id = ?";

        String insertSql =
                "INSERT INTO results (student_id, subject, marks, grade) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = con.prepareStatement(checkSql)) {

            checkStmt.setString(1, studentId);

            try (ResultSet rs = checkStmt.executeQuery()) {

                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this,
                            "Student ID not found!");
                    return;
                }
            }

            try (PreparedStatement insertStmt =
                    con.prepareStatement(insertSql)) {

                insertStmt.setString(1, studentId);
                insertStmt.setString(2, subject);
                insertStmt.setInt(3, marks);
                insertStmt.setString(4, grade);

                insertStmt.executeUpdate();

                JOptionPane.showMessageDialog(this,
                        "Marks Submitted Successfully!\nGrade: " + grade);

                studentIdField.setText("");
                subjectField.setText("");
                marksField.setText("");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Database Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new MarksEntry();
    }
}
