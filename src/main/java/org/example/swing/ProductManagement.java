package org.example.swing;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class ProductManagement extends JPanel {
    private final JTextField productIdField;
    private final JTextField nameField;
    private final JTextField descriptionField;
    private final JTextField priceField;
    private final JTextField stockField;
    private final JComboBox<String> categoryComboBox;
    private final JComboBox<String> brandComboBox;
    private final DefaultTableModel tableModel;
    private final JTable productTable;

    private List<Map<String, Object>> categories;
    private List<Map<String, Object>> brands;

    public ProductManagement() {
        setLayout(new BorderLayout());

        // สร้างฟิลด์สำหรับป้อนข้อมูลสินค้า
        productIdField = new JTextField();
        nameField = new JTextField();
        descriptionField = new JTextField();
        priceField = new JTextField();
        stockField = new JTextField();
        categoryComboBox = new JComboBox<>();
        brandComboBox = new JComboBox<>();

        // สร้าง JPanel สำหรับฟิลด์และปุ่ม
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // เพิ่ม padding

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(new JLabel("Product ID:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(productIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(new JLabel("Product Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(descriptionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(new JLabel("Price:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(new JLabel("Stock Quantity:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(stockField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(new JLabel("Category:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(categoryComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(new JLabel("Brand:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(brandComboBox, gbc);

        // สร้างปุ่มสำหรับการกระทำต่างๆ
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // สร้าง panel ของปุ่มใหม่ด้วย FlowLayout
        JButton addButton = new JButton("Add Product");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProduct();
            }
        });
        buttonPanel.add(addButton);

        JButton editButton = new JButton("Edit Product");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editProduct();
            }
        });
        buttonPanel.add(editButton);

        JButton deleteButton = new JButton("Delete Product");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteProduct();
            }
        });
        buttonPanel.add(deleteButton);

        JButton clearButton = new JButton("Clear Fields");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });
        buttonPanel.add(clearButton);



        editButton.setFont(new Font("Arial", Font.BOLD, 14));
        deleteButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        clearButton.setFont(new Font("Arial", Font.BOLD, 14));


        addButton.setBackground(new Color(0, 128, 0));
        editButton.setBackground(new Color(255, 204, 55));
        deleteButton.setBackground(new Color(255, 0, 0));
        clearButton.setBackground(new Color(0, 0, 255));

        // เพิ่มสีให้กับปุ่มเพื่อความสวยงาม
        addButton.setForeground(Color.WHITE);
        editButton.setForeground(Color.WHITE);
        deleteButton.setForeground(Color.WHITE);
        clearButton.setForeground(Color.WHITE);


        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        inputPanel.add(buttonPanel, gbc); // เพิ่ม panel ของปุ่มเข้าใน input panel

        // สร้างตารางเพื่อแสดงสินค้าทั้งหมด
        String[] columnNames = {"Product ID", "Description", "Product Name", "Price", "Stock Quantity", "Category", "Brand"};
        tableModel = new DefaultTableModel(columnNames, 0);
        productTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);
        productTable.setFont(new Font("Arial", Font.PLAIN, 14));
        productTable.setRowHeight(25);
        productTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        productTable.getTableHeader().setBackground(Color.LIGHT_GRAY);

        // เพิ่ม inputPanel และ scrollPane ลงใน JPanel หลัก
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // เพิ่ม MouseListener ให้ตารางเพื่อเลือกแถว
        productTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow != -1) {
                    // เติมข้อมูล text fields และ dropdown ด้วยข้อมูลจากแถวที่เลือก
                    productIdField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    nameField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    descriptionField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    priceField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    stockField.setText(tableModel.getValueAt(selectedRow, 4).toString());
                    categoryComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 5).toString());
                    brandComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 6).toString());
                }
            }
        });

        // เรียก API เพื่อโหลดข้อมูลสินค้ามาแสดงในตาราง
        updateProductTable();
        // เรียก API เพื่อโหลดข้อมูล Categories และ Brands
        loadCategories();
        loadBrands();
    }

    private void clearFields() {
        productIdField.setText("");
        nameField.setText("");
        descriptionField.setText("");
        priceField.setText("");
        stockField.setText("");
        categoryComboBox.setSelectedIndex(0);
        brandComboBox.setSelectedIndex(0);
    }

    private void addProduct() {
        String name = nameField.getText();
        String description = descriptionField.getText();
        String price = priceField.getText();
        String stock = stockField.getText();
        String category = (String) categoryComboBox.getSelectedItem();
        String brand = (String) brandComboBox.getSelectedItem();

        String response = sendProductData("POST", null, name, description, price, stock, category, brand);
        if (response.equals("Success")) {
            JOptionPane.showMessageDialog(this, "Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add product: " + response, "Error", JOptionPane.ERROR_MESSAGE);
        }

        updateProductTable();
    }

    private void editProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            String productId = tableModel.getValueAt(selectedRow, 0).toString();
            String name = nameField.getText();
            String description = descriptionField.getText();
            String price = priceField.getText();
            String stock = stockField.getText();
            String category = (String) categoryComboBox.getSelectedItem();
            String brand = (String) brandComboBox.getSelectedItem();

            String responseMessage = sendProductData("PUT", productId, name, description, price, stock, category, brand);
            if (responseMessage.equals("Success")) {
                JOptionPane.showMessageDialog(this, "Product edited successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                updateProductTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to edit product: " + responseMessage, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a product to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            String productId = tableModel.getValueAt(selectedRow, 0).toString();

            String responseMessage = sendProductData("DELETE", productId, null, null, null, null, null, null);
            if (responseMessage.equals("Success")) {
                JOptionPane.showMessageDialog(this, "Product deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                updateProductTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete product: " + responseMessage, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private String sendProductData(String method, String productId, String name, String description, String price, String stock, String category, String brand) {
        String responseMessage = "Error";
        try {
            String urlString = "http://localhost:8080/api/Products" + (productId != null ? "/" + productId : "");
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            String jsonInputString = null;
            if (method.equals("POST") || method.equals("PUT")) {
                jsonInputString = String.format("{\"name\":\"%s\", \"description\":\"%s\", \"price\":%s, \"stock\":%s, \"category\":\"%s\", \"brand\":\"%s\"}",
                        name, description, price, stock, category, brand);
            }
            if (jsonInputString != null) {
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                responseMessage = "Success";
            } else {
                responseMessage = "Failed with response code: " + responseCode;
            }
        } catch (Exception e) {
            responseMessage = "Error: " + e.getMessage();
        }
        return responseMessage;
    }

    private List<Map<String, Object>> getProducts() {
        try {
            URL url = new URL("http://localhost:8080/api/Products");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            Gson gson = new Gson();
            Type productListType = new TypeToken<List<Map<String, Object>>>(){}.getType();
            return gson.fromJson(response.toString(), productListType);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            return null;
        }
    }

    private void updateProductTable() {
        try {
            // ล้างตารางก่อนโหลดข้อมูลใหม่
            tableModel.setRowCount(0);
            // เรียก API เพื่อนำข้อมูลสินค้า
            List<Map<String, Object>> products = getProducts();
            if (products != null) {
                for (Map<String, Object> product : products) {
                    // ใช้การตรวจสอบเงื่อนไขเพื่อหลีกเลี่ยง NullPointerException
                    int productId = product.get("id") != null ? (int) Math.round(Double.parseDouble(product.get("id").toString())) : 0;
                    String name = product.get("name") != null ? product.get("name").toString() : "";
                    String description = product.get("description") != null ? product.get("description").toString() : "";
                    String price = product.get("price") != null ? product.get("price").toString() : "";
                    String stock = product.get("stock") != null ? product.get("stock").toString() : "";
                    String category = product.get("category") != null ? product.get("category").toString() : "";
                    String brand = product.get("brand") != null ? product.get("brand").toString() : "";
                    tableModel.addRow(new Object[]{productId, description, name, price, stock, category, brand});
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    private void loadCategories() {
        categoryComboBox.removeAllItems();
        categoryComboBox.addItem("Select Category");
        categories = fetchDataFromApi("http://localhost:8080/api/category");
        if (categories != null) {
            categories.forEach(category -> {
                // ตรวจสอบ null ก่อนเรียก toString
                if (category.get("cat_name") != null) {
                    categoryComboBox.addItem(category.get("cat_name").toString());
                }
            });
        }
    }

    private void loadBrands() {
        brandComboBox.removeAllItems();
        brandComboBox.addItem("Select Brand");
        brands = fetchDataFromApi("http://localhost:8080/api/Brand");
        if (brands != null) {
            brands.forEach(brand -> {
                // ตรวจสอบ null ก่อนเรียก toString
                if (brand.get("brand_name") != null) {
                    brandComboBox.addItem(brand.get("brand_name").toString());
                }
            });
        }
    }

    private List<Map<String, Object>> fetchDataFromApi(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Map<String, Object>>>() {
            }.getType();
            return gson.fromJson(response.toString(), listType);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            return null;
        }
    }
}