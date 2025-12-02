package medicalcentre;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DoctorDashboard {
    private JFrame frame;

    public DoctorDashboard(User doctor) {
        frame = new JFrame("Doctor Dashboard - " + doctor.getName());
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(6, 1));

        JButton viewAppointmentsButton = createButton("View Appointments");
        JButton viewHistoryButton = createButton("View History (Feedbacks)");
        JButton addChargesButton = createButton("Assign Charge");
        JButton viewFeedbackButton = createButton("View Feedback");
        JButton editProfileButton = createButton("Edit Profile");
        JButton exitButton = createButton("Exit");

        viewAppointmentsButton.addActionListener(e -> {
            try {
                List<Appointment> appointments = Appointment.loadAppointments();
                StringBuilder sb = new StringBuilder("Appointments:\n");
                appointments.stream()
                        .filter(a -> a.getDoctorId().equals(doctor.getId()) && !a.getStatus().equals("Completed") && !a.getStatus().equals("Cancelled"))
                        .forEach(a -> sb.append("ID: ").append(a.getAppointmentId())
                                .append(", Customer: ").append(a.getCustomerId())
                                .append(", Date: ").append(a.getDate())
                                .append(", Time: ").append(a.getTime())
                                .append(", Status: ").append(a.getStatus()).append("\n"));
                JOptionPane.showMessageDialog(frame, sb.length() > 13 ? sb.toString() : "No active appointments found.");
            } catch (IOException ex) {
                System.err.println("Error loading appointments: " + ex.getMessage());
                JOptionPane.showMessageDialog(frame, "Failed to load appointments. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        viewHistoryButton.addActionListener(e -> {
            try {
                List<Appointment> appointments = Appointment.loadAppointments();
                List<DocFeedback> feedbacks = DocFeedback.loadFeedbacks("docfeedback.txt");
                StringBuilder sb = new StringBuilder("Completed Appointments and Feedbacks:\n");
                boolean hasData = false;

                // Get all feedbacks for the doctor, sorted by appointmentId
                List<DocFeedback> doctorFeedbacks = feedbacks.stream()
                        .filter(f -> f.getDoctorId().equals(doctor.getId()))
                        .sorted(Comparator.comparing(DocFeedback::getAppointmentId))
                        .collect(Collectors.toList());

                for (DocFeedback df : doctorFeedbacks) {
                    // Check if the appointment exists and is completed
                    Appointment app = appointments.stream()
                            .filter(a -> a.getAppointmentId().equals(df.getAppointmentId()) && "Completed".equals(a.getStatus()))
                            .findFirst()
                            .orElse(null);
                    if (app != null) {
                        hasData = true;
                        sb.append("Appointment ID: ").append(df.getAppointmentId())
                                .append(", Customer: ").append(df.getCustomerId())
                                .append(", Date: ").append(app.getDate())
                                .append(", Time: ").append(app.getTime())
                                .append("\nFeedback: ").append(df.getContent()).append("\n\n");
                    }
                }
                JOptionPane.showMessageDialog(frame, hasData ? sb.toString() : "No completed appointments found.", "History", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                System.err.println("Error loading history: " + ex.getMessage());
                JOptionPane.showMessageDialog(frame, "Failed to load history. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        addChargesButton.addActionListener(e -> {
            try {
                List<Appointment> appointments = Appointment.loadAppointments().stream()
                        .filter(a -> a.getDoctorId().equals(doctor.getId()) && !"Completed".equals(a.getStatus()))
                        .collect(Collectors.toList());
                if (appointments.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "No eligible appointments to charge.");
                    return;
                }
                // Filter out appointments that are already Pending
                List<Appointment> eligibleAppointments = appointments.stream()
                        .filter(a -> !"Pending".equals(a.getStatus()))
                        .collect(Collectors.toList());
                if (eligibleAppointments.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "No eligible appointments to charge.");
                    return;
                }
                String[] appIds = eligibleAppointments.stream().map(Appointment::getAppointmentId).toArray(String[]::new);
                String appId = (String) JOptionPane.showInputDialog(frame, "Select Appointment:", "Assign Charge",
                        JOptionPane.QUESTION_MESSAGE, null, appIds, appIds[0]);
                if (appId == null) return;
                Appointment app = eligibleAppointments.stream().filter(a -> a.getAppointmentId().equals(appId)).findFirst().orElse(null);
                if (app == null) return;
                // Additional check to ensure the selected appointment isn't Pending (in case of manual selection error)
                if ("Pending".equals(app.getStatus())) {
                    JOptionPane.showMessageDialog(frame, "Cannot assign charge. This appointment is already Pending.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String amountStr = JOptionPane.showInputDialog(frame, "Enter Amount:");
                double amount = Double.parseDouble(amountStr);
                Path paymentFile = Path.of("payments.txt");
                if (!Files.exists(paymentFile)) {
                    Files.createFile(paymentFile);
                }
                Payment payment = new Payment(Payment.generateNewPaymentId(), app.getAppointmentId(), app.getCustomerId(),
                        doctor.getId(), amount, "Pending");
                payment.saveToFile();
                String feedback = JOptionPane.showInputDialog(frame, "Enter feedback for the patient (e.g., Sleep more):");
                if (feedback != null && !feedback.trim().isEmpty()) {
                    Path feedbackFile = Path.of("docfeedback.txt");
                    if (!Files.exists(feedbackFile)) {
                        Files.createFile(feedbackFile);
                    }
                    DocFeedback docFeedback = new DocFeedback(DocFeedback.generateNewFeedbackId(), app.getAppointmentId(),
                            doctor.getId(), app.getCustomerId(), feedback.trim());
                    docFeedback.saveToFile();
                }
                Appointment.updateStatus(app.getAppointmentId(), "Pending");
                JOptionPane.showMessageDialog(frame, "Charge and feedback assigned successfully. Appointment set to Pending.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid amount entered.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                System.err.println("Error assigning charge or feedback: " + ex.getMessage());
                JOptionPane.showMessageDialog(frame, "Failed to assign charge or feedback. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        viewFeedbackButton.addActionListener(e -> {
            try {
                List<CustomerComment> comments = CustomerComment.loadComments();
                StringBuilder sb = new StringBuilder("Customer Comments for Doctor:\n");
                boolean hasData = false;

                // Filter and sort comments for the doctor
                List<CustomerComment> doctorComments = comments.stream()
                        .filter(c -> c.getTargetId().equals(doctor.getId()))
                        .sorted(Comparator.comparing(CustomerComment::getCommentId))
                        .collect(Collectors.toList());

                for (CustomerComment c : doctorComments) {
                    hasData = true;
                    sb.append("Comment ID: ").append(c.getCommentId())
                            .append(", Customer: ").append(c.getCustomerId())
                            .append(", Content: ").append(c.getContent()).append("\n");
                }
                JOptionPane.showMessageDialog(frame, hasData ? sb.toString() : "No comments found for this doctor.", "Feedback", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                System.err.println("Error loading comments: " + ex.getMessage());
                JOptionPane.showMessageDialog(frame, "Failed to load comments. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        editProfileButton.addActionListener(e -> new DoctorEditProfileFrame(doctor));

        exitButton.addActionListener(e -> System.exit(0));

        frame.add(viewAppointmentsButton);
        frame.add(viewHistoryButton);
        frame.add(addChargesButton);
        frame.add(viewFeedbackButton);
        frame.add(editProfileButton);
        frame.add(exitButton);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(new Color(51, 153, 153));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }
}