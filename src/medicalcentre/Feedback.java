package medicalcentre;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Feedback {
    private String id;
    private String userId;
    private String personid;
    private String content;

    public Feedback(String id, String userId, String personid, String content) {
        this.id = id;
        this.userId = userId;
        this.personid = personid;
        this.content = content;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getpersonid() {return personid;}
    public String getContent() { return content; }
    

    public String toCsv() {
        return String.join(",", id, userId, content, personid);
    }

    public static Feedback fromCsv(String line) {
        String[] parts = line.split(",", 4);
        if (parts.length >= 4) return new Feedback(parts[0], parts[1], parts[2], parts[3]);
        return null;
    }

    public static List<Feedback> loadFeedbacks(String filename) throws IOException {
        List<Feedback> list = new ArrayList<>();
        Path file = Path.of(filename);
        if (!Files.exists(file)) {
            Files.createFile(file);
            return list;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Feedback fb = fromCsv(line);
                if (fb != null) list.add(fb);
            }
        }
        return list;
    }

    public static void saveFeedbacks(String filename, List<Feedback> list) throws IOException {
        Path file = Path.of(filename);
        if (!Files.exists(file)) {
            Files.createFile(file);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile()))) {
            for (Feedback fb : list) {
                writer.write(fb.toCsv());
                writer.newLine();
            }
        }
    }
}
