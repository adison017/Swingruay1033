package org.example.swing;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ApiService {
    private static final String BASE_URL = "http://localhost:8080/api"; // เปลี่ยนให้ตรงกับ URL ของ API ของคุณ

    // เรียก API ดึงข้อมูลสินค้า
    public static List<String[]> getProducts() {
        List<String[]> products = new ArrayList<>();
        try {
            URL url = new URL(BASE_URL + "/Products");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String[] product = inputLine.split(","); // ปรับตามโครงสร้าง JSON ของคุณ
                    products.add(product);
                }
                in.close();
            } else {
                System.out.println("GET request failed: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    // เพิ่มสินค้าใหม่
    public static void addProduct(String name, String description, String price, String stock, String category) {
        try {
            URL url = new URL("http://localhost:8080/api/Products");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // สร้าง JSON สำหรับส่งไปยัง API
            String jsonInputString = String.format(
                    "{\"name\": \"%s\", \"description\": \"%s\", \"price\": \"%s\", \"stock\": \"%s\", \"category\": \"%s\"}",
                    name, description, price, stock, category
            );

            // ส่ง JSON ไปยัง API
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // เรียก API ดึงข้อมูลคำสั่งซื้อ
    public static List<String[]> getOrders() {
        List<String[]> orders = new ArrayList<>();
        try {
            URL url = new URL(BASE_URL + "/orders");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String[] order = inputLine.split(","); // ปรับตามโครงสร้าง JSON ของคุณ
                    orders.add(order);
                }
                in.close();
            } else {
                System.out.println("GET request failed: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }

    // เปลี่ยนสถานะคำสั่งซื้อ
    public static void updateOrderStatus(String orderId, String status) {
        try {
            URL url = new URL(BASE_URL + "/orders/" + orderId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = String.format("{\"status\":\"%s\"}", status);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.out.println("PUT request failed: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // เรียก API ดึงข้อมูลผู้ใช้
    public static List<String[]> getUsers() {
        List<String[]> users = new ArrayList<>();
        try {
            URL url = new URL(BASE_URL + "/users");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String[] user = inputLine.split(","); // ปรับตามโครงสร้าง JSON ของคุณ
                    users.add(user);
                }
                in.close();
            } else {
                System.out.println("GET request failed: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    // เพิ่มผู้ใช้ใหม่
    public static void addUser(String username, String email, String role) {
        try {
            URL url = new URL(BASE_URL + "/users");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = String.format("{\"username\":\"%s\", \"email\":\"%s\", \"role\":\"%s\"}", username, email, role);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                System.out.println("POST request failed: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // เรียก API รายงานการขาย
    public static List<String[]> getSalesReport() {
        List<String[]> salesReport = new ArrayList<>();
        try {
            URL url = new URL(BASE_URL + "/sales/report");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String[] report = inputLine.split(","); // ปรับตามโครงสร้าง JSON ของคุณ
                    salesReport.add(report);
                }
                in.close();
            } else {
                System.out.println("GET request failed: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return salesReport;
    }

    // เรียก API รายงานยอดสต็อกสินค้า
    public static List<String[]> getStockReport() {
        List<String[]> stockReport = new ArrayList<>();
        try {
            URL url = new URL(BASE_URL + "/stock/report");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String[] report = inputLine.split(","); // ปรับตามโครงสร้าง JSON ของคุณ
                    stockReport.add(report);
                }
                in.close();
            } else {
                System.out.println("GET request failed: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stockReport;
    }
}
