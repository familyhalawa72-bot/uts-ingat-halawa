package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Barang;

public class BarangDAO {

    // Mengembalikan true jika berhasil disimpan
    public boolean simpan(Barang b) {
        String sql = "INSERT INTO barang (kode_barang, nama_barang, kategori, stok) VALUES (?,?,?,?)";
        try (Connection conn = Koneksi.getKoneksi()) {
            if (conn == null) return false;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, b.getKodeBarang());
            ps.setString(2, b.getNamaBarang());
            ps.setString(3, b.getKategori());
            ps.setInt(4, b.getStok());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Gagal simpan: " + e.getMessage());
            return false;
        }
    }

    // Mengembalikan true jika ada baris yang berubah
    public boolean ubah(Barang b) {
        String sql = "UPDATE barang SET nama_barang=?, kategori=?, stok=? WHERE kode_barang=?";
        try (Connection conn = Koneksi.getKoneksi()) {
            if (conn == null) return false;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, b.getNamaBarang());
            ps.setString(2, b.getKategori());
            ps.setInt(3, b.getStok());
            ps.setString(4, b.getKodeBarang());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Gagal ubah: " + e.getMessage());
            return false;
        }
    }

    public boolean hapus(String kode) {
        String sql = "DELETE FROM barang WHERE kode_barang=?";
        try (Connection conn = Koneksi.getKoneksi()) {
            if (conn == null) return false;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, kode);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Gagal hapus: " + e.getMessage());
            return false;
        }
    }

    public List<Barang> getAll() {
        List<Barang> list = new ArrayList<>();
        String sql = "SELECT * FROM barang ORDER BY kode_barang";
        try (Connection conn = Koneksi.getKoneksi()) {
            if (conn == null) return list;
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                list.add(new Barang(
                    rs.getString("kode_barang"),
                    rs.getString("nama_barang"),
                    rs.getString("kategori"),
                    rs.getInt("stok")));
            }
        } catch (SQLException e) {
            System.out.println("Gagal ambil data: " + e.getMessage());
        }
        return list;
    }
}