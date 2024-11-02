package org.example.swing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class UserSwingApp extends JPanel {
    private JTextField idField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField passwordField;
    private JComboBox<String> roleField;  // เปลี่ยนเป็น JComboBox
    private JTable userTable;
    private DefaultTableModel tableModel;

    public UserSwingApp() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        idField = new JTextField(10);
        firstNameField = new JTextField(10);
        lastNameField = new JTextField(10);
        usernameField = new JTextField(10);
        passwordField = new JTextField(10);
        emailField = new JTextField(10);
        roleField = new JComboBox<>();  // ใช้ JComboBox

        formPanel.add(new JLabel("ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("First Name:"));
        formPanel.add(firstNameField);
        formPanel.add(new JLabel("Last Name:"));
        formPanel.add(lastNameField);
        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Role:"));
        formPanel.add(roleField);

        JButton addButton = new JButton("Add User");
        JButton deleteButton = new JButton("Delete User");
        JButton updateButton = new JButton("Update User");
        JButton clearButton = new JButton("Clear Fields");

        updateButton.setFont(new Font("Arial", Font.BOLD, 14));
        deleteButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        clearButton.setFont(new Font("Arial", Font.BOLD, 14));

        addButton.setBackground(new Color(0, 128, 0));
        updateButton.setBackground(new Color(255, 204, 0));
        deleteButton.setBackground(new Color(255, 0, 0));
        clearButton.setBackground(new Color(0, 0, 255));
        addButton.setForeground(Color.WHITE);
        updateButton.setForeground(Color.WHITE);
        deleteButton.setForeground(Color.WHITE);
        clearButton.setForeground(Color.WHITE);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4, 5, 5));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(clearButton);

        tableModel = new DefaultTableModel(new String[]{"ID", "First Name", "Last Name", "Username","Password", "Email", "Role"}, 0);
        userTable = new JTable(tableModel);

        userTable.setFont(new Font("Sarabun", Font.PLAIN, 14));
        userTable.setRowHeight(25);
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        userTable.getTableHeader().setBackground(Color.LIGHT_GRAY);

        JScrollPane scrollPane = new JScrollPane(userTable);

        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
        add(scrollPane, BorderLayout.CENTER);

        addButton.addActionListener(e -> addUser());
        deleteButton.addActionListener(e -> deleteUser());
        updateButton.addActionListener(e -> updateUser());
        clearButton.addActionListener(e -> clearFields());

        loadRoles();  // โหลดข้อมูลบทบาท
        loadDataUser();

        userTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int selectedRow = userTable.getSelectedRow();
                    displayUserData(selectedRow);
                }
            }
        });
    }

    private void displayUserData(int row) {
        idField.setText(tableModel.getValueAt(row, 0).toString());
        firstNameField.setText(tableModel.getValueAt(row, 1).toString());
        lastNameField.setText(tableModel.getValueAt(row, 2).toString());
        usernameField.setText(tableModel.getValueAt(row, 3).toString());
        passwordField.setText(tableModel.getValueAt(row, 4).toString());
        emailField.setText(tableModel.getValueAt(row, 5).toString());
        roleField.setSelectedItem(tableModel.getValueAt(row, 6).toString());
    }

    private void addUser() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        String role = roleField.getSelectedItem().toString();  // ใช้ getSelectedItem

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty() || role.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        String jsonInputString = "{\"firstName\":\"" + firstName + "\",\"lastName\":\"" + lastName + "\",\"username\":\"" + username  +"\",\"password\":\"" + password + "\",\"email\":\"" + email +"\",\"role\":\"" + role + "\"}";
        sendRequest("/api/users", "POST", jsonInputString);
        tableModel.addRow(new Object[]{null,firstName, lastName, username,password, email,role});
    }

    private void deleteUser() {
        int row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
            return;
        }

        String id = tableModel.getValueAt(row, 0).toString();
        sendRequest("/api/users/" + id, "DELETE", null);
        tableModel.removeRow(row);
    }

    private void updateUser() {
        int row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to update.");
            return;
        }

        String id = idField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        String role = roleField.getSelectedItem().toString();  // ใช้ getSelectedItem

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty()|| password.isEmpty() || email.isEmpty() || role.isEmpty() || id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        String jsonInputString = "{\"id\":\"" + id + "\",\"firstName\":\"" + firstName + "\",\"lastName\":\"" + lastName + "\",\"username\":\"" + username  + "\",\"password\":\"" + password+ "\",\"email\":\"" + email +"\",\"role\":\"" + role + "\"}";
        sendRequest("/api/users/" + id, "PUT", jsonInputString);

        tableModel.setValueAt(firstName, row, 1);
        tableModel.setValueAt(lastName, row, 2);
        tableModel.setValueAt(username, row, 3);
        tableModel.setValueAt(password, row, 4);
        tableModel.setValueAt(email, row, 5);
        tableModel.setValueAt(role, row, 6);
    }

    private void sendRequest(String endpoint, String method, String jsonInputString) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                boolean success = false;
                try {
                    URL url = new URL("http://localhost:8080" + endpoint);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod(method);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    if (jsonInputString != null) {
                        try (OutputStream os = conn.getOutputStream()) {
                            byte[] input = jsonInputString.getBytes("utf-8");
                            os.write(input, 0, input.length);
                        }
                    }

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }

                        in.close();
                        JOptionPane.showMessageDialog(UserSwingApp.this, response.toString());
                        success = true;
                    } else {
                        JOptionPane.showMessageDialog(UserSwingApp.this, "Error: " + responseCode);
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(UserSwingApp.this, "Exception: " + e.getMessage());
                } finally {
                    final boolean finalSuccess = success;
                    SwingUtilities.invokeLater(() -> {
                        if (method.equals("POST") && finalSuccess) {
                            JOptionPane.showMessageDialog(UserSwingApp.this, "User added successfully!");
                        } else if (method.equals("DELETE") && finalSuccess) {
                            JOptionPane.showMessageDialog(UserSwingApp.this, "User deleted successfully!");
                        } else if (method.equals("PUT") && finalSuccess) {
                            JOptionPane.showMessageDialog(UserSwingApp.this, "User updated successfully!");
                        }
                    });
                }
                return null;
            }
        };

        worker.execute();
    }

    private void clearFields() {
        idField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        emailField.setText("");
        roleField.setSelectedIndex(-1);  // เคลียร์การเลือก
    }

    private void loadDataUser() {
        try {
            URL url = new URL("http://localhost:8080/api/users");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
                JSONArray users = new JSONArray(response.toString());
                tableModel.setRowCount(0);

                for (int i = 0; i < users.length(); i++) {
                    JSONObject user = users.getJSONObject(i);

                    int id = user.getInt("id");
                    String firstName = user.optString("firstName", "");
                    String lastName = user.optString("lastName", "");
                    String username = user.optString("username", "");
                    String password = user.optString("password", "");
                    String email = user.optString("email", "");
                    String role = user.optString("role", "");

                    tableModel.addRow(new Object[]{id, firstName, lastName, username, password, email, role});
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + responseCode);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Exception: " + e.getMessage());
        }
    }

    private void loadRoles() {
        try {
            URL url = new URL("http://localhost:8080/api/Role");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
                JSONArray roles = new JSONArray(response.toString());
                roleField.removeAllItems();

                for (int i = 0; i < roles.length(); i++) {
                    JSONObject role = roles.getJSONObject(i);
                    roleField.addItem(role.getString("role_name"));
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error loading roles: " + responseCode);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Exception: " + e.getMessage());
        }
    }

    private void exitApplication() {
        System.exit(0);
    }
}