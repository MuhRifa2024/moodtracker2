package com.moodtracker;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class MoodPanel extends JPanel {
    private JTextArea catatan;
    private String currentUser;
    private String currentGender;

    public MoodPanel(String currentUser, String currentGender) {
        this.currentUser = currentUser;
        this.currentGender = currentGender;
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
        String genderSuffix = currentGender.equalsIgnoreCase("Perempuan") ? "_f" : "_m";
        String karakterPath;
        String moodText; // <-- Tambahkan inisialisasi moodText

        switch (moodLabel.toLowerCase()) {
            case "very negative":
                karakterPath = "/assets/angry" + genderSuffix + ".png";
                moodText = "Marah";
                break;
            case "negative":
                karakterPath = "/assets/sad" + genderSuffix + ".png";
                moodText = "Sedih";
                break;
            case "neutral":
                karakterPath = "/assets/neutral" + genderSuffix + ".png";
                moodText = "Netral";
                break;
            case "positive":
                karakterPath = "/assets/happy" + genderSuffix + ".png";
                moodText = "Bahagia";
                break;
            case "very positive":
                karakterPath = "/assets/very_happy" + genderSuffix + ".png";
                moodText = "Sangat Bahagia";
                break;
            default:
                karakterPath = "/assets/neutral" + genderSuffix + ".png";
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
        pesan2.setFont(new Font("Arial", Font.PLAIN, 22));
        pesan2.setBounds(80, 50, 800, 30);
        bottomPanel.add(pesan2);

        add(bottomPanel, BorderLayout.SOUTH);

        // Aksi tombol
        btnTambahMood.addActionListener(_ -> {
            initInputMoodPanel();
        });

        btnRiwayat.addActionListener(_ -> {
            showRiwayatMoodChart();
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
        catatan = new JTextArea(2, 30);
        catatan.setFont(new Font("Arial", Font.PLAIN, 16));
        catatan.setLineWrap(true);
        catatan.setWrapStyleWord(true);
        catatan.setText("Tulis Perasaanmu hari ini.....");

        // Focus listener untuk placeholder
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

        // Bungkus dengan JScrollPane
        JScrollPane scrollCatatan = new JScrollPane(catatan);
        scrollCatatan.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollCatatan.setPreferredSize(new Dimension(400, 40)); // ukuran awal kecil

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
            System.out.println("Input ke SentimentClient: " + note);
            try {
                int stars = SentimentClient.getSentiment(note);
                hasilSentimen = SentimentClient.mapStarsToLabel(stars);
                System.out.println("Sentimen catatan: " + hasilSentimen);
            } catch (Exception ex) {
                hasilSentimen = "neutral";
                ex.printStackTrace();
            }
            saveMood(hasilSentimen, note);
            showResult(hasilSentimen);
        });

        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(scrollCatatan); // tambahkan scroll pane, bukan langsung catatan
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(saveButton);

        add(centerPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }


    private void showRiwayatMoodChart() {
        removeAll();
        setLayout(null);

        // Logo kiri atas
        JLabel logoLabel = new JLabel();
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/assets/logo.png"));
        Image logoImage = logoIcon.getImage().getScaledInstance(120, 60, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(logoImage));
        logoLabel.setBounds(30, 20, 120, 60);
        add(logoLabel);

        // Judul
        JLabel titleLabel = new JLabel("Riwayat Mood");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 38));
        titleLabel.setBounds(40, 90, 350, 50);
        add(titleLabel);

        // Subjudul
        JLabel subTitle = new JLabel("Hari ini");
        subTitle.setFont(new Font("Arial", Font.PLAIN, 20));
        subTitle.setBounds(50, 135, 200, 30);
        add(subTitle);

        // Ikon filter
        JLabel filterIcon = new JLabel();
        ImageIcon filterImg = new ImageIcon(getClass().getResource("/assets/filter.png"));
        filterIcon.setIcon(new ImageIcon(filterImg.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        filterIcon.setBounds(350, 90, 40, 40);
        add(filterIcon);

        // Tombol kembali kanan atas
        JButton btnKembali = new JButton("KEMBALI");
        btnKembali.setFont(new Font("Arial", Font.BOLD, 18));
        btnKembali.setBackground(new Color(255, 200, 210));
        btnKembali.setBounds(900, 30, 150, 40);
        btnKembali.setFocusPainted(false);
        btnKembali.addActionListener(_ -> initInputMoodPanel());
        add(btnKembali);

        // === DATA DARI DATABASE ===
        java.util.List<String[]> data = DatabaseHelper.getMoodHistory(currentUser);

        // Ambil semua kategori mood unik dari data (urutkan sesuai urutan kemunculan)
        LinkedHashSet<String> moodSet = new LinkedHashSet<>();
        for (String[] row : data) {
            moodSet.add(row[0].toUpperCase());
        }
        java.util.List<String> moodCategories = new ArrayList<>(moodSet);

        // Mapping mood ke nilai Y
        java.util.Map<String, Integer> moodToValue = new HashMap<>();
        for (int i = 0; i < moodCategories.size(); i++) {
            moodToValue.put(moodCategories.get(i).toLowerCase(), i + 1);
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (String[] row : data) {
            String mood = row[0].toLowerCase();
            String tanggal = row[2]; // format: dd-MM-yyyy HH:mm
            String jam = tanggal.split(" ")[1]; // ambil HH:mm
            int value = moodToValue.getOrDefault(mood, 1);
            dataset.addValue(value, mood, jam);
        }

        JFreeChart barChart = ChartFactory.createBarChart(
            "", // title
            "", // category axis label
            "", // value axis label
            dataset
        );

        // Customisasi chart agar mirip desain
        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(0,0,0,0)); // transparan
        plot.setOutlineVisible(false);
        plot.setRangeGridlinesVisible(false);
        plot.getDomainAxis().setTickLabelFont(new Font("Arial", Font.PLAIN, 18));
        plot.getRangeAxis().setTickLabelFont(new Font("Arial", Font.PLAIN, 18));
        plot.getDomainAxis().setCategoryMargin(0.2);

        // Sembunyikan label angka sumbu Y
        plot.getRangeAxis().setTickLabelsVisible(false);
        plot.getRangeAxis().setRange(0, moodCategories.size() + 0.5);

        // Custom warna bar
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(40, 30, 60));
        renderer.setBarPainter(new BarRenderer().getBarPainter());
        renderer.setShadowVisible(false);

        // Panel chart
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setOpaque(false);
        chartPanel.setBackground(new Color(0,0,0,0));
        chartPanel.setBounds(60, 180, 900, 400);
        chartPanel.setBorder(BorderFactory.createEmptyBorder());
        add(chartPanel);

        // Custom label sumbu Y manual (dinamis)
        int chartHeight = 400;
        int chartBottom = 580;
        int yStep = moodCategories.size() > 1 ? chartHeight / (moodCategories.size() - 1) : chartHeight;
        for (int i = 0; i < moodCategories.size(); i++) {
            JLabel yLabel = new JLabel(moodCategories.get(i));
            yLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            yLabel.setBounds(10, chartBottom - i * yStep, 180, 30);
            add(yLabel);
        }

        // FILTER ICON INTERAKTIF
        filterIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        filterIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JOptionPane.showMessageDialog(null, "Fitur filter belum diimplementasikan.");
            }
        });

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