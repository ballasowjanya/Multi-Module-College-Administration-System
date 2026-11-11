
package collegeadmin.attendance;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import collegeadmin.DatabaseConnection;

public class AttendanceRecord extends JFrame {

    private JTextField studentIdField;
    private JTextField dateField;
    private JComboBox<String> statusBox;
    private JTextArea resultArea;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    public AttendanceRecord() {

        setTitle("Student Attendance");
        setSize(500, 400);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        inputPanel.add(new JLabel("Student ID:"));
        studentIdField = new JTextField();
        inputPanel.add(studentIdField);

        inputPanel.add(new JLabel("Date (DD-MM-YYYY):"));
        dateField = new JTextField();
        inputPanel.add(dateField);

        inputPanel.add(new JLabel("Attendance Status:"));

        String[] status = {"Present", "Absent"};
        statusBox = new JComboBox<>(status);
        inputPanel.add(statusBox);

        JButton submitButton = new JButton("Submit Attendance");
        JButton viewButton = new JButton("View Attendance");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(viewButton);

        resultArea = new JTextArea();
        resultArea.setEditable(false);

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        submitButton.addActionListener(e -> submitAttendance());
        viewButton.addActionListener(e -> viewAttendance());

        setVisible(true);
    }

    private void submitAttendance() {

        String studentId = studentIdField.getText().trim();
        String dateText = dateField.getText().trim();
        String status = (String) statusBox.getSelectedItem();

        if (studentId.isEmpty() || dateText.isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Please fill all details!");
            return;
        }

        LocalDate attendanceDate;

        try {
            attendanceDate = LocalDate.parse(dateText, DATE_FORMAT);

        } catch (DateTimeParseException ex) {

            JOptionPane.showMessageDialog(this,
                    "Enter a valid date in DD-MM-YYYY format!");
            return;
        }

        String studentSql =
                "SELECT student_name FROM students WHERE student_id = ?";

        String studentName;

        try (Connection con = DatabaseConnection.getConnection()) {

            // Check student
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

            // Insert attendance
            String insertSql =
                    "INSERT INTO attendance "
                    + "(student_id, attendance_date, status) "
                    + "VALUES (?, ?, ?)";

            try (PreparedStatement ps = con.prepareStatement(insertSql)) {

                ps.setString(1, studentId);
                ps.setDate(2, java.sql.Date.valueOf(attendanceDate));
                ps.setString(3, status);

                ps.executeUpdate();

                resultArea.setText(
                        "Attendance Submitted Successfully!\n\n"
                        + "Student ID: " + studentId + "\n"
                        + "Student Name: " + studentName + "\n"
                        + "Date: " + dateText + "\n"
                        + "Status: " + status
                );

                JOptionPane.showMessageDialog(this,
                        "Attendance Submitted Successfully!");

                studentIdField.setText("");
                dateField.setText("");
                statusBox.setSelectedIndex(0);
            }

        } catch (SQLException ex) {

            if ("23000".equals(ex.getSQLState())) {

                resultArea.setText(
                        "Attendance Already Submitted for this Date!");

                JOptionPane.showMessageDialog(this,
                        "Attendance Already Submitted for this Date!");

            } else {

                JOptionPane.showMessageDialog(this,
                        "Database Error: " + ex.getMessage());
            }
        }
    }

    private void viewAttendance() {

        String sql =
                "SELECT a.student_id, s.student_name, "
                + "a.attendance_date, a.status "
                + "FROM attendance a "
                + "JOIN students s ON a.student_id = s.student_id "
                + "ORDER BY a.attendance_date DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder details = new StringBuilder();

            details.append("STUDENT ATTENDANCE DETAILS\n");
            details.append("----------------------------------\n\n");

            boolean found = false;

            while (rs.next()) {

                found = true;

                details.append("Student ID: ")
                        .append(rs.getString("student_id")).append("\n");

                details.append("Student Name: ")
                        .append(rs.getString("student_name")).append("\n");

                details.append("Date: ")
                        .append(rs.getDate("attendance_date")).append("\n");

                details.append("Status: ")
                        .append(rs.getString("status")).append("\n");

                details.append("----------------------------------\n");
            }

            if (found) {
                resultArea.setText(details.toString());
            } else {
                resultArea.setText("No Attendance Records Found!");
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(this,
                    "Database Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new AttendanceRecord());
    }
}
