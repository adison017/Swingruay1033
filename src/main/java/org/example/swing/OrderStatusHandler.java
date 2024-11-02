package org.example.swing;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class OrderStatusHandler {

    private JTable table;
    private DefaultTableModel tableModel;

    public OrderStatusHandler(JTable table, DefaultTableModel tableModel) {
        this.table = table;
        this.tableModel = tableModel;
    }

    public void ShowOrderInTransit() {
        String status = "In Transit";

        // สร้างหน้าต่างใหม่
        JFrame frame = new JFrame("Orders in " + status + " Status");
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        // สร้าง DefaultTableModel ใหม่สำหรับตารางใหม่
        DefaultTableModel newTableModel = new DefaultTableModel();
        JTable newTable = new JTable(newTableModel);

        // กำหนดคอลัมน์
        newTableModel.addColumn("Order Number");
        newTableModel.addColumn("Username");
        newTableModel.addColumn("Status");
        newTableModel.addColumn("Total Amount");

        // ดึงข้อมูลจาก API และเพิ่มลงในตาราง
        try {
            URL url = new URL("http://localhost:8080/api/orders/status?status=" + status.replace(" ", "%20"));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            // ใช้ Gson เพื่อแปลง JSON string เป็น List ของออเดอร์
            Gson gson = new Gson();
            Type orderListType = new TypeToken<List<Order>>() {}.getType();
            List<Order> orders = gson.fromJson(content.toString(), orderListType);

            // เพิ่มข้อมูลลงใน newTableModel
            for (Order order : orders) {
                newTableModel.addRow(new Object[]{order.getOrderNumber(), order.getUsername(), order.getStatus(), order.getTotalAmount()});
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // เพิ่ม JButton สำหรับแสดงรายละเอียด
        JButton detailButton = new JButton("Detail");
        detailButton.addActionListener(e -> {
            int selectedRow = newTable.getSelectedRow();
            if (selectedRow != -1) {
                String orderNumber = newTableModel.getValueAt(selectedRow, 0).toString();
                showOrderItemsInNewWindow(orderNumber);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select an order to view details.");
            }
        });

        // เพิ่ม JTable ลงใน JScrollPane
        JScrollPane scrollPane = new JScrollPane(newTable);

        // เพิ่ม JScrollPane ลงใน JFrame
        frame.add(scrollPane, BorderLayout.CENTER);

        // เพิ่มปุ่ม Detail ด้านล่าง
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(detailButton, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // แสดง JFrame
        frame.setVisible(true);
    }

    public void ShowOrderDelivered() {
        String status = "Delivered";

        // สร้างหน้าต่างใหม่
        JFrame frame = new JFrame("Orders in " + status + " Status");
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        // สร้าง DefaultTableModel ใหม่สำหรับตารางใหม่
        DefaultTableModel newTableModel = new DefaultTableModel();
        JTable newTable = new JTable(newTableModel);

        // กำหนดคอลัมน์
        newTableModel.addColumn("Order Number");
        newTableModel.addColumn("Username");
        newTableModel.addColumn("Status");
        newTableModel.addColumn("Total Amount");

        // ดึงข้อมูลจาก API และเพิ่มลงในตาราง
        try {
            URL url = new URL("http://localhost:8080/api/orders/status?status=" + status.replace(" ", "%20"));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            // ใช้ Gson เพื่อแปลง JSON string เป็น List ของออเดอร์
            Gson gson = new Gson();
            Type orderListType = new TypeToken<List<Order>>() {}.getType();
            List<Order> orders = gson.fromJson(content.toString(), orderListType);

            // เพิ่มข้อมูลลงใน newTableModel
            for (Order order : orders) {
                newTableModel.addRow(new Object[]{order.getOrderNumber(), order.getUsername(), order.getStatus(), order.getTotalAmount()});
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // เพิ่ม JButton สำหรับแสดงรายละเอียด
        JButton detailButton = new JButton("Detail");
        detailButton.addActionListener(e -> {
            int selectedRow = newTable.getSelectedRow();
            if (selectedRow != -1) {
                String orderNumber = newTableModel.getValueAt(selectedRow, 0).toString();
                showOrderItemsInNewWindow(orderNumber);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select an order to view details.");
            }
        });

        // เพิ่ม JTable ลงใน JScrollPane
        JScrollPane scrollPane = new JScrollPane(newTable);

        // เพิ่ม JScrollPane ลงใน JFrame
        frame.add(scrollPane, BorderLayout.CENTER);

        // เพิ่มปุ่ม Detail ด้านล่าง
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(detailButton, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // แสดง JFrame
        frame.setVisible(true);
    }

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

            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.add(totalLabel, BorderLayout.EAST);
            dialog.add(bottomPanel, BorderLayout.SOUTH);

            dialog.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}