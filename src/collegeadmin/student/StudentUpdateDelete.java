
package collegeadmin.student;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import collegeadmin.DatabaseConnection;

public class StudentUpdateDelete extends JFrame {

    private JTextField idField, nameField, branchField;
    private JTextArea resultArea;

    private boolean studentFound = false;

    public StudentUpdateDelete() {

        setTitle("Student Update and Delete");
        setSize(500, 400);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        inputPanel.add(new JLabel("Existing Student ID:"));
        idField = new JTextField();
        inputPanel.add(idField);

        inputPanel.add(new JLabel("Student Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Branch:"));
        branchField = new JTextField();
        inputPanel.add(branchField);

        JButton findButton = new JButton("Find Student");
        JButton updateButton = new JButton("Update Student");
        JButton deleteButton = new JButton("Delete Student");

        JPanel buttonPanel = new JPanel();

        buttonPanel.add(findButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        resultArea = new JTextArea();
        resultArea.setEditable(false);

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        findButton.addActionListener(e -> findStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());

        setVisible(true);
    }

    private void findStudent() {

        String id = idField.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Student ID!");
            return;
        }

        String sql = "SELECT * FROM students WHERE student_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    studentFound = true;

                    nameField.setText(rs.getString("student_name"));
                    branchField.setText(rs.getString("department"));

                    resultArea.setText(
                            "Student Found!\n\n"
                            + "Student ID: " + rs.getString("student_id") + "\n"
                            + "Student Name: " + rs.getString("student_name") + "\n"
                            + "Branch: " + rs.getString("department") + "\n\n"
                            + "You can now update the details."
                    );

                } else {

                    studentFound = false;
                    nameField.setText("");
                    branchField.setText("");
                    resultArea.setText("Student Not Found!");
                }
            }

        } catch (SQLException ex) {

            studentFound = false;
            JOptionPane.showMessageDialog(this,
                    "Database Error: " + ex.getMessage());
        }
    }

    private void updateStudent() {

        if (!studentFound) {
            JOptionPane.showMessageDialog(this,
                    "First find the student!");
            return;
        }

        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String branch = branchField.getText().trim();

        if (name.isEmpty() || branch.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Enter Student Name and Branch!");
            return;
        }

        String sql = "UPDATE students SET student_name = ?, "
                + "department = ? WHERE student_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, branch);
            ps.setString(3, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {

                resultArea.setText(
                        "Student Updated Successfully!\n\n"
                        + "Student ID: " + id + "\n"
                        + "Student Name: " + name + "\n"
                        + "Branch: " + branch
                );

                JOptionPane.showMessageDialog(this,
                        "Student Updated Successfully!");

            } else {
                studentFound = false;
                resultArea.setText("Student Not Found!");
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(this,
                    "Database Error: " + ex.getMessage());
        }
    }

    private void deleteStudent() {

        String id = idField.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Enter Student ID!");
            return;
        }

        if (!studentFound) {
            JOptionPane.showMessageDialog(this,
                    "First find the student!");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this student?\n\n"
                + "ID: " + id + "\n"
                + "Name: " + nameField.getText(),
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM students WHERE student_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {

                resultArea.setText("Student Deleted Successfully!");

                JOptionPane.showMessageDialog(this,
                        "Student Deleted Successfully!");

                idField.setText("");
                nameField.setText("");
                branchField.setText("");

                studentFound = false;

            } else {
                studentFound = false;
                resultArea.setText("Student Not Found!");
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(this,
                    "Unable to delete student: " + ex.getMessage()
                    + "\nCheck whether this student has course registrations "
                    + "or other related records.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentUpdateDelete());
    }
}
