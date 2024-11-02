package org.example.swing;

import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginFrame extends JFrame {
    private JPanel panel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;

    public LoginFrame() {
        setTitle("Login");
        setSize(300, 230);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(usernameLabel, gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(passwordField, gbc);

        loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        panel.add(loginButton, gbc);

        statusLabel = new JLabel();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(statusLabel, gbc);

        loginButton.addActionListener(this::authenticateUser);

        add(panel);
    }

    private void authenticateUser(ActionEvent event) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            URL url = new URL("http://localhost:8080/api/users/authenticate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject requestData = new JSONObject();
            requestData.put("username", username);
            requestData.put("password", password);

            OutputStream os = conn.getOutputStream();
            os.write(requestData.toString().getBytes());
            os.flush();
            os.close();

            InputStream is;
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = conn.getInputStream();
            } else {
                is = conn.getErrorStream();
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            JSONObject responseJson;
            try {
                responseJson = new JSONObject(response.toString());
            } catch (Exception e) {
                statusLabel.setText("Login failed: Invalid response from server.");
                return;
            }

            // ดึงข้อมูล role และ message จาก response JSON
            String role = responseJson.optString("role", "");
            String message = responseJson.optString("message", "");

            if ("Login successful!!".equals(message)) {
                if ("admin".equalsIgnoreCase(role)) {
                    JOptionPane.showMessageDialog(this, "Login successful!");
                    SwingUtilities.invokeLater(() -> new Main(username, role).createAndShowGUI());  // ส่ง username และ role ไปยัง Main
                    this.dispose();
                } else {
                    statusLabel.setText("Login failed: Only admins can login.");
                }
            } else {
                statusLabel.setText("Login failed: " + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Login failed: Server error.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}