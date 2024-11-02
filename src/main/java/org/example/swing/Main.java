package org.example.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Main {
    private static JPanel mainPanel;
    private String username;
    private String role;

    public Main(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);  // เรียกหน้า Login ก่อน
        });
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Product Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLayout(new BorderLayout());

        // กำหนดสีหลักให้ UI
        Color primaryColor = new Color(45, 45, 45);
        Color secondaryColor = new Color(255, 87, 34);
        Color hoverColor = new Color(255, 112, 67);
        Color textColor = new Color(245, 245, 245);

        // สร้าง side menu panel พร้อมตกแต่ง
        JPanel sideMenu = new JPanel();
        sideMenu.setLayout(new GridLayout(7, 1, 10, 10)); // เพิ่มระยะห่างระหว่างปุ่ม
        sideMenu.setBackground(primaryColor);
        sideMenu.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // เพิ่มระยะขอบ

        // กำหนดฟอนต์
        Font menuFont = new Font("Arial", Font.BOLD, 18);

        // สร้าง Label สำหรับแสดง username และ role
        JLabel usernameLabel = new JLabel("Username: " + username); // ดึง username จากหน้า login
        usernameLabel.setFont(menuFont);
        usernameLabel.setForeground(textColor);

        JLabel roleLabel = new JLabel("Status: " + role); // ดึง role จากหน้า login
        roleLabel.setFont(menuFont);
        roleLabel.setForeground(textColor);

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new GridLayout(2, 1));
        labelPanel.setBackground(primaryColor);
        labelPanel.add(usernameLabel);
        labelPanel.add(roleLabel);

        // เพิ่ม labelPanel ไปยัง side menu
        sideMenu.add(labelPanel);

        // สร้างปุ่มเมนูด้วยการตกแต่งเพิ่มเติม
        JButton productButton = createMenuButton("Product Management", menuFont, textColor, secondaryColor, hoverColor);
        productButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showForm(new ProductManagement());
            }
        });

        JButton orderButton = createMenuButton("Order Management", menuFont, textColor, secondaryColor, hoverColor);
        orderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showForm(new OrderManagement());
            }
        });

        JButton userButton = createMenuButton("User Management", menuFont, textColor, secondaryColor, hoverColor);
        userButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showForm(new UserSwingApp());
            }
        });

        JButton reportButton = createMenuButton("Reports", menuFont, textColor, secondaryColor, hoverColor);
        reportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showForm(new ReportManagement());
            }
        });

        // สร้างปุ่ม Brand/Category/Role Management ด้วยการตกแต่งเพิ่มเติม
        JButton managementButton = createMenuButton("Brand/Category/Role Management", menuFont, textColor, secondaryColor, hoverColor);
        managementButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showForm(new BrandCategoryRoleManagement()); // ยังไม่กำหนด class นี้ คุณสามารถสร้าง class ตามต้องการได้
            }
        });

        // สร้างปุ่ม Logout ด้วยการตกแต่งเพิ่มเติม
        JButton logoutButton = createMenuButton("Logout", menuFont, textColor, secondaryColor, hoverColor);
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int response = JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to logout?",
                        "Logout Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    frame.dispose();  // ปิดโปรแกรม
                }
            }
        });

        // เพิ่มปุ่มไปยัง side menu
        sideMenu.add(productButton);
        sideMenu.add(orderButton);
        sideMenu.add(userButton);
        sideMenu.add(reportButton);
        sideMenu.add(managementButton); // เพิ่มปุ่มใหม่
        sideMenu.add(logoutButton);

        frame.add(sideMenu, BorderLayout.WEST);

        // สร้าง main panel สำหรับแสดงฟอร์ม โดยใช้ CardLayout
        mainPanel = new JPanel();
        mainPanel.setLayout(new CardLayout());
        frame.add(mainPanel, BorderLayout.CENTER);

        // เพิ่ม placeholderPanel
        JPanel placeholderPanel = new JPanel();
        placeholderPanel.setBackground(Color.LIGHT_GRAY);
        placeholderPanel.add(new JLabel("Select a menu option to view the form.", JLabel.CENTER));
        mainPanel.add(placeholderPanel, "Placeholder");

        frame.setVisible(true);
    }

    // สร้าง method เพื่อสร้างปุ่มเมนูด้วยการตกแต่งเพิ่มเติม
    private JButton createMenuButton(String text, Font font, Color textColor, Color bgColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(font);
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // เพิ่ม MouseListener เพื่อสร้าง hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    // Method to show a form in the main panel
    private void showForm(JPanel form) {
        mainPanel.removeAll();
        mainPanel.add(form);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
}