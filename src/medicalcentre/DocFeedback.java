package medicalcentre;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DocFeedback {
    private String feedbackId; // F001…
    private String appointmentId; // A001…
    private String doctorId; // D001…
    private String customerId; // C001…
    private String content; // Feedback text (e.g., "Sleep more")

    public DocFeedback(String feedbackId, String appointmentId, String doctorId, String customerId, String content) {
        this.feedbackId = feedbackId;
        this.appointmentId = appointmentId;
        this.doctorId = doctorId;
        this.customerId = customerId;
        this.content = content;
    }

    public String getFeedbackId() { return feedbackId; }
    public String getAppointmentId() { return appointmentId; }
    public String getDoctorId() { return doctorId; }
    public String getCustomerId() { return customerId; }
    public String getContent() { return content; }

    public String toCsv() {
        return String.join(",", feedbackId, appointmentId, doctorId, customerId, content);
    }

    public static DocFeedback fromCsv(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 5) {
            System.err.println("Invalid feedback format in docfeedback.txt: " + line);
            return null;
        }
        return new DocFeedback(parts[0], parts[1], parts[2], parts[3], parts[4]);
    }

    public void saveToFile() throws IOException {
        Path file = Path.of("docfeedback.txt");
        if (!Files.exists(file)) {
            Files.createFile(file);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile(), true))) {
            writer.write(toCsv());
            writer.newLine();
        }
    }

    public static List<DocFeedback> loadFeedbacks(String filename) throws IOException {
        List<DocFeedback> list = new ArrayList<>();
        Path file = Path.of(filename);
        if (!Files.exists(file)) {
            Files.createFile(file);
            return list;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                DocFeedback feedback = fromCsv(line);
                if (feedback != null) {
                    list.add(feedback);
                }
            }
        }
        return list;
    }

    public static String generateNewFeedbackId() throws IOException {
        Path file = Path.of("docfeedback.txt");
        if (!Files.exists(file)) {
            Files.createFile(file);
            return "F001";
        }
        int max = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length > 0 && parts[0].startsWith("F")) {
                    try {
                        int n = Integer.parseInt(parts[0].substring(1));
                        max = Math.max(max, n);
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        return String.format("F%03d", max + 1);
    }
}
