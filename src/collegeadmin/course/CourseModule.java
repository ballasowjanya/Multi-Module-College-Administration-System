
package collegeadmin.course;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import collegeadmin.DatabaseConnection;

public class CourseModule extends JFrame {

    private JTextField idField;
    private JTextField nameField;
    private JTextField creditsField;
    private JTextArea resultArea;

    public CourseModule() {

        setTitle("Course Management");
        setSize(500, 400);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        inputPanel.add(new JLabel("Course ID:"));
        idField = new JTextField();
        inputPanel.add(idField);

        inputPanel.add(new JLabel("Course Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Credits:"));
        creditsField = new JTextField();
        inputPanel.add(creditsField);

        JButton addButton = new JButton("Add Course");
        JButton viewButton = new JButton("View Courses");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);

        resultArea = new JTextArea();
        resultArea.setEditable(false);

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        addButton.addActionListener(e -> addCourse());
        viewButton.addActionListener(e -> viewCourses());

        setVisible(true);
    }

    private void addCourse() {

        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String creditsText = creditsField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || creditsText.isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Please fill all details!");
            return;
        }

        int credits;

        try {
            credits = Integer.parseInt(creditsText);

            if (credits <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Credits must be greater than zero!");
                return;
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numeric credits!");
            return;
        }

        String sql = "INSERT INTO courses "
                + "(course_id, course_name, credits) "
                + "VALUES (?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.setString(2, name);
            ps.setInt(3, credits);

            ps.executeUpdate();

            resultArea.setText(
                    "Course Added Successfully!\n\n"
                    + "Course ID: " + id + "\n"
                    + "Course Name: " + name + "\n"
                    + "Credits: " + credits
            );

            JOptionPane.showMessageDialog(this,
                    "Course Added Successfully!");

            idField.setText("");
            nameField.setText("");
            creditsField.setText("");

        } catch (SQLException ex) {

            if ("23000".equals(ex.getSQLState())) {
                JOptionPane.showMessageDialog(this,
                        "Course ID Already Exists!");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Database Error: " + ex.getMessage());
            }
        }
    }

    private void viewCourses() {

        String sql = "SELECT * FROM courses ORDER BY course_id";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder details = new StringBuilder();

            details.append("AVAILABLE COURSES\n");
            details.append("------------------------------\n\n");

            boolean found = false;

            while (rs.next()) {

                found = true;

                details.append("Course ID: ")
                        .append(rs.getString("course_id")).append("\n");

                details.append("Course Name: ")
                        .append(rs.getString("course_name")).append("\n");

                details.append("Credits: ")
                        .append(rs.getInt("credits")).append("\n");

                details.append("------------------------------\n");
            }

            if (found) {
                resultArea.setText(details.toString());
            } else {
                resultArea.setText("No Courses Found!");
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(this,
                    "Database Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new CourseModule());
    }
}
