package medicalcentre;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Appointment {
    private String appointmentId; // A001…
    private String customerId;    // C001…
    private String doctorId;      // D001…
    private String date;          // yyyy-MM-dd
    private String time;          // HH:mm
    private String staffId;       // S001…
    private String status;        // Pending/Assigned/Completed/Cancelled

    public Appointment(String appointmentId, String customerId, String doctorId,
                       String date, String time, String staffId, String status) {
        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
        this.staffId = staffId;
        this.status = status;
    }

    @Override
    public String toString() {
        return appointmentId + " | Customer: " + customerId +
               " | Doctor: " + (doctorId.isEmpty() ? "N/A" : doctorId) +
               " | Date: " + date + " " + time +
               " | Staff: " + staffId + " | Status: " + status;
    }

    public String toCsv() {
        return String.join(",", appointmentId, customerId, doctorId, date, time, staffId, status);
    }

    public static String generateNewAppointmentId() throws IOException {
        Path file = Path.of("appointments.txt");
        if (!Files.exists(file)) {
            Files.createFile(file);
            return "A001";
        }
        int max = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length > 0 && parts[0].startsWith("A")) {
                    try {
                        int n = Integer.parseInt(parts[0].substring(1));
                        max = Math.max(max, n);
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        return String.format("A%03d", max + 1);
    }

    public void saveToFile() throws IOException {
        Path file = Path.of("appointments.txt");
        if (!Files.exists(file)) {
            Files.createFile(file);
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.toFile(), true))) {
            bw.write(toCsv());
            bw.newLine();
        }
    }

    public static List<Appointment> loadAppointments() throws IOException {
        List<Appointment> list = new ArrayList<>();
        Path file = Path.of("appointments.txt");
        if (!Files.exists(file)) {
            Files.createFile(file);
            return list;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 7) {
                    list.add(new Appointment(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]));
                }
            }
        }
        return list;
    }

    public static void deleteById(String appointmentId) throws IOException {
        Path file = Path.of("appointments.txt");
        if (!Files.exists(file)) {
            return;
        }
        List<Appointment> appointments = loadAppointments();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.toFile()))) {
            for (Appointment a : appointments) {
                if (!a.getAppointmentId().equals(appointmentId)) {
                    bw.write(a.toCsv());
                    bw.newLine();
                }
            }
        }
    }

    public static void saveAppointments(List<Appointment> list) throws IOException {
        Path file = Path.of("appointments.txt");
        if (!Files.exists(file)) {
            Files.createFile(file);
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.toFile()))) {
            for (Appointment a : list) {
                bw.write(a.toCsv());
                bw.newLine();
            }
        }
    }

    public static void updateStatus(String appointmentId, String newStatus) throws IOException {
        List<Appointment> appointments = loadAppointments();
        boolean updated = false;
        for (int i = 0; i < appointments.size(); i++) {
            Appointment a = appointments.get(i);
            if (a.getAppointmentId().equals(appointmentId)) {
                appointments.set(i, new Appointment(a.getAppointmentId(), a.getCustomerId(), a.getDoctorId(),
                        a.getDate(), a.getTime(), a.getStaffId(), newStatus));
                updated = true;
                break;
            }
        }
        if (updated) {
            saveAppointments(appointments);
        }
    }

    public String getAppointmentId() { return appointmentId; }
    public String getCustomerId() { return customerId; }
    public String getDoctorId() { return doctorId; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getStaffId() { return staffId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
}
