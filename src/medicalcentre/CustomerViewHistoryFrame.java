package medicalcentre;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerViewHistoryFrame extends JFrame {
    private DefaultTableModel tableModel;
    private JTable historyTable;
    private User customer;

    public CustomerViewHistoryFrame(User customer) {
        this.customer = customer;
        setTitle("View History");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(102, 204, 204));
        setLayout(new BorderLayout());

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setPreferredSize(new Dimension(300, 60));
        JLabel titleLabel = new JLabel("View History");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titlePanel.add(titleLabel);

        JPanel northWrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        northWrapperPanel.setBackground(new Color(102, 204, 204));
        northWrapperPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        northWrapperPanel.add(titlePanel);
        add(northWrapperPanel, BorderLayout.NORTH);

        // Table Panel
        JPanel tableDisplayPanel = new JPanel(new BorderLayout());
        tableDisplayPanel.setBackground(Color.WHITE);
        tableDisplayPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        JLabel allHistoryLabel = new JLabel("Appointment History");
        allHistoryLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        tableDisplayPanel.add(allHistoryLabel, BorderLayout.NORTH);

        String[] columnNames = {"Appointment ID", "Doctor ID", "Date", "Time", "Charge", "Doctor Feedback"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        historyTable = new JTable(tableModel);
        historyTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        historyTable.setRowHeight(25);
        historyTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        historyTable.getTableHeader().setBackground(new Color(240, 240, 240));
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableDisplayPanel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        add(tableDisplayPanel, BorderLayout.CENTER);

        readHistory();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void readHistory() {
        tableModel.setRowCount(0);
        try {
            // Debugging: Log customer ID
            System.out.println("Loading history for customer ID: " + customer.getId());

            // Check if appointments.txt exists and read its contents
            if (!Files.exists(Paths.get("appointments.txt"))) {
                System.err.println("appointments.txt does not exist.");
                JOptionPane.showMessageDialog(this, "Appointments file not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            List<String> appointmentLines = Files.readAllLines(Paths.get("appointments.txt"));
            System.out.println("appointments.txt contents: " + appointmentLines);

            // Load and filter appointments
            List<Appointment> appointments = Appointment.loadAppointments().stream()
                    .filter(a -> {
                        boolean match = a.getCustomerId().equals(customer.getId()) && a.getStatus().equals("Completed");
                        System.out.println("Appointment ID: " + a.getAppointmentId() + ", Customer ID: " + a.getCustomerId() + ", Status: " + a.getStatus() + ", Match: " + match);
                        return match;
                    })
                    .collect(Collectors.toList());

            // Debugging: Log number of appointments found
            System.out.println("Found " + appointments.size() + " completed appointments for customer " + customer.getId());

            List<Payment> payments = Files.exists(Paths.get("payments.txt")) ? Payment.loadPayments() : List.of();
            List<DocFeedback> feedbacks = Files.exists(Paths.get("docfeedback.txt")) ? DocFeedback.loadFeedbacks("docfeedback.txt") : List.of();

            boolean hasData = false;
            for (Appointment a : appointments) {
                hasData = true;
                String paymentAmount = payments.stream()
                        .filter(p -> p.getAppointmentId().equals(a.getAppointmentId()))
                        .map(p -> String.format("%.2f", p.getAmount()))
                        .findFirst()
                        .orElse("No charge");
                String feedbackContent = feedbacks.stream()
                        .filter(f -> f.getAppointmentId().equals(a.getAppointmentId()))
                        .map(DocFeedback::getContent)
                        .findFirst()
                        .orElse("No feedback");
                tableModel.addRow(new Object[]{
                    a.getAppointmentId(),
                    a.getDoctorId(),
                    a.getDate(),
                    a.getTime(),
                    paymentAmount,
                    feedbackContent
                });
            }
            if (!hasData) {
                JOptionPane.showMessageDialog(this, "No completed appointments found.", "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            System.err.println("Error reading history: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error reading history: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
