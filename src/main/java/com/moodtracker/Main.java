package com.moodtracker;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            DatabaseHelper.initDatabase();
            showLoginScreen();
        });
    }

    private static void showLoginScreen() {
        System.out.println("Menampilkan login screen...");
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(700, 600);
        loginFrame.setLocationRelativeTo(null);

        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(120, 180, 255);
                Color color2 = new Color(200, 220, 255);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        bgPanel.setLayout(new GridBagLayout());

        JPanel loginPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(186, 129, 97));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
            }
        };
        loginPanel.setOpaque(false);
        loginPanel.setPreferredSize(new Dimension(500, 450));
        loginPanel.setLayout(null);

        // Logo
        JLabel logoLabel = new JLabel();
        ImageIcon logoIcon = new ImageIcon(Main.class.getResource("/assets/logo.png"));
        Image logoImage = logoIcon.getImage().getScaledInstance(259, 134, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(logoImage));
        logoLabel.setBounds(120, 5, 259, 134);
        loginPanel.add(logoLabel);

        // Judul
        JLabel titleLabel = new JLabel("Selamat Datang");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 32));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(100, 100, 300, 40);
        loginPanel.add(titleLabel);

        // Username
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        userLabel.setBounds(70, 160, 100, 20);
        loginPanel.add(userLabel);

        JTextField usernameField = new JTextField();
        usernameField.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        usernameField.setBounds(70, 185, 360, 45);
        usernameField.setBackground(Color.WHITE);
        usernameField.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        usernameField.setOpaque(true);
        usernameField.setMargin(new Insets(0, 10, 0, 10));
        usernameField.setColumns(20);
        usernameField.setCaretColor(Color.BLACK);
        usernameField.setForeground(Color.BLACK);
        loginPanel.add(usernameField);

        // Password
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        passLabel.setBounds(70, 240, 100, 20);
        loginPanel.add(passLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        passwordField.setBounds(70, 265, 360, 45);
        passwordField.setBackground(Color.WHITE);
        passwordField.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        passwordField.setOpaque(true);
        passwordField.setMargin(new Insets(0, 10, 0, 10));
        passwordField.setCaretColor(Color.BLACK);
        passwordField.setForeground(Color.BLACK);
        loginPanel.add(passwordField);

        // Tombol Masuk
        JButton loginBtn = new JButton("Masuk");
        loginBtn.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        loginBtn.setBackground(new Color(30, 60, 120));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBounds(70, 330, 360, 50);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorder(BorderFactory.createEmptyBorder());
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.setOpaque(true);
        loginPanel.add(loginBtn);

        // Label register
        JLabel regLabel = new JLabel("Belum punya akun?");
        regLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        regLabel.setBounds(110, 390, 180, 30);
        loginPanel.add(regLabel);

        JButton registerBtn = new JButton("<html><u>Register</u></html>");
        registerBtn.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        registerBtn.setForeground(new Color(30, 60, 120));
        registerBtn.setBackground(new Color(186, 129, 97));
        registerBtn.setBorder(BorderFactory.createEmptyBorder());
        registerBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerBtn.setBounds(260, 390, 100, 30);
        loginPanel.add(registerBtn);

        // Action login
        loginBtn.addActionListener(_ -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginFrame, "Username dan password tidak boleh kosong!");
                return;
            }

            if (AuthService.login(username, password)) {
                loginFrame.dispose();
                String gender = DatabaseHelper.getGenderByUsername(username);
                launchMainApp(username, gender);
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Login gagal! Periksa username dan password Anda.");
            }
        });

        // Action register
        registerBtn.addActionListener(_ -> {
            loginFrame.dispose();
            showRegisterScreen();
        });

        bgPanel.add(loginPanel, new GridBagConstraints());
        loginFrame.setContentPane(bgPanel);
        loginFrame.setResizable(false);
        loginFrame.setVisible(true);
    }

    private static void launchMainApp(String username, String gender) {
        JFrame frame = new JFrame("Mood Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLocationRelativeTo(null);

        MoodPanel moodPanel = new MoodPanel(username, gender);
        frame.add(moodPanel);

        frame.setVisible(true);
    }

    private static void showRegisterScreen() {
        JFrame registerFrame = new JFrame("Register");
        registerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        registerFrame.setSize(700, 600);
        registerFrame.setLocationRelativeTo(null);

        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(120, 180, 255);
                Color color2 = new Color(200, 220, 255);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        bgPanel.setLayout(new GridBagLayout());

        JPanel registerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(186, 129, 97));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
            }
        };
        registerPanel.setOpaque(false);
        registerPanel.setPreferredSize(new Dimension(500, 450));
        registerPanel.setLayout(null);

        // Logo
        JLabel logoLabel = new JLabel();
        ImageIcon logoIcon = new ImageIcon(Main.class.getResource("/assets/logo.png"));
        Image logoImage = logoIcon.getImage().getScaledInstance(259, 134, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(logoImage));
        logoLabel.setBounds(120, 5, 259, 134);
        registerPanel.add(logoLabel);

        // Judul
        JLabel titleLabel = new JLabel("Buat Akun");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 32));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(100, 100, 300, 40);
        registerPanel.add(titleLabel);

        // Username
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        userLabel.setBounds(70, 160, 100, 20);
        registerPanel.add(userLabel);

        JTextField usernameField = new JTextField();
        usernameField.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        usernameField.setBounds(70, 185, 360, 45);
        usernameField.setBackground(Color.WHITE);
        usernameField.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        usernameField.setOpaque(true);
        usernameField.setMargin(new Insets(0, 10, 0, 10));
        usernameField.setColumns(20);
        usernameField.setCaretColor(Color.BLACK);
        usernameField.setForeground(Color.BLACK);
        registerPanel.add(usernameField);

        // Password
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        passLabel.setBounds(70, 240, 100, 20);
        registerPanel.add(passLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        passwordField.setBounds(70, 265, 360, 45);
        passwordField.setBackground(Color.WHITE);
        passwordField.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        passwordField.setOpaque(true);
        passwordField.setMargin(new Insets(0, 10, 0, 10));
        passwordField.setCaretColor(Color.BLACK);
        passwordField.setForeground(Color.BLACK);
        registerPanel.add(passwordField);

        // Jenis Kelamin
        JLabel genderLabel = new JLabel("Jenis Kelamin:");
        genderLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        genderLabel.setBounds(70, 320, 120, 20);
        registerPanel.add(genderLabel);

        String[] genderOptions = {"Laki-laki", "Perempuan"};
        JComboBox<String> genderCombo = new JComboBox<>(genderOptions);
        genderCombo.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        genderCombo.setBounds(200, 315, 230, 30);
        registerPanel.add(genderCombo);

        // Tombol Register
        JButton registerBtn = new JButton("Register");
        registerBtn.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        registerBtn.setBackground(new Color(30, 60, 120));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setBounds(70, 370, 360, 50);
        registerBtn.setFocusPainted(false);
        registerBtn.setBorder(BorderFactory.createEmptyBorder());
        registerBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerBtn.setOpaque(true);
        registerPanel.add(registerBtn);

        // Action registerBtn: lakukan proses registrasi
        registerBtn.addActionListener(_ -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String selectedGender = (String) genderCombo.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(registerFrame, "Username dan password tidak boleh kosong!");
                return;
            }

            if (AuthService.register(username, password, selectedGender)) {
                JOptionPane.showMessageDialog(registerFrame, "Registrasi berhasil!");
                registerFrame.dispose();
                showLoginScreen();
            } else {
                JOptionPane.showMessageDialog(registerFrame, "Registrasi gagal! Username mungkin sudah digunakan.");
            }
        });

        bgPanel.add(registerPanel, new GridBagConstraints());
        registerFrame.setContentPane(bgPanel);
        registerFrame.setResizable(false);
        registerFrame.setVisible(true);
    }
}