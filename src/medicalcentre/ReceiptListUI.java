package medicalcentre;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class ReceiptListUI extends JFrame {
    private JTextArea area;
    private JComboBox<String> receiptSelector;
    private StaffService staffService;

    public ReceiptListUI() {
        staffService = new StaffService();
        setTitle("All Receipts");
        setSize(400, 300);
        setLocationRelativeTo(null);

        // Setup UI
        area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Monospaced for better readability of receipts

        receiptSelector = new JComboBox<>();
        receiptSelector.addItem("All Receipts");
        try {
            List<String> receiptIds = staffService.getReceiptIds();
            for (String id : receiptIds) {
                receiptSelector.addItem(id);
            }
        } catch (IOException e) {
            System.err.println("Error loading receipt IDs: " + e.getMessage());
            receiptSelector.addItem("Error loading IDs");
        }
        receiptSelector.addActionListener(e -> updateReceiptDisplay());

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Select Receipt:"));
        topPanel.add(receiptSelector);

        JScrollPane scroll = new JScrollPane(area);
        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        JButton close = createButton("Close", e -> dispose());
        add(close, BorderLayout.SOUTH);

        updateReceiptDisplay(); // Initial display
        setVisible(true);
    }

    private void updateReceiptDisplay() {
        String selectedId = (String) receiptSelector.getSelectedItem();
        if (selectedId == null) {
            area.setText("No selection available.");
            return;
        }
        try {
            String content;
            if ("All Receipts".equalsIgnoreCase(selectedId) || "Error loading IDs".equals(selectedId)) {
                content = staffService.getReceipts("all");
            } else {
                content = staffService.getReceipts(selectedId);
            }
            area.setText(content.isEmpty() ? "No receipts found." : content);
        } catch (IOException e) {
            System.err.println("Error loading receipt: " + e.getMessage());
            area.setText("Failed to load receipt. Please try again.");
        }
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
}