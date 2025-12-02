package medicalcentre;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ManagerReportsFrame extends JFrame {
    private static final String USERS_FILE = "users.txt";
    private static final String APPOINTMENTS_FILE = "appointments.txt";
    private static final String FEEDBACKS_FILE = "feedbacks.txt";
    private static final String DOC_FEEDBACKS_FILE = "docfeedback.txt";

    public ManagerReportsFrame() {
        setTitle("Manager Reports");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(102, 204, 204));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(102, 204, 204));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JButton ageReportBtn = createStyledButton("Age Report");
        ageReportBtn.addActionListener(e -> generateAgeReport());
        mainPanel.add(ageReportBtn);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton genderReportBtn = createStyledButton("Gender Report");
        genderReportBtn.addActionListener(e -> generateGenderReport());
        mainPanel.add(genderReportBtn);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton doctorAppointmentReportBtn = createStyledButton("Doctor Appointment Report");
        doctorAppointmentReportBtn.addActionListener(e -> generateDoctorAppointmentReport());
        mainPanel.add(doctorAppointmentReportBtn);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton generalReportBtn = createStyledButton("General Report");
        generalReportBtn.addActionListener(e -> generateGeneralReport());
        mainPanel.add(generalReportBtn);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton doctorFeedbackReportBtn = createStyledButton("Doctor Feedback Report");
        doctorFeedbackReportBtn.addActionListener(e -> generateDoctorFeedbackReport());
        mainPanel.add(doctorFeedbackReportBtn);

        add(mainPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(300, 50));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setFocusPainted(false);
        return button;
    }

    private void generateAgeReport() {
        try {
            Path userFile = Path.of(USERS_FILE);
            if (!Files.exists(userFile)) {
                Files.createFile(userFile);
                JOptionPane.showMessageDialog(this, "No customers found.", "Age Report", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            List<User> users = User.loadFromFile(USERS_FILE).stream()
                    .filter(u -> u.getRole().equals("Customer"))
                    .collect(Collectors.toList());
            System.out.println("Age Report: Loaded " + users.size() + " customers from " + USERS_FILE);

            long kids = users.stream().filter(u -> u.getAge() >= 10 && u.getAge() <= 17).count();
            long adults = users.stream().filter(u -> u.getAge() >= 18).count();

            String report = "Age Report:\n" +
                           "Kids (10-17): " + kids + "\n" +
                           "Adults (18+): " + adults;
            JOptionPane.showMessageDialog(this, report, "Age Report", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            System.err.println("Error generating Age Report: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to generate Age Report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateGenderReport() {
        try {
            Path userFile = Path.of(USERS_FILE);
            if (!Files.exists(userFile)) {
                Files.createFile(userFile);
                JOptionPane.showMessageDialog(this, "No customers found.", "Gender Report", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            List<User> users = User.loadFromFile(USERS_FILE).stream()
                    .filter(u -> u.getRole().equals("Customer"))
                    .collect(Collectors.toList());
            System.out.println("Gender Report: Loaded " + users.size() + " customers from " + USERS_FILE);

            long males = users.stream().filter(u -> u.getGender().equalsIgnoreCase("Male")).count();
            long females = users.stream().filter(u -> u.getGender().equalsIgnoreCase("Female")).count();

            String report = "Gender Report:\n" +
                           "Males: " + males + "\n" +
                           "Females: " + females;
            JOptionPane.showMessageDialog(this, report, "Gender Report", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            System.err.println("Error generating Gender Report: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to generate Gender Report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateDoctorAppointmentReport() {
        try {
            Path appFile = Path.of(APPOINTMENTS_FILE);
            if (!Files.exists(appFile)) {
                Files.createFile(appFile);
                JOptionPane.showMessageDialog(this, "No appointments found.", "Doctor Appointment Report", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            List<String> lines = Files.readAllLines(appFile);
            System.out.println("Doctor Appointment Report: Contents of " + APPOINTMENTS_FILE + ": " + lines);
            List<Appointment> appointments = Appointment.loadAppointments();
            System.out.println("Doctor Appointment Report: Loaded " + appointments.size() + " appointments");

            Map<String, Long> doctorAppointments = appointments.stream()
                    .filter(a -> !a.getDoctorId().isEmpty())
                    .collect(Collectors.groupingBy(Appointment::getDoctorId, Collectors.counting()));

            StringBuilder report = new StringBuilder("Doctor Appointment Report:\n");
            if (doctorAppointments.isEmpty()) {
                report.append("No doctor appointments found.");
            } else {
                doctorAppointments.forEach((docId, count) -> report.append("Doctor ").append(docId).append(": ").append(count).append("\n"));
            }
            JOptionPane.showMessageDialog(this, report.toString(), "Doctor Appointment Report", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            System.err.println("Error generating Doctor Appointment Report: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to generate Doctor Appointment Report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateGeneralReport() {
        try {
            Path appFile = Path.of(APPOINTMENTS_FILE);
            if (!Files.exists(appFile)) {
                Files.createFile(appFile);
            }
            List<Appointment> appointments = Appointment.loadAppointments();
            System.out.println("Loaded " + appointments.size() + " appointments for General Report");

            int totalAppointments = appointments.size();
            long pending = appointments.stream().filter(a -> a.getStatus().equalsIgnoreCase("Pending")).count();
            long completed = appointments.stream().filter(a -> a.getStatus().equalsIgnoreCase("Completed")).count();
            long cancelled = appointments.stream().filter(a -> a.getStatus().equalsIgnoreCase("Cancelled")).count();

            String report = "General Report:\n" +
                           "Total Appointments: " + totalAppointments + "\n" +
                           "  - Pending: " + pending + "\n" +
                           "  - Completed: " + completed + "\n" +
                           "  - Cancelled: " + cancelled;
            JOptionPane.showMessageDialog(this, report, "General Report", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            System.err.println("Error generating General Report: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to generate General Report. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateDoctorFeedbackReport() {
        try {
            Path docFeedbackFile = Path.of(DOC_FEEDBACKS_FILE);
            if (!Files.exists(docFeedbackFile)) {
                Files.createFile(docFeedbackFile);
                JOptionPane.showMessageDialog(this, "No doctor feedbacks found.", "Doctor Feedback Report", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            List<String> lines = Files.readAllLines(docFeedbackFile);
            System.out.println("Doctor Feedback Report: Contents of " + DOC_FEEDBACKS_FILE + ": " + lines);
            List<DocFeedback> docFeedbacks = DocFeedback.loadFeedbacks(DOC_FEEDBACKS_FILE);
            System.out.println("Doctor Feedback Report: Loaded " + docFeedbacks.size() + " doctor feedbacks");

            int totalDocFeedbacks = docFeedbacks.size();
            StringBuilder report = new StringBuilder("Doctor Feedback Report:\n");
            report.append("Total Doctor Feedbacks: ").append(totalDocFeedbacks).append("\n");
            if (docFeedbacks.isEmpty()) {
                report.append("No doctor feedbacks found.");
            } else {
                for (DocFeedback df : docFeedbacks) {
                    report.append("ID: ").append(df.getFeedbackId())
                          .append(", Appointment: ").append(df.getAppointmentId())
                          .append(", Doctor: ").append(df.getDoctorId())
                          .append(", Customer: ").append(df.getCustomerId())
                          .append(", Content: ").append(df.getContent()).append("\n");
                }
            }
            JOptionPane.showMessageDialog(this, report.toString(), "Doctor Feedback Report", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            System.err.println("Error generating Doctor Feedback Report: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to generate Doctor Feedback Report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
