
package collegeadmin.attendance;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import collegeadmin.DatabaseConnection;

public class AttendanceShortage extends JFrame {

    JLabel title, idLabel, nameLabel, totalLabel;
    JLabel attendedLabel, resultLabel;

    JTextField idField, nameField, totalField, attendedField;

    JButton checkButton, clearButton;

    public AttendanceShortage() {

        setTitle("College Administration System - Attendance Shortage");
        setSize(500, 400);
        setLayout(new GridLayout(7, 2, 10, 10));

        title = new JLabel("ATTENDANCE SHORTAGE", JLabel.CENTER);
        add(title);
        add(new JLabel(""));

        idLabel = new JLabel("Student ID:");
        idField = new JTextField();
        add(idLabel);
        add(idField);

        nameLabel = new JLabel("Student Name:");
        nameField = new JTextField();
        nameField.setEditable(false);
        add(nameLabel);
        add(nameField);

        totalLabel = new JLabel("Total Classes:");
        totalField = new JTextField();
        totalField.setEditable(false);
        add(totalLabel);
        add(totalField);

        attendedLabel = new JLabel("Attended Classes:");
        attendedField = new JTextField();
        attendedField.setEditable(false);
        add(attendedLabel);
        add(attendedField);

        checkButton = new JButton("Check Attendance");
        clearButton = new JButton("Clear");

        add(checkButton);
        add(clearButton);

        resultLabel = new JLabel("Attendance Result");
        add(resultLabel);
        add(new JLabel(""));

        checkButton.addActionListener(e -> checkAttendance());
        clearButton.addActionListener(e -> clearFields());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void checkAttendance() {

        String id = idField.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter Student ID!");
            return;
        }

        String studentSql =
                "SELECT student_name FROM students WHERE student_id = ?";

        String attendanceSql =
                "SELECT COUNT(*) AS total_classes, "
                + "SUM(CASE WHEN status = 'Present' THEN 1 ELSE 0 END) "
                + "AS attended_classes "
                + "FROM attendance WHERE student_id = ?";

        try (Connection con = DatabaseConnection.getConnection()) {

            // Find student
            try (PreparedStatement ps =
                    con.prepareStatement(studentSql)) {

                ps.setString(1, id);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        nameField.setText("");
                        totalField.setText("");
                        attendedField.setText("");
                        resultLabel.setText("Student Not Found!");

                        JOptionPane.showMessageDialog(this,
                                "Student ID Not Found!");
                        return;
                    }

                    nameField.setText(rs.getString("student_name"));
                }
            }

            // Calculate attendance
            try (PreparedStatement ps =
                    con.prepareStatement(attendanceSql)) {

                ps.setString(1, id);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {

                        int total = rs.getInt("total_classes");
                        int attended = rs.getInt("attended_classes");

                        totalField.setText(String.valueOf(total));
                        attendedField.setText(String.valueOf(attended));

                        if (total == 0) {
                            resultLabel.setText(
                                    "No Attendance Records Found!");
                            JOptionPane.showMessageDialog(this,
                                    "No attendance records available for this student!");
                            return;
                        }

                        double percentage =
                                (attended * 100.0) / total;

                        if (percentage < 75) {

                            resultLabel.setText(
                                    String.format("Shortage: %.2f%%",
                                            percentage));

                            JOptionPane.showMessageDialog(this,
                                    "Student has Attendance Shortage!");

                        } else {

                            resultLabel.setText(
                                    String.format("Eligible: %.2f%%",
                                            percentage));

                            JOptionPane.showMessageDialog(this,
                                    "Student is Eligible!");
                        }
                    }
                }
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(this,
                    "Database Error: " + ex.getMessage());
        }
    }

    private void clearFields() {

        idField.setText("");
        nameField.setText("");
        totalField.setText("");
        attendedField.setText("");

        resultLabel.setText("Attendance Result");
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new AttendanceShortage());
    }
}
