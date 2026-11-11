
package collegeadmin.student;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import collegeadmin.DatabaseConnection;

public class StudentAddSearch extends JFrame {

    private JTextField idField, nameField, branchField;
    private JTextArea resultArea;

    public StudentAddSearch() {

        setTitle("Student Add and Search");
        setSize(450, 350);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        inputPanel.add(new JLabel("Student ID:"));
        idField = new JTextField();
        inputPanel.add(idField);

        inputPanel.add(new JLabel("Student Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Branch:"));
        branchField = new JTextField();
        inputPanel.add(branchField);

        JButton addButton = new JButton("Add Student");
        JButton searchButton = new JButton("Search Student");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(searchButton);

        resultArea = new JTextArea();
        resultArea.setEditable(false);

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        addButton.addActionListener(e -> addStudent());
        searchButton.addActionListener(e -> searchStudent());

        setVisible(true);
    }

    private void addStudent() {

        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String branch = branchField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || branch.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill all details!");
            return;
        }

        String sql = "INSERT INTO students "
                + "(student_id, student_name, department) "
                + "VALUES (?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, branch);

            ps.executeUpdate();

            resultArea.setText(
                    "Student Added Successfully!\n\n"
                    + "Student ID: " + id + "\n"
                    + "Student Name: " + name + "\n"
                    + "Branch: " + branch
            );

            JOptionPane.showMessageDialog(this,
                    "Student Added Successfully!");

            idField.setText("");
            nameField.setText("");
            branchField.setText("");

        } catch (SQLException ex) {

            if ("23000".equals(ex.getSQLState())) {
                JOptionPane.showMessageDialog(this,
                        "Student ID Already Exists!");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Database Error: " + ex.getMessage());
            }
        }
    }

    private void searchStudent() {

        String id = idField.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Enter Student ID!");
            return;
        }

        String sql = "SELECT * FROM students WHERE student_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    resultArea.setText(
                            "Student Found!\n\n"
                            + "Student ID: "
                            + rs.getString("student_id") + "\n"
                            + "Student Name: "
                            + rs.getString("student_name") + "\n"
                            + "Branch: "
                            + rs.getString("department")
                    );

                } else {
                    resultArea.setText("Student Not Found!");
                }
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(this,
                    "Database Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentAddSearch());
    }
}
