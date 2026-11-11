
package collegeadmin.faculty;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import collegeadmin.DatabaseConnection;

public class FacultyModule extends JFrame {

    private JTextField idField;
    private JTextField nameField;
    private JTextField departmentField;
    private JTextArea resultArea;

    public FacultyModule() {

        setTitle("Faculty Management");
        setSize(500, 400);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        inputPanel.add(new JLabel("Faculty ID:"));
        idField = new JTextField();
        inputPanel.add(idField);

        inputPanel.add(new JLabel("Faculty Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Department:"));
        departmentField = new JTextField();
        inputPanel.add(departmentField);

        JButton addButton = new JButton("Add Faculty");
        JButton viewButton = new JButton("View Faculty");

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

        addButton.addActionListener(e -> addFaculty());
        viewButton.addActionListener(e -> viewFaculty());

        setVisible(true);
    }

    private void addFaculty() {

        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String department = departmentField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || department.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill all details!");
            return;
        }

        String sql = "INSERT INTO faculty "
                + "(faculty_id, faculty_name, department) "
                + "VALUES (?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, department);

            ps.executeUpdate();

            resultArea.setText(
                    "Faculty Added Successfully!\n\n"
                    + "Faculty ID: " + id + "\n"
                    + "Faculty Name: " + name + "\n"
                    + "Department: " + department
            );

            JOptionPane.showMessageDialog(this,
                    "Faculty Added Successfully!");

            idField.setText("");
            nameField.setText("");
            departmentField.setText("");

        } catch (SQLException ex) {

            if ("23000".equals(ex.getSQLState())) {
                JOptionPane.showMessageDialog(this,
                        "Faculty ID Already Exists!");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Database Error: " + ex.getMessage());
            }
        }
    }

    private void viewFaculty() {

        String sql = "SELECT * FROM faculty ORDER BY faculty_id";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder details = new StringBuilder();

            details.append("FACULTY DETAILS\n");
            details.append("------------------------------\n\n");

            boolean found = false;

            while (rs.next()) {

                found = true;

                details.append("Faculty ID: ")
                        .append(rs.getString("faculty_id"))
                        .append("\n");

                details.append("Faculty Name: ")
                        .append(rs.getString("faculty_name"))
                        .append("\n");

                details.append("Department: ")
                        .append(rs.getString("department"))
                        .append("\n");

                details.append("------------------------------\n");
            }

            if (found) {
                resultArea.setText(details.toString());
            } else {
                resultArea.setText("No Faculty Records Found!");
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(this,
                    "Database Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new FacultyModule());
    }
}
