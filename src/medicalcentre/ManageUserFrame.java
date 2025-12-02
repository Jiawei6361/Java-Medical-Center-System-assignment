package medicalcentre;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern; 
import java.util.stream.Collectors;

public class ManageUserFrame extends JFrame {
    private String role;
    private String filename = "users.txt";
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, passwordField, emailField, phoneField, ageField;
    private JComboBox<String> genderComboBox;
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public ManageUserFrame(String role) {
        this.role = role;
        setTitle("Manage " + role + "s");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(102, 204, 204));
        setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setPreferredSize(new Dimension(300, 60));
        JLabel titleLabel = new JLabel("Manage " + role);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titlePanel.add(titleLabel);

        JPanel northWrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        northWrapperPanel.setBackground(new Color(102, 204, 204));
        northWrapperPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        northWrapperPanel.add(titlePanel);
        add(northWrapperPanel, BorderLayout.NORTH);

        JPanel tableDisplayPanel = new JPanel(new BorderLayout());
        tableDisplayPanel.setBackground(Color.WHITE);
        tableDisplayPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        JLabel allUsersLabel = new JLabel("All " + role + "s");
        allUsersLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        tableDisplayPanel.add(allUsersLabel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "NAME", "Gender", "Email", "Phone number", "Age", "Role"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        userTable = new JTable(tableModel);
        userTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        userTable.setRowHeight(25);
        userTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        userTable.getTableHeader().setBackground(new Color(240, 240, 240));
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && userTable.getSelectedRow() != -1) {
                displaySelectedUserInFields();
            }
        });
        tableDisplayPanel.add(new JScrollPane(userTable), BorderLayout.CENTER);
        add(tableDisplayPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new GridBagLayout());
        southPanel.setBackground(new Color(102, 204, 204));
        southPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        southPanel.add(createInputLabel("Enter user name"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = createInputField();
        southPanel.add(nameField, gbc);
        gbc.weightx = 0;

        gbc.gridx = 0; gbc.gridy = 1;
        southPanel.add(createInputLabel("Enter user password"), gbc);
        gbc.gridx = 1;
        passwordField = createInputField();
        southPanel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        southPanel.add(createInputLabel("Choose user gender"), gbc);
        gbc.gridx = 1;
        String[] genders = {"Male", "Female", "Other"};
        genderComboBox = new JComboBox<>(genders);
        genderComboBox.setPreferredSize(new Dimension(200, 30));
        genderComboBox.setFont(new Font("SansSerif", Font.PLAIN, 16));
        southPanel.add(genderComboBox, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        gbc.gridheight = 3;
        southPanel.add(Box.createRigidArea(new Dimension(50, 0)), gbc);
        gbc.gridheight = 1;

        gbc.gridx = 3; gbc.gridy = 0;
        southPanel.add(createInputLabel("Email"), gbc);
        gbc.gridx = 4;
        emailField = createInputField();
        southPanel.add(emailField, gbc);

        gbc.gridx = 3; gbc.gridy = 1;
        southPanel.add(createInputLabel("Phone number"), gbc);
        gbc.gridx = 4;
        phoneField = createInputField();
        southPanel.add(phoneField, gbc);

        gbc.gridx = 3; gbc.gridy = 2;
        southPanel.add(createInputLabel("Age"), gbc);
        gbc.gridx = 4;
        ageField = createInputField();
        southPanel.add(ageField, gbc);

        gbc.gridx = 5; gbc.gridy = 0;
        gbc.gridheight = 3;
        southPanel.add(Box.createRigidArea(new Dimension(50, 0)), gbc);
        gbc.gridheight = 1;

        gbc.gridx = 6; gbc.gridy = 0;
        JButton createBtn = createStyledButton("Create");
        createBtn.addActionListener(e -> createUser());
        southPanel.add(createBtn, gbc);

        gbc.gridy = 1;
        JButton updateBtn = createStyledButton("Update");
        updateBtn.addActionListener(e -> updateUser());
        southPanel.add(updateBtn, gbc);

        gbc.gridy = 2;
        JButton deleteBtn = createStyledButton("Delete");
        deleteBtn.addActionListener(e -> deleteUser());
        southPanel.add(deleteBtn, gbc);

        add(southPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        readUsers();
        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(120, 35));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        return button;
    }

    private JLabel createInputLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        label.setForeground(Color.BLACK);
        return label;
    }

    private JTextField createInputField() {
        JTextField field = new JTextField(15);
        field.setPreferredSize(new Dimension(200, 30));
        field.setMinimumSize(new Dimension(200, 30));
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        return field;
    }

    private boolean isValidEmail(String email) {
        if (email == null) return false;
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    private void createUser() {
        String name = nameField.getText().trim();
        String password = passwordField.getText().trim();
        String gender = (String) genderComboBox.getSelectedItem();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String ageStr = ageField.getText().trim();

        if (name.isEmpty() || password.isEmpty() || gender == null || email.isEmpty() || phone.isEmpty() || ageStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required to create a user.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age <= 0 || age > 120) {
                JOptionPane.showMessageDialog(this, "Age must be a positive number up to 120.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String newId;
        try {
            newId = User.generateNewId(role, filename);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error generating new ID: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = null;
        switch (role) {
            case "Manager": user = new Manager(newId, name, password, gender, email, phone, age); break;
            case "Staff": user = new Staff(newId, name, password, gender, email, phone, age); break;
            case "Doctor": user = new Doctor(newId, name, password, gender, email, phone, age); break;
        }

        try {
            user.saveToFile(filename);
            JOptionPane.showMessageDialog(this, role + " created successfully with ID: " + newId, "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            readUsers();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error creating " + role + ": " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void readUsers() {
        tableModel.setRowCount(0);
        try {
            List<User> users = User.loadFromFile(filename).stream()
                    .filter(u -> u.getRole().equals(role))
                    .collect(Collectors.toList());
            for (User u : users) {
                tableModel.addRow(new Object[]{u.getId(), u.getName(), u.getGender(), u.getEmail(), u.getPhone(), u.getAge(), u.getRole()});
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading " + role + "s: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user from the table to update.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String idToUpdate = (String) tableModel.getValueAt(selectedRow, 0);
        String name = nameField.getText().trim();
        String password = passwordField.getText().trim();
        String gender = (String) genderComboBox.getSelectedItem();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String ageStr = ageField.getText().trim();

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age <= 0 || age > 120) {
                JOptionPane.showMessageDialog(this, "Age must be a positive number up to 120.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User existingUser = null;
        try {
            List<User> users = User.loadFromFile(filename);
            existingUser = users.stream().filter(u -> u.getId().equals(idToUpdate)).findFirst().orElse(null);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error loading user for update check: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (existingUser == null) {
            JOptionPane.showMessageDialog(this, "User with ID " + idToUpdate + " not found.", "User Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String newName = name.isEmpty() ? existingUser.getName() : name;
        String newPassword = password.isEmpty() ? existingUser.getPassword() : password;
        String newGender = gender == null ? existingUser.getGender() : gender;
        String newEmail = email.isEmpty() ? existingUser.getEmail() : email;
        String newPhone = phone.isEmpty() ? existingUser.getPhone() : phone;
        int newAge = ageStr.isEmpty() ? existingUser.getAge() : age;

        User updatedUser = null;
        switch (role) {
            case "Manager": updatedUser = new Manager(idToUpdate, newName, newPassword, newGender, newEmail, newPhone, newAge); break;
            case "Staff": updatedUser = new Staff(idToUpdate, newName, newPassword, newGender, newEmail, newPhone, newAge); break;
            case "Doctor": updatedUser = new Doctor(idToUpdate, newName, newPassword, newGender, newEmail, newPhone, newAge); break;
        }

        try {
            User.updateInFile(filename, updatedUser);
            JOptionPane.showMessageDialog(this, role + " updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            readUsers();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error updating " + role + ": " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user from the table to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idToDelete = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete user ID: " + idToDelete + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.NO_OPTION) return;

        try {
            User.deleteFromFile(filename, idToDelete);
            JOptionPane.showMessageDialog(this, role + " deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            readUsers();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting " + role + ": " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        nameField.setText("");
        passwordField.setText("");
        genderComboBox.setSelectedIndex(0);
        emailField.setText("");
        phoneField.setText("");
        ageField.setText("");
        userTable.clearSelection();
    }

    private void displaySelectedUserInFields() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow != -1) {
            nameField.setText((String) tableModel.getValueAt(selectedRow, 1));
            String genderValue = (String) tableModel.getValueAt(selectedRow, 2);
            for (int i = 0; i < genderComboBox.getItemCount(); i++) {
                if (genderComboBox.getItemAt(i).equals(genderValue)) {
                    genderComboBox.setSelectedIndex(i);
                    break;
                }
            }
            emailField.setText((String) tableModel.getValueAt(selectedRow, 3));
            phoneField.setText((String) tableModel.getValueAt(selectedRow, 4));
            ageField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 5)));
        }
    }
}
