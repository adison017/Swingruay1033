package org.example.swing;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportManagement extends JPanel {
    private static final String API_URL = "http://localhost:8080/api/orders";

    public ReportManagement() {
        setLayout(new BorderLayout());
        // Fetch the sales report
        List<Order> salesReport = getSalesReport();

        // Process daily sales and order data
        Map<String, Double> dailySalesData = new HashMap<>();
        int totalOrdersToday = 0;
        double totalSalesToday = 0.0;
        String todayDate = getFormattedDate();

        for (Order order : salesReport) {
            String date = order.getDateTime() != null
                    ? order.getDateTime().split(" ")[0]
                    : "unknown"; // Handle case when dateTime is null

            double totalAmount = order.getTotalAmount();
            dailySalesData.put(date, dailySalesData.getOrDefault(date, 0.0) + totalAmount);

            if (todayDate.equals(date)) {
                totalOrdersToday++;
                totalSalesToday += order.getTotalAmount();
            }
        }

        // Create a card for the daily orders and today's sales
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(2, 1));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        JLabel ordersLabel = new JLabel("Daily Orders: " + totalOrdersToday);
        ordersLabel.setFont(new Font("Arial", Font.BOLD, 20));
        ordersLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ordersLabel.setBorder(BorderFactory.createTitledBorder("Daily Orders"));


        JLabel salesLabel = new JLabel("Today's Sales: " + String.format("%.2f Baht", totalSalesToday));
        salesLabel.setFont(new Font("Arial", Font.BOLD, 20));
        salesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        salesLabel.setBorder(BorderFactory.createTitledBorder("Today's Sales"));

        // เพิ่มสีพื้นหลังให้กับ labels
        ordersLabel.setBackground(new Color( 253, 253, 150)); // เพิ่มสีพื้นหลังให้ label Daily Orders
        ordersLabel.setOpaque(true); // ทำให้สีพื้นหลังแสดงผล
        salesLabel.setBackground(new Color(200, 255, 200)); // เพิ่มสีพื้นหลังให้ label Today's Sales
        salesLabel.setOpaque(true); // ทำให้สีพื้นหลังแสดงผล

        infoPanel.add(ordersLabel);
        infoPanel.add(salesLabel);

        // Create daily sales chart
        DefaultCategoryDataset dailyDataset = new DefaultCategoryDataset();
        for (String date : getLast7Days()) {
            dailyDataset.addValue(dailySalesData.getOrDefault(date, 0.0), "Sales Amount", date);
        }

        JFreeChart dailyChart = ChartFactory.createBarChart(
                "Daily Sales Report", "Date", "Sales Amount (Baht)",
                dailyDataset, PlotOrientation.VERTICAL,
                false, true, false);

        // Customize chart to display sales labels
        CategoryPlot plot = dailyChart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator() {
            @Override
            public String generateLabel(CategoryDataset dataset, int row, int column) {
                Number value = dataset.getValue(row, column);
                return String.format("%.2f Baht", value.doubleValue());
            }
        });
        renderer.setDefaultItemLabelsVisible(true);

        // Change the color of the bars
        renderer.setSeriesPaint(0, Color.BLUE); // เปลี่ยนสีแท่งกราฟที่ 0 เป็นสีน้ำเงิน
        // หากคุณมีหลายชุดข้อมูลให้เพิ่มสีเพิ่มเติม
        // renderer.setSeriesPaint(1, Color.GREEN); // เปลี่ยนสีแท่งกราฟที่ 1 เป็นสีเขียว

        // Add chart to JPanel
        ChartPanel chartPanel = new ChartPanel(dailyChart);
        chartPanel.setPreferredSize(new Dimension(800, 400)); // Set preferred size for the chart panel

        // Add components to the main panel
        add(infoPanel, BorderLayout.NORTH);
        add(chartPanel, BorderLayout.CENTER);
    }

    // Method to fetch data from API
    private List<Order> getSalesReport() {
        List<Order> orders = new ArrayList<>();
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            JSONArray jsonArray = new JSONArray(response.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Extract data from JSON
                int id = jsonObject.getInt("id");
                String orderNumber = jsonObject.getString("orderNumber");
                String username = jsonObject.getString("username");
                String status = jsonObject.getString("status");
                double totalAmount = jsonObject.getDouble("totalAmount");

                // Check and extract dateTime
                String dateTime = jsonObject.isNull("dateTime")
                        ? null
                        : jsonObject.optString("dateTime", null);

                Order order = new Order(id, orderNumber, username, status, totalAmount, dateTime);
                orders.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }

    // Method to get the formatted current date
    private String getFormattedDate() {
        return java.time.LocalDate.now().toString();
    }

    // Method to get the last 7 days including today
    private List<String> getLast7Days() {
        List<String> last7Days = new ArrayList<>();
        java.time.LocalDate date = java.time.LocalDate.now();
        for (int i = 0; i < 7; i++) {
            last7Days.add(date.toString());
            date = date.minusDays(1);
        }
        return last7Days;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sales Report");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ReportManagement());
        frame.setPreferredSize(new Dimension(800, 600)); // Set preferred size for the frame
        frame.pack();
        frame.setVisible(true);
    }
}
