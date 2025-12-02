package medicalcentre;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDateTime;

public class StaffUI extends JFrame {
    private StaffService service = new StaffService();
    private DefaultTableModel customerTableModel;
    private DefaultTableModel appointmentTableModel;
    private DefaultTableModel paymentTableModel;
    private JTable customerTable;
    private JTable appointmentTable;
    private JTable paymentTable;
    private User currentStaff;

    public StaffUI(User staff) {
        this.currentStaff = staff;
        setTitle("Staff Dashboard - " + staff.getName());
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        // Customer Table
        String[] customerColumns = {"ID", "Name", "Gender", "Email", "Phone", "Age"};
        customerTableModel = new DefaultTableModel(customerColumns, 0);
        customerTable = new JTable(customerTableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerTable.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel customerPanel = new JPanel(new BorderLayout());
        customerPanel.add(new JScrollPane(customerTable), BorderLayout.CENTER);
        JPanel customerButtons = new JPanel();
        customerButtons.add(createButton("Add Customer", e -> addCustomer()));
        customerButtons.add(createButton("Edit Customer", e -> editCustomer()));
        customerButtons.add(createButton("Delete Customer", e -> deleteCustomer()));
        customerPanel.add(customerButtons, BorderLayout.SOUTH);

        // Appointment Table
        String[] appointmentColumns = {"ID", "Customer ID", "Doctor ID", "Date", "Time", "Staff ID", "Status"};
        appointmentTableModel = new DefaultTableModel(appointmentColumns, 0);
        appointmentTable = new JTable(appointmentTableModel);
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentTable.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel appointmentPanel = new JPanel(new BorderLayout());
        appointmentPanel.add(new JScrollPane(appointmentTable), BorderLayout.CENTER);
        JPanel appointmentButtons = new JPanel();
        appointmentButtons.add(createButton("Add Appointment", e -> addAppointment()));
        appointmentButtons.add(createButton("Assign Doctor", e -> assignDoctor()));
        appointmentButtons.add(createButton("Delete Appointment", e -> deleteAppointment()));
        appointmentPanel.add(appointmentButtons, BorderLayout.SOUTH);

        // Payment Table
        String[] paymentColumns = {"ID", "Appointment ID", "Customer ID", "Doctor ID", "Amount", "Status"};
        paymentTableModel = new DefaultTableModel(paymentColumns, 0);
        paymentTable = new JTable(paymentTableModel);
        paymentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        paymentTable.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel paymentPanel = new JPanel(new BorderLayout());
        paymentPanel.add(new JScrollPane(paymentTable), BorderLayout.CENTER);
        JPanel paymentButtons = new JPanel();
        paymentButtons.add(createButton("Pay Now", e -> payNow()));
        paymentButtons.add(createButton("View Receipts", e -> new ReceiptListUI()));
        paymentPanel.add(paymentButtons, BorderLayout.SOUTH);

        tabs.add("Customers", customerPanel);
        tabs.add("Appointments", appointmentPanel);
        tabs.add("Payments", paymentPanel);

        add(tabs);
        refreshAll();
        setVisible(true);
    }

    private JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(new Color(51, 153, 153));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.addActionListener(listener);
        return button;
    }

    private void refreshAll() {
        // Refresh Customers
        customerTableModel.setRowCount(0);
        try {
            for (User c : service.listCustomers()) {
                customerTableModel.addRow(new Object[]{
                    c.getId(), c.getName(), c.getGender(), c.getEmail(), c.getPhone(), c.getAge()
                });
            }
        } catch (IOException e) {
            System.err.println("Error loading customers: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to load customers. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Refresh Appointments
        appointmentTableModel.setRowCount(0);
        try {
            for (Appointment a : service.listAppointments()) {
                appointmentTableModel.addRow(new Object[]{
                    a.getAppointmentId(), a.getCustomerId(), a.getDoctorId(), a.getDate(), a.getTime(), a.getStaffId(), a.getStatus()
                });
            }
        } catch (IOException e) {
            System.err.println("Error loading appointments: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to load appointments. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Refresh Payments
        paymentTableModel.setRowCount(0);
        try {
            for (Payment p : Payment.loadPayments()) {
                paymentTableModel.addRow(new Object[]{
                    p.id, p.appointmentId, p.customerId, p.doctorId, p.amount, p.status
                });
            }
        } catch (IOException e) {
            System.err.println("Error loading payments: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to load payments. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addCustomer() {
        String name = JOptionPane.showInputDialog(this, "Name:");
        if (name == null) return;
        String password = JOptionPane.showInputDialog(this, "Password:");
        if (password == null) return;
        String phone = JOptionPane.showInputDialog(this, "Phone:");
        String email = JOptionPane.showInputDialog(this, "Email:");
        String gender = JOptionPane.showInputDialog(this, "Gender:");
        String ageStr = JOptionPane.showInputDialog(this, "Age:");
        try {
            int age = Integer.parseInt(ageStr);
            service.createCustomer(name, password, phone, email, gender, age);
            refreshAll();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid age entered.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            System.err.println("Error creating customer: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to create customer. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a customer first.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) customerTableModel.getValueAt(selectedRow, 0);
        String name = JOptionPane.showInputDialog(this, "Name:", customerTableModel.getValueAt(selectedRow, 1));
        String password = JOptionPane.showInputDialog(this, "Password:");
        String phone = JOptionPane.showInputDialog(this, "Phone:", customerTableModel.getValueAt(selectedRow, 4));
        String email = JOptionPane.showInputDialog(this, "Email:", customerTableModel.getValueAt(selectedRow, 3));
        String gender = JOptionPane.showInputDialog(this, "Gender:", customerTableModel.getValueAt(selectedRow, 2));
        String ageStr = JOptionPane.showInputDialog(this, "Age:", customerTableModel.getValueAt(selectedRow, 5));
        try {
            int age = Integer.parseInt(ageStr);
            service.updateCustomer(id, name, password, phone, email, gender, age);
            refreshAll();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid age entered.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            System.err.println("Error updating customer: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to update customer. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a customer first.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) customerTableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this customer?");
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.deleteCustomer(id);
                refreshAll();
            } catch (IOException ex) {
                System.err.println("Error deleting customer: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Failed to delete customer. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addAppointment() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a customer first.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String customerId = (String) customerTableModel.getValueAt(selectedRow, 0);
        String doctorId = JOptionPane.showInputDialog(this, "Enter Doctor ID (D001…):");
        if (doctorId == null) return;
        String date = JOptionPane.showInputDialog(this, "Enter Date (yyyy-MM-dd):");
        if (date == null) return;
        String time = JOptionPane.showInputDialog(this, "Enter Time (HH:mm):");
        if (time == null) return;
        try {
            LocalDateTime dateTime = LocalDateTime.parse(date + "T" + time + ":00");
            service.assistBooking(customerId, doctorId, dateTime, currentStaff.getId());
            refreshAll();
        } catch (Exception ex) {
            System.err.println("Error creating appointment: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to create appointment. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void assignDoctor() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an appointment first.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String appointmentId = (String) appointmentTableModel.getValueAt(selectedRow, 0);
        String docId = JOptionPane.showInputDialog(this, "Doctor ID:");
        if (docId == null) return;
        try {
            service.assignAppointment(appointmentId, docId);
            refreshAll();
        } catch (IOException ex) {
            System.err.println("Error assigning doctor: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to assign doctor. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an appointment to delete.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String appointmentId = (String) appointmentTableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this appointment?");
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Appointment.deleteById(appointmentId);
                refreshAll();
            } catch (IOException ex) {
                System.err.println("Error deleting appointment: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Failed to delete appointment. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void payNow() {
        int selectedRow = paymentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a payment first.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String paymentId = (String) paymentTableModel.getValueAt(selectedRow, 0);
        String status = (String) paymentTableModel.getValueAt(selectedRow, 5);
        if (!"Pending".equals(status)) {
            JOptionPane.showMessageDialog(this, "Select a Pending payment.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double amount = (Double) paymentTableModel.getValueAt(selectedRow, 4);
        int confirm = JOptionPane.showConfirmDialog(this, "Pay now for " + amount + "?");
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Payment selectedPayment = Payment.loadPayments().stream()
                        .filter(p -> p.id.equals(paymentId))
                        .findFirst()
                        .orElse(null);
                if (selectedPayment != null) {
                    service.payNow(selectedPayment);
                    refreshAll();
                    JOptionPane.showMessageDialog(this, "Payment processed successfully. Appointment marked as Completed.");
                } else {
                    JOptionPane.showMessageDialog(this, "Payment not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                System.err.println("Error processing payment: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Failed to process payment. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
