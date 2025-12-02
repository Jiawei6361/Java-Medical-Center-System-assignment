package medicalcentre;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerViewAppointmentsFrame extends JFrame {
    private DefaultTableModel tableModel;
    private JTable appointmentTable;
    private User customer;

    public CustomerViewAppointmentsFrame(User customer) {
        this.customer = customer;
        setTitle("Check Appointments");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(102, 204, 204));
        setLayout(new BorderLayout());

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setPreferredSize(new Dimension(300, 60));
        JLabel titleLabel = new JLabel("Check Appointments");
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
        JLabel allAppointmentsLabel = new JLabel("Appointments");
        allAppointmentsLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        tableDisplayPanel.add(allAppointmentsLabel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Doctor ID", "Date", "Time", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        appointmentTable = new JTable(tableModel);
        appointmentTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        appointmentTable.setRowHeight(25);
        appointmentTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        appointmentTable.getTableHeader().setBackground(new Color(240, 240, 240));
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableDisplayPanel.add(new JScrollPane(appointmentTable), BorderLayout.CENTER);
        add(tableDisplayPanel, BorderLayout.CENTER);

        readAppointments();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void readAppointments() {
        tableModel.setRowCount(0);
        try {
            List<Appointment> appointments = Appointment.loadAppointments().stream()
                    .filter(a -> a.getCustomerId().equals(customer.getId()) && !a.getStatus().equals("Completed") && !a.getStatus().equals("Cancelled"))
                    .collect(Collectors.toList());
            for (Appointment a : appointments) {
                tableModel.addRow(new Object[]{
                    a.getAppointmentId(),
                    a.getDoctorId(),
                    a.getDate(),
                    a.getTime(),
                    a.getStatus()
                });
            }
            if (appointments.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No active appointments found.", "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            System.err.println("Error reading appointments: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error reading appointments: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
