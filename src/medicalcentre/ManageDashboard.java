package medicalcentre;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ManageDashboard extends JFrame {
    private static final String APPOINTMENTS_FILE = "appointments.txt";
    private static final String FEEDBACKS_FILE = "customercomments.txt";

    public ManageDashboard() {
        setTitle("Manager Dashboard");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(102, 204, 204));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(102, 204, 204));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JButton manageManagersBtn = createStyledButton("Manage Manager");
        manageManagersBtn.addActionListener(e -> new ManageUserFrame("Manager"));
        mainPanel.add(manageManagersBtn);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton manageStaffBtn = createStyledButton("Manage Staff");
        manageStaffBtn.addActionListener(e -> new ManageUserFrame("Staff"));
        mainPanel.add(manageStaffBtn);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton manageDoctorsBtn = createStyledButton("Manage Doctor");
        manageDoctorsBtn.addActionListener(e -> new ManageUserFrame("Doctor"));
        mainPanel.add(manageDoctorsBtn);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton viewAppointmentsBtn = createStyledButton("View all appointment");
        viewAppointmentsBtn.addActionListener(e -> viewAppointments());
        mainPanel.add(viewAppointmentsBtn);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton viewFeedbacksBtn = createStyledButton("View all feedback and comment");
        viewFeedbacksBtn.addActionListener(e -> viewFeedbacks());
        mainPanel.add(viewFeedbacksBtn);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton viewReportsBtn = createStyledButton("View Reports");
        viewReportsBtn.addActionListener(e -> new ManagerReportsFrame());
        mainPanel.add(viewReportsBtn);

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

    private void viewAppointments() {
        try {
            Path file = Path.of(APPOINTMENTS_FILE);
            if (!Files.exists(file)) {
                Files.createFile(file);
            }
            List<Appointment> appointments = Appointment.loadAppointments();
            StringBuilder sb = new StringBuilder("All Appointments:\n");
            if (appointments.isEmpty()) {
                sb.append("No appointments found.");
            } else {
                for (Appointment app : appointments) {
                    sb.append(app.toString()).append("\n");
                }
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "All Appointments", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            System.err.println("Error loading appointments: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to load appointments. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewFeedbacks() {
        try {
            Path file = Path.of(FEEDBACKS_FILE);
            if (!Files.exists(file)) {
                Files.createFile(file);
            }
            List<Feedback> feedbacks = Feedback.loadFeedbacks(FEEDBACKS_FILE);
            StringBuilder sb = new StringBuilder("All Feedbacks and Comments:\n");
            if (feedbacks.isEmpty()) {
                sb.append("No feedbacks found.");
            } else {
                for (Feedback fb : feedbacks) {
                    sb.append("ID: ").append(fb.getId())
                            .append(", User: ").append(fb.getUserId())
                            .append(", Content: ").append(fb.getContent()).append("\n");
                }
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "All Feedbacks", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            System.err.println("Error loading feedbacks: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to load feedbacks. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}