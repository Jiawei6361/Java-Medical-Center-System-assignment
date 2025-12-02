package medicalcentre;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Payment {
    public String id;
    public String appointmentId;
    public String customerId;
    public String doctorId;
    public double amount;
    public String status; // Pending, Paid

    public Payment(String id, String appointmentId, String customerId, String doctorId, double amount, String status) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.doctorId = doctorId;
        this.amount = amount;
        this.status = status;
    }

    @Override
    public String toString() {
        String displayStatus = "Paid".equals(status) ? "Received" : status;
        return id + " | Appointment:" + appointmentId + " | Customer:" + customerId +
               " | Doctor:" + doctorId + " | Amount:" + amount + " | Status:" + displayStatus;
    }

    public String toCsv() {
        return String.join(",", id, appointmentId, customerId, doctorId, String.valueOf(amount), status);
    }

    public static Payment fromCsv(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 6) return null;
        try {
            return new Payment(parts[0], parts[1], parts[2], parts[3],
                               Double.parseDouble(parts[4]), parts[5]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid amount format in payments.txt: " + line);
            return null;
        }
    }

    public static List<Payment> loadPayments() throws IOException {
        List<Payment> list = new ArrayList<>();
        Path file = Path.of("payments.txt");
        if (!Files.exists(file)) {
            Files.createFile(file);
            return list;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                Payment pay = fromCsv(line);
                if (pay != null) list.add(pay);
            }
        }
        return list;
    }

    public static void savePayments(List<Payment> list) throws IOException {
        Path file = Path.of("payments.txt");
        if (!Files.exists(file)) {
            Files.createFile(file);
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.toFile()))) {
            for (Payment p : list) {
                bw.write(p.toCsv());
                bw.newLine();
            }
        }
    }

    public void saveToFile() throws IOException {
        Path file = Path.of("payments.txt");
        if (!Files.exists(file)) {
            Files.createFile(file);
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.toFile(), true))) {
            bw.write(toCsv());
            bw.newLine();
        }
    }

    public static String generateNewPaymentId() throws IOException {
        Path file = Path.of("payments.txt");
        if (!Files.exists(file)) {
            Files.createFile(file);
            return "P001";
        }
        int max = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length > 0 && parts[0].startsWith("P")) {
                    try {
                        int n = Integer.parseInt(parts[0].substring(1));
                        max = Math.max(max, n);
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        return String.format("P%03d", max + 1);
    }

    public static void updatePayment(Payment updated) throws IOException {
        List<Payment> payments = loadPayments();
        for (int i = 0; i < payments.size(); i++) {
            if (payments.get(i).id.equals(updated.id)) {
                payments.set(i, updated);
                break;
            }
        }
        savePayments(payments);
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }
}
