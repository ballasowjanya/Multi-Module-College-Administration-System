
package collegeadmin;

import javax.swing.*;
import java.awt.*;

import collegeadmin.student.StudentAddSearch;
import collegeadmin.student.StudentUpdateDelete;

import collegeadmin.faculty.FacultyModule;

import collegeadmin.course.CourseModule;
import collegeadmin.course.CourseRegistration;

import collegeadmin.attendance.AttendanceRecord;
import collegeadmin.attendance.AttendanceShortage;

import collegeadmin.examination.MarksEntry;
import collegeadmin.examination.ResultGrade;

public class MainDashboard extends JFrame {

    public MainDashboard() {

        setTitle("College Administration System");
        setSize(600, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(10, 1, 10, 10));

        JLabel heading = new JLabel(
            "COLLEGE ADMINISTRATION SYSTEM",
            JLabel.CENTER
        );

        heading.setFont(new Font("Arial", Font.BOLD, 20));

        JButton studentAdd = new JButton("Student Add / Search");
        JButton studentUpdate = new JButton("Student Update / Delete");
        JButton faculty = new JButton("Faculty Management");
        JButton course = new JButton("Course Management");
        JButton registration = new JButton("Course Registration");
        JButton attendance = new JButton("Attendance Record");
        JButton shortage = new JButton("Attendance Shortage");
        JButton marksEntry = new JButton("Marks Entry");
        JButton resultGrade = new JButton("Result / Grade");

        studentAdd.addActionListener(e ->
            new StudentAddSearch()
        );

        studentUpdate.addActionListener(e ->
            new StudentUpdateDelete()
        );

        faculty.addActionListener(e ->
            new FacultyModule()
        );

        course.addActionListener(e ->
            new CourseModule()
        );

        registration.addActionListener(e ->
            new CourseRegistration()
        );

        attendance.addActionListener(e ->
            new AttendanceRecord()
        );

        shortage.addActionListener(e ->
            new AttendanceShortage()
        );

        marksEntry.addActionListener(e ->
            new MarksEntry()
        );

        resultGrade.addActionListener(e ->
            new ResultGrade()
        );

        panel.add(heading);
        panel.add(studentAdd);
        panel.add(studentUpdate);
        panel.add(faculty);
        panel.add(course);
        panel.add(registration);
        panel.add(attendance);
        panel.add(shortage);
        panel.add(marksEntry);
        panel.add(resultGrade);

        add(panel);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainDashboard());
    }
}
