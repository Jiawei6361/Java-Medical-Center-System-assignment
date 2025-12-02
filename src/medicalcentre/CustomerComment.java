package medicalcentre;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerComment {
    private String commentId;
    private String customerId;
    private String targetId; // Staff or Doctor ID
    private String content;

    public CustomerComment(String commentId, String customerId, String targetId, String content) {
        this.commentId = commentId;
        this.customerId = customerId;
        this.targetId = targetId;
        this.content = content;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getTargetId() {
        return targetId;
    }

    public String getContent() {
        return content;
    }

    public void saveToFile() throws IOException {
        Path path = Paths.get("customercomments.txt");
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        String line = String.format("%s,%s,%s,%s%n", commentId, customerId, targetId, content);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
            // Check if the file is not empty and the last line doesn't end with a newline
            if (Files.size(path) > 0) {
                String lastLine = Files.readString(path).trim();
                if (!lastLine.endsWith("\n")) {
                    writer.newLine(); // Add a newline if the last line lacks one
                }
            }
            writer.write(line);
        }
    }

    public static List<CustomerComment> loadComments() throws IOException {
        Path path = Paths.get("customercomments.txt");
        if (!Files.exists(path)) {
            return List.of();
        }
        return Files.lines(path)
                .map(line -> {
                    String[] parts = line.split(",", 4);
                    if (parts.length == 4) {
                        return new CustomerComment(parts[0], parts[1], parts[2], parts[3]);
                    }
                    return null;
                })
                .filter(c -> c != null)
                .collect(Collectors.toList());
    }

    public static String generateNewCommentId() throws IOException {
        List<CustomerComment> comments = loadComments();
        int maxId = comments.stream()
                .map(c -> Integer.parseInt(c.getCommentId().substring(2)))
                .max(Integer::compare)
                .orElse(0);
        return String.format("CM%03d", maxId + 1);
    }
}