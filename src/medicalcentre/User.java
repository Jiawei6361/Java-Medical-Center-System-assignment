package medicalcentre;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public abstract class User {
    private String id;
    private String name;
    private String password;
    private String gender;
    private String email;
    private String phone;
    private int age;

    public User(String id, String name, String password, String gender, String email, String phone, int age) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.age = age;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public String getGender() { return gender; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public int getAge() { return age; }

    public void setName(String name) { this.name = name; }
    public void setPassword(String password) { this.password = password; }
    public void setGender(String gender) { this.gender = gender; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAge(int age) { this.age = age; }

    public abstract String getRole();

    public String toCsv() {
        return String.join(",", id, getRole(), name, password, gender, email, phone, String.valueOf(age));
    }

    public void saveToFile(String filename) throws IOException {
        Path path = Paths.get(filename);
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        String line = toCsv() + "\n"; // Use toCsv() for consistency and ensure newline
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
            if (Files.size(path) > 0) {
                String lastLine = Files.readString(path).trim();
                if (!lastLine.endsWith("\n")) {
                    writer.newLine(); // Add a newline if the last line lacks one
                }
            }
            writer.write(line);
        }
    }

    public static List<User> loadFromFile(String filename) throws IOException {
        List<User> users = new ArrayList<>();
        Path file = Path.of(filename);
        if (!Files.exists(file)) {
            Files.createFile(file);
            return users;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length != 8) {
                    System.err.println("Skipping invalid line in users.txt: " + line);
                    continue;
                }
                try {
                    String id = parts[0];
                    String role = parts[1];
                    String name = parts[2];
                    String password = parts[3];
                    String gender = parts[4];
                    String email = parts[5];
                    String phone = parts[6];
                    int age = Integer.parseInt(parts[7]);
                    switch (role) {
                        case "Manager":
                            users.add(new Manager(id, name, password, gender, email, phone, age));
                            break;
                        case "Doctor":
                            users.add(new Doctor(id, name, password, gender, email, phone, age));
                            break;
                        case "Staff":
                            users.add(new Staff(id, name, password, gender, email, phone, age));
                            break;
                        case "Customer":
                            users.add(new Customer(id, name, password, gender, email, phone, age));
                            break;
                        default:
                            System.err.println("Unknown role in users.txt: " + role);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid age format in users.txt: " + line);
                } catch (Exception e) {
                    System.err.println("Error parsing user line: " + line + ", Error: " + e.getMessage());
                }
            }
        }
        return users;
    }

    public static void updateInFile(String filename, User updatedUser) throws IOException {
        List<String> lines = new ArrayList<>();
        Path file = Path.of(filename);
        if (!Files.exists(file)) {
            Files.createFile(file);
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length > 0 && parts[0].equals(updatedUser.getId())) {
                    line = updatedUser.toCsv();
                }
                lines.add(line);
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile()))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    public static void deleteFromFile(String filename, String id) throws IOException {
        List<String> lines = new ArrayList<>();
        Path file = Path.of(filename);
        if (!Files.exists(file)) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length > 0 && !parts[0].equals(id)) {
                    lines.add(line);
                }
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile()))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    public static String generateNewId(String role, String filename) throws IOException {
        String prefix = switch (role) {
            case "Manager" -> "M";
            case "Staff" -> "S";
            case "Doctor" -> "D";
            case "Customer" -> "C";
            default -> "U";
        };
        List<Integer> existingIds = new ArrayList<>();
        Path file = Path.of(filename);
        if (Files.exists(file)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",", -1);
                    if (parts.length > 0 && parts[0].startsWith(prefix)) {
                        try {
                            existingIds.add(Integer.parseInt(parts[0].substring(1)));
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
        }
        int nextIdNumber = existingIds.isEmpty() ? 1 : Collections.max(existingIds) + 1;
        return String.format("%s%03d", prefix, nextIdNumber);
    }
}
