package medicalcentre;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CustomerDashboard {
    private JFrame frame;

    public CustomerDashboard(User customer) {
        frame = new JFrame("Customer Dashboard - " + customer.getName());
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(5, 1));

        JButton viewAppointmentsButton = createButton("Check Appointments");
        JButton viewHistoryButton = createButton("View History");
        JButton provideCommentsButton = createButton("Provide Comments");
        JButton editProfileButton = createButton("Edit Profile");
        JButton exitButton = createButton("Exit");

        viewAppointmentsButton.addActionListener(e -> new CustomerViewAppointmentsFrame(customer));

        viewHistoryButton.addActionListener(e -> new CustomerViewHistoryFrame(customer));

        provideCommentsButton.addActionListener(e -> {
            try {
                String targetId = JOptionPane.showInputDialog(frame, "Enter Staff or Doctor ID to comment on:");
                if (targetId == null || targetId.trim().isEmpty()) return;
                String content = JOptionPane.showInputDialog(frame, "Enter your comment:");
                if (content == null || content.trim().isEmpty()) return;
                Path commentFile = Path.of("customercomments.txt");
                if (!Files.exists(commentFile)) {
                    Files.createFile(commentFile);
                }
                CustomerComment comment = new CustomerComment(CustomerComment.generateNewCommentId(), customer.getId(), targetId, content);
                comment.saveToFile();
                JOptionPane.showMessageDialog(frame, "Comment submitted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                System.err.println("Error submitting comment: " + ex.getMessage());
                JOptionPane.showMessageDialog(frame, "Failed to submit comment. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        editProfileButton.addActionListener(e -> new CustomerEditProfileFrame(customer));

        exitButton.addActionListener(e -> System.exit(0));

        frame.add(viewAppointmentsButton);
        frame.add(viewHistoryButton);
        frame.add(provideCommentsButton);
        frame.add(editProfileButton);
        frame.add(exitButton);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(new Color(51, 153, 153));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }
}
