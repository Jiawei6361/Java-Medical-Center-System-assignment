package medicalcentre;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    public LoginFrame() {
        setTitle("APU Medical Centre Login");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(102, 204, 204));
        setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setPreferredSize(new Dimension(300, 70));
        JLabel titleLabel = new JLabel("APU Medical Centre");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titlePanel.add(titleLabel);

        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        northPanel.setBackground(new Color(102, 204, 204));
        northPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        northPanel.add(titlePanel);
        add(northPanel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(102, 204, 204));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(createLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = createTextField();
        inputPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(createLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(200, 30));
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        inputPanel.add(passwordField, gbc);

        add(inputPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        southPanel.setBackground(new Color(102, 204, 204));
        JButton loginButton = createButton("Login", new Color(51, 153, 153), Color.WHITE);
        loginButton.addActionListener(new LoginListener());
        southPanel.add(loginButton);

        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.RED);
        messageLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        southPanel.add(messageLabel);

        add(southPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setPreferredSize(new Dimension(200, 30));
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        return field;
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(100, 35));
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        return button;
    }

    private class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Username and password are required.");
                return;
            }
            try {
                Path file = Path.of("users.txt");
                if (!Files.exists(file)) {
                    Files.createFile(file);
                }
                User user = authenticateUser(username, password);
                if (user != null) {
                    messageLabel.setText("Login successful!");
                    dispose();
                    redirectBasedOnRole(user);
                } else {
                    messageLabel.setText("Invalid username or password.");
                }
            } catch (IOException ex) {
                System.err.println("Error during login: " + ex.getMessage());
                messageLabel.setText("Failed to access user data. Please try again.");
            }
        }

        private User authenticateUser(String username, String password) throws IOException {
            List<User> users = User.loadFromFile("users.txt");
            return users.stream()
                    .filter(u -> u.getName().equals(username) && u.getPassword().equals(password))
                    .findFirst().orElse(null);
        }

        private void redirectBasedOnRole(User user) {
            switch (user.getRole()) {
                case "Manager": new ManageDashboard(); break;
                case "Doctor": new DoctorDashboard(user); break;
                case "Staff": new StaffUI(user); break;
                case "Customer": new CustomerDashboard(user); break;
                default: JOptionPane.showMessageDialog(null, "Customer page not implemented yet.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
