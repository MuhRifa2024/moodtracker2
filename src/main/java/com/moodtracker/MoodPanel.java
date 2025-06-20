package com.moodtracker;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MoodPanel extends JPanel {
    private JTextArea catatan;
    private String currentUser;

    public MoodPanel(String currentUser) {
        this.currentUser = currentUser;
        setOpaque(false);
        setLayout(new BorderLayout());
        initInputMoodPanel();
    }

    // Custom paintComponent untuk background gradient
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

    private void saveMood(String mood, String note) {
        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
        DatabaseHelper.insertMood(currentUser, mood, note, date);
        catatan.setText("");
    }

    private void showResult(String moodLabel) {
        removeAll();
        setLayout(new BorderLayout());

        // Panel atas: logo, judul, tanggal, tombol kanan atas
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        // Panel kiri (logo, judul, tanggal) vertikal
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));

        // Logo
        JLabel logoLabel = new JLabel();
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/assets/logo.png"));
        Image logoImage = logoIcon.getImage().getScaledInstance(249, 124, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(logoImage));
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(logoLabel);

        // Judul
        JLabel titleLabel = new JLabel("Pencatat Mood Harian");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(titleLabel);

        // Tanggal
        JLabel dateLabel = new JLabel(new SimpleDateFormat("EEEE, d MMM yyyy").format(new Date()));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(dateLabel);

        topPanel.add(leftPanel, BorderLayout.WEST);

        // Panel tombol kanan atas
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        buttonPanel.setOpaque(false);

        JButton btnTambahMood = new JButton("Catat Mood");
        btnTambahMood.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        btnTambahMood.setBackground(new Color(255, 200, 200));
        btnTambahMood.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnTambahMood.setFocusPainted(false);

        JButton btnRiwayat = new JButton("Riwayat Mood");
        btnRiwayat.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        btnRiwayat.setBackground(new Color(255, 200, 200));
        btnRiwayat.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnRiwayat.setFocusPainted(false);

        buttonPanel.add(btnTambahMood);
        buttonPanel.add(btnRiwayat);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Panel tengah (slider vertikal, karakter, label mood)
        JPanel centerPanel = new JPanel(null);
        centerPanel.setOpaque(false);
        centerPanel.setPreferredSize(new Dimension(1000, 400));

        // Mapping label sentimen ke gambar dan teks
        String karakterPath;
        String moodText;
        switch (moodLabel.toLowerCase()) {
            case "very negative":
                karakterPath = "/assets/angry.png";
                moodText = "Marah";
                break;
            case "negative":
                karakterPath = "/assets/sad.png";
                moodText = "Sedih";
                break;
            case "neutral":
                karakterPath = "/assets/neutral.png";
                moodText = "Netral";
                break;
            case "positive":
                karakterPath = "/assets/happy.png";
                moodText = "Bahagia";
                break;
            case "very positive":
                karakterPath = "/assets/very_happy.png";
                moodText = "Sangat Bahagia";
                break;
            default:
                karakterPath = "/assets/neutral.png";
                moodText = moodLabel;
        }

        // Karakter (tengah)
        JLabel karakterLabel = new JLabel();
        ImageIcon karakterIcon = new ImageIcon(getClass().getResource(karakterPath));
        Image karakterImg = karakterIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
        karakterLabel.setIcon(new ImageIcon(karakterImg));
        karakterLabel.setBounds(400, 80, 250, 250);
        centerPanel.add(karakterLabel);

        // Label mood kanan atas
        JLabel moodLabelText = new JLabel(moodText);
        moodLabelText.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        moodLabelText.setBounds(700, 120, 400, 40);
        centerPanel.add(moodLabelText);

        add(centerPanel, BorderLayout.CENTER);

        // Pesan motivasi kiri bawah
        JPanel bottomPanel = new JPanel(null);
        bottomPanel.setOpaque(false);
        bottomPanel.setPreferredSize(new Dimension(1000, 100));

        JLabel pesan1 = new JLabel("Ayoo semangat!!!");
        pesan1.setFont(new Font("Comic Sans MS", Font.BOLD, 26));
        pesan1.setBounds(80, 10, 600, 40);
        bottomPanel.add(pesan1);

        JLabel pesan2 = new JLabel("Semoga harimu lebih baik dan kamu lebih bahagia");
        pesan2.setFont(new Font("Comic Sans MS", Font.PLAIN, 22));
        pesan2.setBounds(80, 50, 800, 30);
        bottomPanel.add(pesan2);

        add(bottomPanel, BorderLayout.SOUTH);

        // Aksi tombol
        btnTambahMood.addActionListener(_ -> {
            initInputMoodPanel();
        });

        btnRiwayat.addActionListener(_ -> {
            showRiwayatMood();
        });

        revalidate();
        repaint();
    }

    // Panel input mood (form awal)
    private void initInputMoodPanel() {
        removeAll();
        setLayout(new BorderLayout());

        // Panel atas: logo, judul, tanggal
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        // Logo kiri atas
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        logoPanel.setOpaque(false);
        JLabel logoLabel = new JLabel();
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/assets/logo.png"));
        Image logoImage = logoIcon.getImage().getScaledInstance(249, 124, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(logoImage));
        logoPanel.add(logoLabel);
        topPanel.add(logoPanel);

        // Judul besar
        JLabel titleLabel = new JLabel("Pencatat Mood Harian");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 32));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(titleLabel);

        // Tanggal
        JLabel dateLabel = new JLabel(new SimpleDateFormat("EEEE, d MMM yyyy").format(new Date()));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(dateLabel);

        add(topPanel, BorderLayout.NORTH);

        // Panel tengah: slider, input, tombol
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));

        // Input catatan
        catatan = new JTextArea(2, 20);
        catatan.setFont(new Font("Arial", Font.PLAIN, 16));
        catatan.setLineWrap(true);
        catatan.setWrapStyleWord(true);
        catatan.setMaximumSize(new Dimension(350, 40));
        catatan.setAlignmentX(Component.CENTER_ALIGNMENT);
        catatan.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        catatan.setText("Tulis Perasaanmu hari ini.....");

        // Tambahkan FocusListener
        catatan.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (catatan.getText().equals("Tulis Perasaanmu hari ini.....")) {
                    catatan.setText("");
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (catatan.getText().isEmpty()) {
                    catatan.setText("Tulis Perasaanmu hari ini.....");
                }
            }
        });

        // Tombol simpan
        JButton saveButton = new JButton("Simpan");
        saveButton.setFont(new Font("Arial", Font.BOLD, 16));
        saveButton.setBackground(new Color(30, 60, 120));
        saveButton.setForeground(Color.WHITE);
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveButton.setMaximumSize(new Dimension(200, 40));
        saveButton.addActionListener(_ -> {
            String note = catatan.getText().trim();
            if (note.equals("Tulis Perasaanmu hari ini.....") || note.isEmpty()) {
                note = "";
            }
            String hasilSentimen = "";
            try {
                int stars = SentimentClient.getSentiment(note);
                hasilSentimen = SentimentClient.mapStarsToLabel(stars);
                System.out.println("Sentimen catatan: " + hasilSentimen);
            } catch (Exception ex) {
                hasilSentimen = "neutral";
                ex.printStackTrace();
            }

            saveMood(hasilSentimen, note); // simpan label sentimen
            showResult(hasilSentimen);     // tampilkan hasil berdasarkan label
        });

        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(catatan);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(saveButton);

        add(centerPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    // Panel riwayat mood (dummy, silakan sesuaikan dengan data dari database)
    private void showRiwayatMood() {
        removeAll();
        setLayout(new BorderLayout());

        // Panel filter atas
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setOpaque(false);

        String[] filterOptions = {"Hari Ini", "Semua", "Pilih Tanggal"};
        JComboBox<String> filterCombo = new JComboBox<>(filterOptions);

        JTextField tanggalField = new JTextField(10);
        tanggalField.setVisible(false);

        filterPanel.add(new JLabel("Filter:"));
        filterPanel.add(filterCombo);
        filterPanel.add(tanggalField);

        add(filterPanel, BorderLayout.NORTH);

        // Panel tabel
        JPanel tablePanel = new JPanel(new BorderLayout());
        add(tablePanel, BorderLayout.CENTER);

        // Method untuk refresh tabel sesuai filter
        Runnable refreshTable = () -> {
            java.util.List<String[]> data;
            String selected = (String) filterCombo.getSelectedItem();
            String today = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

            if ("Semua".equals(selected)) {
                data = DatabaseHelper.getMoodHistory(currentUser); // semua data
            } else if ("Pilih Tanggal".equals(selected)) {
                String tanggal = tanggalField.getText().trim();
                data = DatabaseHelper.getMoodHistoryByDate(currentUser, tanggal); // buat method ini di DatabaseHelper
            } else {
                // Default: hari ini
                data = DatabaseHelper.getMoodHistoryByDate(currentUser, today); // buat method ini di DatabaseHelper
            }

            String[] columns = {"Mood", "Catatan", "Tanggal"};
            String[][] tableData = data.toArray(new String[0][]);
            JTable table = new JTable(tableData, columns);
            JScrollPane scrollPane = new JScrollPane(table);

            tablePanel.removeAll();
            tablePanel.add(scrollPane, BorderLayout.CENTER);
            tablePanel.revalidate();
            tablePanel.repaint();
        };

        // Listener filter
        filterCombo.addActionListener(_ -> {
            String selected = (String) filterCombo.getSelectedItem();
            tanggalField.setVisible("Pilih Tanggal".equals(selected));
            refreshTable.run();
        });

        tanggalField.addActionListener(_ -> refreshTable.run());

        // Tombol kembali
        JButton btnKembali = new JButton("Kembali");
        btnKembali.setFont(new Font("Arial", Font.BOLD, 16));
        btnKembali.addActionListener(_ -> initInputMoodPanel());

        JPanel panelBtn = new JPanel();
        panelBtn.setOpaque(false);
        panelBtn.add(btnKembali);

        add(panelBtn, BorderLayout.SOUTH);

        // Tampilkan tabel pertama kali (default: hari ini)
        refreshTable.run();

        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        // Contoh penggunaan SentimentClient
        String kalimat = "Saya sangat senang hari ini!";

        try {
            int stars = SentimentClient.getSentiment(kalimat);
            String label = SentimentClient.mapStarsToLabel(stars);
            System.out.println("Hasil sentimen: " + label + " (" + stars + " stars)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}