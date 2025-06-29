package com.moodtracker;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;
// import org.jfree.chart.ChartFactory;
// import org.jfree.chart.ChartPanel;
// import org.jfree.chart.JFreeChart;
// import org.jfree.chart.plot.CategoryPlot;
// import org.jfree.chart.renderer.category.BarRenderer;
// import org.jfree.data.category.DefaultCategoryDataset;
// import java.util.stream.Collectors;

public class MoodPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private JTextArea catatan;
    private String currentUser;
    private String currentGender;

    // Mapping emosi ke kategori mood
    private static final Map<String, String> EMOTION_TO_CATEGORY = Map.ofEntries(
        Map.entry("gembira", "very positive"),
        Map.entry("bersemangat", "very positive"),
        Map.entry("bersyukur", "very positive"),
        Map.entry("senang", "very positive"),
        Map.entry("bahagia", "positive"),
        Map.entry("tenang", "positive"),
        Map.entry("puas", "positive"),
        Map.entry("netral", "neutral"),
        Map.entry("biasa saja", "neutral"),
        Map.entry("bosan", "neutral"),
        Map.entry("mengantuk", "neutral"),
        Map.entry("sedih", "negative"),
        Map.entry("cemas", "negative"),
        Map.entry("kesepian", "negative"),
        Map.entry("marah", "very negative"),
        Map.entry("frustrasi", "very negative"),
        Map.entry("putus asa", "very negative")
    );

    // Motivasi dua baris per kategori
    private static final Map<String, List<String[]>> MOTIVASI_PER_KATEGORI = Map.of(
        "very positive", List.of(
            new String[]{"Pertahankan semangatmu hari ini!", "Terus tebarkan energi positif ke sekitarmu."},
            new String[]{"Kamu luar biasa, teruskan!", "Jangan lupa bersyukur atas kebahagiaanmu."},
            new String[]{"Energi positifmu menular!", "Semoga hari-harimu selalu ceria."},
            new String[]{"Hari ini penuh kebahagiaan!", "Bagikan senyummu pada dunia."},
            new String[]{"Jangan lupa bersyukur atas kebahagiaanmu!", "Kebahagiaanmu adalah inspirasi bagi orang lain."}
        ),
        "positive", List.of(
            new String[]{"Nikmati hari yang indah ini.", "Syukuri setiap hal kecil hari ini."},
            new String[]{"Tetap tenang dan terus maju.", "Kamu sudah melakukan yang terbaik."},
            new String[]{"Kamu sudah melakukan yang terbaik.", "Teruskan langkah positifmu!"},
            new String[]{"Syukuri setiap hal kecil hari ini.", "Hari baik dimulai dari hati yang tenang."},
            new String[]{"Teruskan langkah positifmu!", "Semoga harimu penuh kedamaian."}
        ),
        "neutral", List.of(
            new String[]{"Hari biasa juga penting untuk istirahat.", "Gunakan waktu ini untuk refleksi diri."},
            new String[]{"Tidak apa-apa jika harimu biasa saja.", "Besok bisa jadi lebih baik!"},
            new String[]{"Gunakan waktu ini untuk refleksi diri.", "Tetap jalani hari dengan santai."},
            new String[]{"Besok bisa jadi lebih baik!", "Setiap hari punya cerita sendiri."},
            new String[]{"Tetap jalani hari dengan santai.", "Nikmati momen sederhana hari ini."}
        ),
        "negative", List.of(
            new String[]{"Tidak apa-apa merasa sedih, kamu tidak sendiri.", "Ceritakan perasaanmu pada orang terdekat."},
            new String[]{"Ceritakan perasaanmu pada orang terdekat.", "Semua akan berlalu, tetap kuat."},
            new String[]{"Semua akan berlalu, tetap kuat.", "Ambil waktu untuk dirimu sendiri."},
            new String[]{"Ambil waktu untuk dirimu sendiri.", "Kamu berharga, apapun yang terjadi."},
            new String[]{"Kamu berharga, apapun yang terjadi.", "Jangan ragu untuk meminta bantuan."}
        ),
        "very negative", List.of(
            new String[]{"Tarik napas dalam-dalam, kamu bisa melewati ini.", "Jangan menyerah, hari buruk akan berlalu."},
            new String[]{"Jangan menyerah, hari buruk akan berlalu.", "Cari bantuan jika kamu butuh, itu bukan kelemahan."},
            new String[]{"Cari bantuan jika kamu butuh, itu bukan kelemahan.", "Kamu lebih kuat dari yang kamu kira."},
            new String[]{"Kamu lebih kuat dari yang kamu kira.", "Ingat, selalu ada harapan di setiap situasi."},
            new String[]{"Ingat, selalu ada harapan di setiap situasi.", "Kamu tidak sendiri, tetap bertahan."}
        )
    );

    // Tambahan variabel untuk menyimpan hasil mood terakhir
    private String lastMoodLabel = null;
    private String lastEmosiAsli = null;

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

    private void saveMood(String mood, String emosiAsli, String note) {
        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
        DatabaseHelper.insertMood(currentUser, mood, emosiAsli, note, date);
        catatan.setText("");
    }

    private void showResult(String moodLabel, String emosiAsli) {
        // Simpan hasil terakhir
        lastMoodLabel = moodLabel;
        lastEmosiAsli = emosiAsli;

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
        java.net.URL logoUrl = getClass().getResource("/assets/logo.png");
        if (logoUrl != null) {
            ImageIcon logoIcon = new ImageIcon(logoUrl);
            Image logoImage = logoIcon.getImage().getScaledInstance(249, 124, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(logoImage));
        } else {
            logoLabel.setText("Logo tidak ditemukan");
        }
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
        switch (moodLabel.toLowerCase()) {
            case "very negative":
                karakterPath = "/assets/angry" + genderSuffix + ".png";
                break;
            case "negative":
                karakterPath = "/assets/sad" + genderSuffix + ".png";
                break;
            case "neutral":
                karakterPath = "/assets/neutral" + genderSuffix + ".png";
                break;
            case "positive":
                karakterPath = "/assets/happy" + genderSuffix + ".png";
                break;
            case "very positive":
                karakterPath = "/assets/very_happy" + genderSuffix + ".png";
                break;
            default:
                karakterPath = "/assets/neutral" + genderSuffix + ".png";
        }

        // Karakter (tengah)
        JLabel karakterLabel = new JLabel();
        java.net.URL karakterUrl = getClass().getResource(karakterPath);
        if (karakterUrl != null) {
            ImageIcon karakterIcon = new ImageIcon(karakterUrl);
            Image karakterImg = karakterIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
            karakterLabel.setIcon(new ImageIcon(karakterImg));
        } else {
            karakterLabel.setText("Gambar tidak ditemukan");
        }
        karakterLabel.setBounds(400, 80, 250, 250);
        centerPanel.add(karakterLabel);

        // Ganti label mood kanan atas:
        JLabel moodLabelText = new JLabel(emosiAsli != null && !emosiAsli.isEmpty() ? capitalize(emosiAsli) : capitalize(moodLabel));
        moodLabelText.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        moodLabelText.setBounds(700, 120, 400, 40);
        centerPanel.add(moodLabelText);

        add(centerPanel, BorderLayout.CENTER);

        // Pesan motivasi kiri bawah
        JPanel bottomPanel = new JPanel(null);
        bottomPanel.setOpaque(false);
        bottomPanel.setPreferredSize(new Dimension(1000, 100));

        // Pilih motivasi random sesuai kategori
        List<String[]> motivasiList = MOTIVASI_PER_KATEGORI.getOrDefault(
            moodLabel.toLowerCase(),
            new ArrayList<String[]>() {{
                add(new String[]{"Semangat menjalani hari!", "Semoga harimu lebih baik dan kamu lebih bahagia"});
            }}
        );
        String[] motivasiRandom = motivasiList.get(new Random().nextInt(motivasiList.size()));

        JLabel pesan1 = new JLabel(motivasiRandom[0]);
        pesan1.setFont(new Font("Comic Sans MS", Font.BOLD, 26));
        pesan1.setBounds(80, 10, 800, 40);
        bottomPanel.add(pesan1);

        JLabel pesan2 = new JLabel(motivasiRandom[1]);
        pesan2.setFont(new Font("Arial", Font.PLAIN, 22));
        pesan2.setBounds(80, 50, 800, 30);
        bottomPanel.add(pesan2);

        add(bottomPanel, BorderLayout.SOUTH);

        // Aksi tombol
        btnTambahMood.addActionListener(_ -> {
            initInputMoodPanel();
        });

        btnRiwayat.addActionListener(_ -> {
            showRiwayatMoodTable();
        });

        revalidate();
        repaint();
    }

    // Fungsi deteksi mood berdasarkan kata kunci emosi
    private String detectMoodByKeyword(String note) {
        String lower = note.toLowerCase();
        for (Map.Entry<String, String> entry : EMOTION_TO_CATEGORY.entrySet()) {
            if (lower.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null; // Tidak ditemukan, lanjut ke SentimentClient
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
        java.net.URL logoUrl = getClass().getResource("/assets/logo.png");
        if (logoUrl != null) {
            ImageIcon logoIcon = new ImageIcon(logoUrl);
            Image logoImage = logoIcon.getImage().getScaledInstance(249, 124, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(logoImage));
        } else {
            logoLabel.setText("Logo tidak ditemukan");
        }
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
                JOptionPane.showMessageDialog(this, "Silakan tulis perasaanmu terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String hasilSentimen = detectMoodByKeyword(note);
            String emosiAsli = detectEmotionKeyword(note);
            if (hasilSentimen == null) {
                try {
                    int stars = SentimentClient.getSentiment(note);
                    hasilSentimen = SentimentClient.mapStarsToLabel(stars);
                    // emosiAsli = hasilSentimen; // <-- ini salah, karena hasilSentimen adalah kategori!
                    emosiAsli = ""; // atau null, agar label tidak menampilkan kategori
                } catch (Exception ex) {
                    hasilSentimen = "neutral";
                    emosiAsli = "";
                    ex.printStackTrace();
                }
            }
            saveMood(hasilSentimen, emosiAsli, note);
            showResult(hasilSentimen, emosiAsli);
        });

        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(scrollCatatan); // tambahkan scroll pane, bukan langsung catatan
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(saveButton);

        add(centerPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private void showRiwayatMoodTable(String filterTanggal) {
        removeAll();
        setLayout(new BorderLayout());

        // Panel judul dan filter
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setPreferredSize(new Dimension(getWidth(), 140));

        JPanel leftPanel = new JPanel(null);
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(600, 140));

        JLabel titleLabel = new JLabel("Riwayat Mood");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 38));
        titleLabel.setBounds(40, 40, 350, 50);
        leftPanel.add(titleLabel);

        JLabel subTitle = new JLabel("Tanggal: " + filterTanggal);
        subTitle.setFont(new Font("Arial", Font.PLAIN, 20));
        subTitle.setBounds(50, 90, 300, 30);
        leftPanel.add(subTitle);

        JLabel filterIcon = new JLabel();
        java.net.URL filterUrl = getClass().getResource("/assets/filter.png");
        if (filterUrl != null) {
            ImageIcon filterImg = new ImageIcon(filterUrl);
            filterIcon.setIcon(new ImageIcon(filterImg.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        } else {
            filterIcon.setText("Filter");
        }
        filterIcon.setBounds(350, 40, 40, 40);
        leftPanel.add(filterIcon);

        topPanel.add(leftPanel, BorderLayout.WEST);

        // Tombol kembali di pojok kanan atas
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 30));
        rightPanel.setOpaque(false);
        JButton btnKembali = new JButton("KEMBALI");
        btnKembali.setFont(new Font("Arial", Font.BOLD, 18));
        btnKembali.setBackground(new Color(255, 200, 210));
        btnKembali.setFocusPainted(false);
        btnKembali.addActionListener(_ -> {
            if (lastMoodLabel != null) {
                showResult(lastMoodLabel, lastEmosiAsli);
            } else {
                initInputMoodPanel();
            }
        });
        rightPanel.add(btnKembali);
        topPanel.add(rightPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Ambil data dari database
        List<String[]> data = DatabaseHelper.getMoodHistory(currentUser)
            .stream()
            .collect(java.util.stream.Collectors.toList());
        // Urutkan data berdasarkan tanggal terbaru di urutan pertama
        data.sort((a, b) -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                Date dateA = sdf.parse(a[3]);
                Date dateB = sdf.parse(b[3]);
                return dateB.compareTo(dateA); // descending
            } catch (Exception e) {
                return 0;
            }
        });
        System.out.println("Jumlah data: " + data.size());
        for (String[] row : data) {
            System.out.println(Arrays.toString(row));
        }

        // Kolom tabel
        String[] columnNames = {"Mood", "Emosi Asli", "Catatan", "Tanggal"};
        String[][] tableData = data.toArray(new String[0][]);

        JTable table = new JTable(tableData, columnNames);
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.setRowHeight(28);
        table.setOpaque(false);
        ((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        // Ukuran 80% dari panel
        int panelWidth = (int) (getWidth() * 0.8);
        int panelHeight = (int) (getHeight() * 0.8);
        scrollPane.setPreferredSize(new Dimension(panelWidth, panelHeight));
        add(scrollPane, BorderLayout.CENTER);

        // Responsif saat resize
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = (int) (getWidth() * 0.8);
                int h = (int) (getHeight() * 0.8);
                scrollPane.setPreferredSize(new Dimension(w, h));
                revalidate();
            }
        });

        // Filter interaktif
        filterIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        filterIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                String[] options = {"Hari ini", "Kemarin", "Pilih Tanggal..."};
                int choice = JOptionPane.showOptionDialog(
                    null,
                    "Tampilkan riwayat mood:",
                    "Filter Riwayat",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
                );
                String filterTanggal = null;
                if (choice == 0) {
                    filterTanggal = new java.text.SimpleDateFormat("dd-MM-yyyy").format(new java.util.Date());
                } else if (choice == 1) {
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.add(java.util.Calendar.DATE, -1);
                    filterTanggal = new java.text.SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
                } else if (choice == 2) {
                    JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
                    dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd-MM-yyyy"));
                    int result = JOptionPane.showOptionDialog(
                        null, dateSpinner, "Pilih Tanggal", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null
                    );
                    if (result == JOptionPane.OK_OPTION) {
                        java.util.Date selectedDate = (java.util.Date) dateSpinner.getValue();
                        filterTanggal = new java.text.SimpleDateFormat("dd-MM-yyyy").format(selectedDate);
                    }
                }
                if (filterTanggal != null && !filterTanggal.isEmpty()) {
                    showRiwayatMoodTable(filterTanggal);
                }
            }
        });

        revalidate();
        repaint();
    }

    // Ganti pemanggilan showRiwayatMoodChart() menjadi showRiwayatMoodTable()
    private void showRiwayatMoodTable() {
        String today = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        showRiwayatMoodTable(today);
    }

    // Fungsi deteksi emosi dari kata kunci
    private String detectEmotionKeyword(String note) {
        String lower = note.toLowerCase();
        for (String keyword : EMOTION_TO_CATEGORY.keySet()) {
            if (lower.contains(keyword)) {
                return keyword;
            }
        }
        return null;
    }

    // Fungsi bantu kapitalisasi
    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    // Untuk testing mandiri (opsional)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Mood Tracker");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new MoodPanel("tester", "Laki-laki"));
            frame.setSize(1200, 800);
            frame.setVisible(true);
        });
    }
}