// นำเข้าแพ็กเกจที่จำเป็น
package org.example.swing;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.example.swing.OrderStatusHandler;

// คลาส OrderManagement ขยายจาก JPanel
public class OrderManagement extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField orderNumberField, usernameField, statusField;
    private List<Item> selectedItems;

    public OrderManagement() {
        selectedItems = new ArrayList<>();
        setLayout(new BorderLayout(10, 10));

        OrderStatusHandler orderStatusHandler = new OrderStatusHandler(table, tableModel);

        // ปรับปรุงแบบฟอร์มให้ดูดี
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // กำหนดค่าเริ่มต้นของตารางและแบบจำลองของตาราง
        tableModel = new DefaultTableModel(new Object[]{"ID", "Order Number", "Username", "Status", "Total Amount"}, 0);
        table = new JTable(tableModel);
        loadData();

        // ตั้งค่าสีและฟอนต์
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(Color.LIGHT_GRAY);

        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // ปรับปรุงแบบฟอร์มสำหรับกรอกข้อมูล
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        formPanel.add(new JLabel("Order Number:"));
        orderNumberField = createStyledTextField();
        formPanel.add(orderNumberField);
        formPanel.add(new JLabel("Username:"));
        usernameField = createStyledTextField();
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Status:"));
        statusField = createStyledTextField();
        formPanel.add(statusField);

        // สร้างปุ่มสำหรับการจัดการออร์เดอร์
        JButton updateButton = new JButton("Update Order");
        JButton deleteButton = new JButton("Delete Order");
        JButton reset = new JButton("Clear Fields");
        JButton detailButton = new JButton("Detail");
        JButton inTransitButton = new JButton("Product In Transit");
        JButton deliveredButton = new JButton("Product Delivered");

        // กำหนดสีและฟอนต์ของปุ่ม
        updateButton.setFont(new Font("Arial", Font.BOLD, 14));
        deleteButton.setFont(new Font("Arial", Font.BOLD, 14));
        reset.setFont(new Font("Arial", Font.BOLD, 14));
        detailButton.setFont(new Font("Arial", Font.BOLD, 14));
        inTransitButton.setFont(new Font("Arial", Font.BOLD, 14));
        deliveredButton.setFont(new Font("Arial", Font.BOLD, 14));


        reset.setBackground(new Color(0, 0, 255));
        updateButton.setBackground(new Color(255, 204, 0));
        deleteButton.setBackground(new Color(255, 0, 0));
        detailButton.setBackground(new Color(151, 74, 228));
        inTransitButton.setBackground(new Color(243, 101, 15));
        deliveredButton.setBackground(new Color(0, 255, 0));

        reset.setForeground(Color.WHITE);
        updateButton.setForeground(Color.WHITE);
        deleteButton.setForeground(Color.WHITE);
        detailButton.setForeground(Color.WHITE);
        inTransitButton.setForeground(Color.WHITE);
        deliveredButton.setForeground(Color.WHITE);

        // จัดวางปุ่มในแผงปุ่ม
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(reset);
        buttonPanel.add(detailButton);
        buttonPanel.add(inTransitButton);
        buttonPanel.add(deliveredButton);

        // จัดเรียงองค์ประกอบทั้งหมด
        add(mainPanel, BorderLayout.CENTER);
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        // กำหนดการทำงานของปุ่ม
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int id = (int) tableModel.getValueAt(selectedRow, 0);

                    // แก้ตรงนี้: ตรวจสอบ null ก่อนเรียกใช้ toString()
                    String orderNumber = tableModel.getValueAt(selectedRow, 1) != null ? tableModel.getValueAt(selectedRow, 1).toString() : "";
                    String username = tableModel.getValueAt(selectedRow, 2) != null ? tableModel.getValueAt(selectedRow, 2).toString() : "";
                    String status = tableModel.getValueAt(selectedRow, 3) != null ? tableModel.getValueAt(selectedRow, 3).toString() : "";

                    orderNumberField.setText(orderNumber);
                    usernameField.setText(username);
                    statusField.setText(status);
                }
            }
        });

        reset.addActionListener(e -> clearFields());
        updateButton.addActionListener(e -> updateOrder());
        deleteButton.addActionListener(e -> deleteOrder());
        inTransitButton.addActionListener(e -> orderStatusHandler.ShowOrderInTransit());
        deliveredButton.addActionListener(e -> orderStatusHandler.ShowOrderDelivered());
        detailButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String orderNumber = tableModel.getValueAt(selectedRow, 1).toString();
                showOrderItemsInNewWindow(orderNumber);
            } else {
                JOptionPane.showMessageDialog(this, "Please select an order to view details.");
            }
        });
    }



    // สร้างเมธอดเพื่อสร้างฟิลด์ที่ถูกปรับแต่งเฉพาะ
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
        textField.setBackground(Color.WHITE);
        textField.setForeground(Color.BLACK);
        textField.setMargin(new Insets(5, 5, 5, 5)); // Padding ภายในฟิลด์
        return textField;
    }
    // เมธอดเพื่อแสดงรายการออร์เดอร์ในหน้าต่างใหม่
    // เมธอดเพื่อแสดงรายการออร์เดอร์ในหน้าต่างใหม่
    private void showOrderItemsInNewWindow(String orderNumber) {
        try {
            URL url = new URL("http://localhost:8080/api/order_items/orderNumber?orderNumber=" + orderNumber);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder jsonResponse = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonResponse.append(line);
            }
            reader.close();

            Gson gson = new Gson();
            Type itemListType = new TypeToken<List<Item>>() {}.getType();
            List<Item> items = gson.fromJson(jsonResponse.toString(), itemListType);

            JDialog dialog = new JDialog((Frame) null, "Order Details", true);
            dialog.setSize(600, 400);
            dialog.setLayout(new BorderLayout(10, 10));
            dialog.setLocationRelativeTo(null);

            String[] columnNames = {"ID", "Order Number", "Product Name", "Quantity", "Price"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
            JTable itemsTable = new JTable(tableModel);

            // ปรับปรุงตารางให้ดูทันสมัย
            itemsTable.setFont(new Font("Arial", Font.PLAIN, 14));
            itemsTable.setRowHeight(25);
            itemsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
            itemsTable.getTableHeader().setBackground(Color.LIGHT_GRAY);

            JScrollPane scrollPane = new JScrollPane(itemsTable);
            dialog.add(scrollPane, BorderLayout.CENTER);

            double totalPrice = 0;
            for (Item item : items) {
                double price = item.getPrice();
                int quantity = item.getQuantity();
                double itemTotalPrice = price * quantity;
                tableModel.addRow(new Object[]{
                        item.getId(), item.getOrdernumber(), item.getProduct_Name(),
                        item.getQuantity(), String.format("%.2f", itemTotalPrice)
                });
                totalPrice += itemTotalPrice;
            }

            // ปรับปรุงป้ายราคารวมให้ดูทันสมัย
            JLabel totalLabel = new JLabel("Total: " + String.format("%.2f", totalPrice));
            totalLabel.setHorizontalAlignment(SwingConstants.LEFT);
            totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
            totalLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JButton confirmButton = new JButton("Confirm Order");

            // ปรับปรุงปุ่มยืนยันการสั่งซื้อให้สวยงาม
            confirmButton.setFont(new Font("Arial", Font.BOLD, 14));
            confirmButton.setBackground(new Color(44, 209, 31));
            confirmButton.setForeground(Color.WHITE);
            confirmButton.setBorder(new LineBorder(new Color(0, 100, 0), 2, true));
            confirmButton.setMargin(new Insets(10, 10, 10, 10));

            // เพิ่มเอฟเฟคให้กับปุ่มเมื่อเมาส์ชี้
            confirmButton.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    confirmButton.setBackground(new Color(34, 139, 34));
                    confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    confirmButton.setBackground(new Color(144, 238, 144));
                }
            });

            // จัดวางส่วนประกอบหลัก
            JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
            bottomPanel.add(totalLabel, BorderLayout.WEST);
            bottomPanel.add(confirmButton, BorderLayout.EAST);
            dialog.add(bottomPanel, BorderLayout.SOUTH);

            // กำหนดการทำงานของปุ่ม
            confirmButton.addActionListener(e -> {
                String apiUrl = "http://localhost:8080/api/orders/updateStatus";

                try {
                    URL urlUpdate = new URL(apiUrl);
                    HttpURLConnection connectionUpdate = (HttpURLConnection) urlUpdate.openConnection();
                    connectionUpdate.setRequestMethod("POST");
                    connectionUpdate.setRequestProperty("Content-Type", "application/json; utf-8");
                    connectionUpdate.setDoOutput(true);

                    // JSON object to update the status
                    String jsonInputString = String.format("{\"orderNumber\": \"%s\", \"status\": \"In Transit\"}", orderNumber);

                    try (OutputStream os = connectionUpdate.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    int responseCode = connectionUpdate.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        JOptionPane.showMessageDialog(dialog, "Order Confirmed!");
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Failed to update order status");
                    }
                    dialog.dispose();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            dialog.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    // เมธอดเพื่อโหลดข้อมูลจาก API
    private void loadData() {
        try {
            URL url = new URL("http://localhost:8080/api/orders/status?status=Pending");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder jsonResponse = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonResponse.append(line);
            }
            reader.close();

            // แปลงข้อมูล JSON
            Gson gson = new Gson();
            Type orderListType = new TypeToken<List<Order>>() {}.getType();
            List<Order> orders = gson.fromJson(jsonResponse.toString(), orderListType);

            // เคลียร์แถวเก่าในตาราง
            tableModel.setRowCount(0);

            // เพิ่มแถวใหม่ในตารางจากออร์เดอร์
            for (Order order : orders) {
                tableModel.addRow(new Object[]{order.getId(), order.getOrderNumber(), order.getUsername(), order.getStatus(), order.getTotalAmount()});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // เมธอดสำหรับอัปเดตข้อมูลออร์เดอร์
    private void updateOrder() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to update.");
            return;
        }

        try {
            // ดึงข้อมูลจากฟิลด์
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String orderNumber = orderNumberField.getText();
            String username = usernameField.getText();
            String status = statusField.getText();

            // ส่งคำขอ HTTP PUT เพื่ออัปเดตออร์เดอร์
            URL url = new URL("http://localhost:8080/api/orders/" + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            // สร้าง JSON จากข้อมูลที่กรอก
            String jsonInputString = "{ \"orderNumber\": \"" + orderNumber + "\", \"username\": \"" + username + "\", \"status\": \"" + status + "\"}";

            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = connection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                // อัปเดตแถวที่เลือกในตาราง
                tableModel.setValueAt(orderNumber, selectedRow, 1);
                tableModel.setValueAt(username, selectedRow, 2);
                tableModel.setValueAt(status, selectedRow, 3);

                JOptionPane.showMessageDialog(this, "Order updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update order. Server responded with code: " + code);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // เมธอดสำหรับลบออร์เดอร์
    private void deleteOrder() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            try {
                URL url = new URL("http://localhost:8080/api/orders/" + id);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE");
                if (connection.getResponseCode() == 200) {
                    JOptionPane.showMessageDialog(this, "Order deleted successfully.");
                    tableModel.removeRow(selectedRow); // ลบแถวออกจาก JTable
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete order.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // เมธอดสำหรับเคลียร์ฟิลด์ต่างๆ
    private void clearFields() {
        orderNumberField.setText("");
        usernameField.setText("");
        statusField.setText("");
    }
}