package medicalcentre;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class StaffService {
    public User createCustomer(String name, String password, String phone, String email, String gender, int age) throws IOException {
        String id = User.generateNewId("Customer", "users.txt");
        User customer = new Customer(id, name, password, gender, email, phone, age);
        customer.saveToFile("users.txt");
        return customer;
    }

    public List<User> listCustomers() throws IOException {
        return User.loadFromFile("users.txt").stream()
                .filter(u -> "Customer".equals(u.getRole()))
                .collect(Collectors.toList());
    }

    public boolean updateCustomer(String id, String name, String password, String phone, String email, String gender, int age) throws IOException {
        User updated = new Customer(id, name, password, gender, email, phone, age);
        User.updateInFile("users.txt", updated);
        return true;
    }

    public boolean deleteCustomer(String id) throws IOException {
        User.deleteFromFile("users.txt", id);
        return true;
    }

    public Appointment assistBooking(String customerId, String doctorIdOrEmpty, LocalDateTime dateTime, String staffId) throws IOException {
        String appointmentId = Appointment.generateNewAppointmentId();
        String date = dateTime.toLocalDate().toString();
        String time = dateTime.toLocalTime().toString();
        String status = (doctorIdOrEmpty == null || doctorIdOrEmpty.isEmpty()) ? "Pending" : "Assigned";
        Appointment appt = new Appointment(appointmentId, customerId, (doctorIdOrEmpty == null ? "" : doctorIdOrEmpty), date, time, staffId, status);
        appt.saveToFile();
        return appt;
    }

    public List<Appointment> listAppointments() throws IOException {
        return Appointment.loadAppointments();
    }

    public boolean assignAppointment(String appointmentId, String doctorId) throws IOException {
        List<Appointment> appts = Appointment.loadAppointments();
        boolean updated = false;
        for (int i = 0; i < appts.size(); i++) {
            Appointment a = appts.get(i);
            if (a.getAppointmentId().equals(appointmentId)) {
                appts.set(i, new Appointment(a.getAppointmentId(), a.getCustomerId(), doctorId, a.getDate(), a.getTime(), a.getStaffId(), "Assigned"));
                updated = true;
                break;
            }
        }
        if (updated) Appointment.saveAppointments(appts);
        return updated;
    }

    public void payNow(Payment payment) throws IOException {
        Payment updated = new Payment(payment.id, payment.appointmentId, payment.customerId, payment.doctorId, payment.amount, "Paid");
        Payment.updatePayment(updated);

        // Update appointment status to Completed
        Appointment.updateStatus(payment.appointmentId, "Completed");

        // Append receipt to single file "receipts.txt"
        List<String> receiptLines = new ArrayList<>();
        receiptLines.add("=== APU Medical Centre - Receipt ===");
        receiptLines.add("Receipt ID: " + updated.id);
        receiptLines.add("Appointment ID: " + updated.appointmentId);
        receiptLines.add("Customer ID: " + updated.customerId);
        receiptLines.add("Doctor ID: " + updated.doctorId);
        receiptLines.add(String.format("Amount Paid: %.2f", updated.amount));
        receiptLines.add("Thank you for using APU Medical Centre.");
        receiptLines.add("=== END OF RECEIPT ===");

        Path receiptFile = Paths.get("receipts.txt");
        if (!Files.exists(receiptFile)) {
            Files.createFile(receiptFile);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(receiptFile.toFile(), true))) {
            for (String line : receiptLines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    public String getReceipts(String receiptId) throws IOException {
        Path receiptFile = Paths.get("receipts.txt");
        if (!Files.exists(receiptFile)) {
            return "No receipts found.";
        }
        List<String> allLines = Files.readAllLines(receiptFile);
        StringBuilder result = new StringBuilder();
        boolean found = false;

        if ("all".equalsIgnoreCase(receiptId)) {
            return String.join("\n", allLines);
        } else {
            for (int i = 0; i < allLines.size(); i++) {
                if (allLines.get(i).contains("Receipt ID: " + receiptId)) {
                    found = true;
                    while (i < allLines.size() && !allLines.get(i).startsWith("===")) {
                        result.append(allLines.get(i)).append("\n");
                        i++;
                    }
                    break;
                }
            }
            return found ? result.toString() : "Receipt ID " + receiptId + " not found.";
        }
    }

    public List<String> getReceiptIds() throws IOException {
        Path receiptFile = Paths.get("receipts.txt");
        if (!Files.exists(receiptFile)) {
            return new ArrayList<>();
        }
        return Files.readAllLines(receiptFile).stream()
                .filter(line -> line.contains("Receipt ID: "))
                .map(line -> line.replace("Receipt ID: ", "").trim())
                .collect(Collectors.toList());
    }
}