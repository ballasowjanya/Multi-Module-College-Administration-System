
package collegeadmin.examination;

import javax.swing.*;
import java.awt.*;

public class ResultGrade extends JFrame {

    JTextField marksField;
    JButton calculateButton;

    public ResultGrade() {

        setTitle("Result and Grade");
        setSize(400, 180);
        setLayout(new GridLayout(2, 2, 10, 10));

        add(new JLabel("Enter Marks (0-100):"));

        marksField = new JTextField();
        add(marksField);

        calculateButton = new JButton("Calculate Result");
        add(new JLabel(""));
        add(calculateButton);

        calculateButton.addActionListener(e -> calculateResult());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    void calculateResult() {

        String marksText = marksField.getText().trim();

        if (marksText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter marks!");
            return;
        }

        try {
            int marks = Integer.parseInt(marksText);

            if (marks < 0 || marks > 100) {
                JOptionPane.showMessageDialog(this,
                        "Enter marks between 0 and 100");
                return;
            }

            String grade;

            if (marks >= 90)
                grade = "A";
            else if (marks >= 80)
                grade = "B";
            else if (marks >= 70)
                grade = "C";
            else if (marks >= 60)
                grade = "D";
            else
                grade = "F";

            String result = marks >= 50 ? "Pass" : "Fail";

            JOptionPane.showMessageDialog(this,
                    "Marks: " + marks +
                    "\nGrade: " + grade +
                    "\nResult: " + result);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numeric marks!");
        }
    }

    public static void main(String[] args) {
        new ResultGrade();
    }
}
