package view;

import dao.BarangDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Barang;

public class FormBarang extends JFrame {

    private final JTextField txtKode = new JTextField(15);
    private final JTextField txtNama = new JTextField(15);
    private final JTextField txtStok = new JTextField(15);
    private final JComboBox<String> cmbKategori =
        new JComboBox<>(new String[]{"Komputer", "Jaringan", "Alat Ukur"});

    private final JButton btnSimpan = new JButton("Simpan");
    private final JButton btnUbah = new JButton("Ubah");
    private final JButton btnHapus = new JButton("Hapus");
    private final JButton btnBersihkan = new JButton("Bersihkan");

    private final DefaultTableModel tableModel = new DefaultTableModel(
        new Object[]{"Kode", "Nama Barang", "Kategori", "Stok"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tblBarang = new JTable(tableModel);

    private final BarangDAO dao = new BarangDAO();

    public FormBarang() {
        setTitle("Form Data Barang - Inventori");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ===== Panel input =====
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        tambahBaris(panelForm, g, 0, "Kode Barang", txtKode);
        tambahBaris(panelForm, g, 1, "Nama Barang", txtNama);
        tambahBaris(panelForm, g, 2, "Kategori", cmbKategori);
        tambahBaris(panelForm, g, 3, "Stok", txtStok);

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        panelTombol.add(btnSimpan);
        panelTombol.add(btnUbah);
        panelTombol.add(btnHapus);
        panelTombol.add(btnBersihkan);

        JPanel atas = new JPanel(new BorderLayout());
        atas.add(panelForm, BorderLayout.CENTER);
        atas.add(panelTombol, BorderLayout.SOUTH);

        add(atas, BorderLayout.NORTH);
        add(new JScrollPane(tblBarang), BorderLayout.CENTER);

        // ===== Event =====
        btnSimpan.addActionListener(e -> simpan());
        btnUbah.addActionListener(e -> ubah());
        btnHapus.addActionListener(e -> hapus());
        btnBersihkan.addActionListener(e -> bersihkan());

        tblBarang.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                isiFormDariTabel();
            }
        });

        tampilData(); // tampilkan data saat form dibuka

        setSize(560, 480);
        setLocationRelativeTo(null);
    }

    private void tambahBaris(JPanel p, GridBagConstraints g, int baris, String label, JComponent field) {
        g.gridy = baris;
        g.gridx = 0;
        g.weightx = 0;
        p.add(new JLabel(label), g);
        g.gridx = 1;
        g.weightx = 1;
        p.add(field, g);
    }

    // ===== Validasi: mengembalikan objek Barang jika valid, null jika tidak =====
    private Barang validasiInput() {
        String kode = txtKode.getText().trim();
        String nama = txtNama.getText().trim();
        String stokStr = txtStok.getText().trim();

        if (kode.isEmpty() || nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kode dan nama barang tidak boleh kosong!",
                "Validasi", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int stok;
        try {
            stok = Integer.parseInt(stokStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Stok harus berupa angka!",
                "Validasi", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (stok < 0) {
            JOptionPane.showMessageDialog(this, "Stok harus lebih besar atau sama dengan 0!",
                "Validasi", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return new Barang(kode, nama, cmbKategori.getSelectedItem().toString(), stok);
    }

    private void simpan() {
        Barang b = validasiInput();
        if (b == null) return;

        if (dao.simpan(b)) {
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan.");
            tampilData();
            bersihkan();
        } else {
            JOptionPane.showMessageDialog(this,
                "Data gagal disimpan. Kode barang mungkin sudah ada.",
                "Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ubah() {
        Barang b = validasiInput();
        if (b == null) return;

        if (dao.ubah(b)) {
            JOptionPane.showMessageDialog(this, "Data berhasil diubah.");
            tampilData();
            bersihkan();
        } else {
            JOptionPane.showMessageDialog(this,
                "Data gagal diubah. Pastikan kode barang sudah ada.",
                "Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapus() {
        String kode = txtKode.getText().trim();
        if (kode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Isi kode barang atau pilih data di tabel!",
                "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int pilihan = JOptionPane.showConfirmDialog(this,
            "Yakin ingin menghapus barang dengan kode " + kode + "?",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (pilihan != JOptionPane.YES_OPTION) return;

        if (dao.hapus(kode)) {
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
            tampilData();
            bersihkan();
        } else {
            JOptionPane.showMessageDialog(this,
                "Data gagal dihapus. Kode barang tidak ditemukan.",
                "Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tampilData() {
        tableModel.setRowCount(0);
        List<Barang> list = dao.getAll();
        for (Barang b : list) {
            tableModel.addRow(new Object[]{
                b.getKodeBarang(), b.getNamaBarang(), b.getKategori(), b.getStok()});
        }
    }

    private void isiFormDariTabel() {
        int baris = tblBarang.getSelectedRow();
        if (baris < 0) return;
        txtKode.setText(tableModel.getValueAt(baris, 0).toString());
        txtNama.setText(tableModel.getValueAt(baris, 1).toString());
        cmbKategori.setSelectedItem(tableModel.getValueAt(baris, 2).toString());
        txtStok.setText(tableModel.getValueAt(baris, 3).toString());
    }

    private void bersihkan() {
        txtKode.setText("");
        txtNama.setText("");
        txtStok.setText("");
        cmbKategori.setSelectedIndex(0);
        tblBarang.clearSelection();
        txtKode.requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FormBarang().setVisible(true));
    }
}