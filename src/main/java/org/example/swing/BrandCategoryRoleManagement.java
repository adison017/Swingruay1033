package org.example.swing;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import org.json.JSONArray;
import org.json.JSONObject;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BrandCategoryRoleManagement extends JPanel {
    private JTable categoryTable;
    private JTable brandTable;
    private JTable roleTable;

    public BrandCategoryRoleManagement() {
        setLayout(new GridLayout(1, 3));

        // Create models for the tables
        DefaultTableModel categoryModel = new DefaultTableModel(new Object[]{"cat_id", "cat_name"}, 0);
        DefaultTableModel brandModel = new DefaultTableModel(new Object[]{"brand_id", "brand_name"}, 0);
        DefaultTableModel roleModel = new DefaultTableModel(new Object[]{"role_id", "role_name"}, 0);

        categoryTable = new JTable(categoryModel);
        brandTable = new JTable(brandModel);
        roleTable = new JTable(roleModel);

        // Fetch data from APIs
        fetchData("http://localhost:8080/api/category", categoryModel, "cat_id", "cat_name");
        fetchData("http://localhost:8080/api/Brand", brandModel, "brand_id", "brand_name");
        fetchData("http://localhost:8080/api/Role", roleModel, "role_id", "role_name");

        // Create panels for each column
        JPanel categoryPanel = createColumnPanel("Category", categoryTable, categoryModel, "http://localhost:8080/api/category", new String[]{"cat_id", "cat_name"});
        JPanel brandPanel = createColumnPanel("Brand", brandTable, brandModel, "http://localhost:8080/api/Brand", new String[]{"brand_id", "brand_name"});
        JPanel rolePanel = createColumnPanel("Role", roleTable, roleModel, "http://localhost:8080/api/Role", new String[]{"role_id", "role_name"});

        // Add panels to the main panel
        add(categoryPanel);
        add(brandPanel);
        add(rolePanel);
    }

    private void fetchData(String apiUrl, DefaultTableModel model, String idKey, String nameKey) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Process JSON and populate model
            JSONArray jsonArray = new JSONArray(response.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject.has(idKey) && jsonObject.has(nameKey)) {
                    model.addRow(new Object[]{jsonObject.get(idKey), jsonObject.get(nameKey)});
                } else {
                    JOptionPane.showMessageDialog(this, "Missing keys in JSON data", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private JPanel createColumnPanel(String title, JTable table, DefaultTableModel model, String apiUrl, String[] jsonKeys) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        // Create text fields
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();

        // Add ListSelectionListener to the table
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    idField.setText(model.getValueAt(selectedRow, 0).toString());
                    nameField.setText(model.getValueAt(selectedRow, 1).toString());
                }
            }
        });

        // Create buttons with color
        JButton addButton = createStyledButton("Add", new Color(0, 128, 0), Color.WHITE); // Green
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    JSONObject json = new JSONObject();
                    json.put(jsonKeys[0], idField.getText());
                    json.put(jsonKeys[1], nameField.getText());

                    // Send POST request to add data
                    sendHttpRequest(apiUrl, "POST", json.toString());
                    model.addRow(new Object[]{idField.getText(), nameField.getText()});
                    JOptionPane.showMessageDialog(null, "Added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error adding data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton editButton = createStyledButton("Edit", new Color(255, 204, 0), Color.WHITE); // Yellow
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        JSONObject json = new JSONObject();
                        json.put(jsonKeys[0], idField.getText());
                        json.put(jsonKeys[1], nameField.getText());

                        // Send PUT request to update data
                        sendHttpRequest(apiUrl, "PUT", json.toString());

                        model.setValueAt(idField.getText(), selectedRow, 0);
                        model.setValueAt(nameField.getText(), selectedRow, 1);
                        JOptionPane.showMessageDialog(null, "Edited successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error editing data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton deleteButton = createStyledButton("Delete", new Color(255, 0, 0), Color.WHITE); // Red
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        String id = model.getValueAt(selectedRow, 0).toString();

                        // Send DELETE request to delete data
                        sendHttpRequest(apiUrl + "/" + id, "DELETE", null);

                        model.removeRow(selectedRow);
                        JOptionPane.showMessageDialog(null, "Deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error deleting data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Create a clear button
        JButton clearButton = createStyledButton("Clear", new Color(0, 0, 255), Color.WHITE); // Blue
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                idField.setText("");
                nameField.setText("");
            }
        });

        // Create a panel for text fields and buttons
        JPanel formPanel = new JPanel(new GridLayout(8, 1, 10, 10)); // Add space between components
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        formPanel.add(new JLabel("ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(addButton);
        formPanel.add(editButton);
        formPanel.add(deleteButton);
        formPanel.add(clearButton);

        // Add components to the column panel
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void sendHttpRequest(String apiUrl, String method, String jsonData) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");

        if (jsonData != null) {
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
    }

    private JButton createStyledButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Brand, Category, and Role Management");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new BrandCategoryRoleManagement());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}