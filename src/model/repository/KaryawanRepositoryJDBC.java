package model.repository;

import model.*;
import model.factory.KaryawanFactory;
import util.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// PEMBUKA KODE BERUBAH - Migrasi dari In-Memory ke JDBC
public class KaryawanRepositoryJDBC implements KaryawanRepository {
    
    @Override
    public Karyawan getKaryawanByNik(String nik) {
        String sql = "SELECT * FROM tabel_karyawan WHERE nik = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nik);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToKaryawan(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<Karyawan> getAllKaryawan() {
        List<Karyawan> list = new ArrayList<>();
        String sql = "SELECT * FROM tabel_karyawan ORDER BY nik";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(mapResultSetToKaryawan(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public Karyawan authenticate(String nik, String password) {
        String sql = "SELECT * FROM tabel_karyawan WHERE nik = ? AND password = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nik);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToKaryawan(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public void addAbsensi(String nik, AbsensiKaryawan absensi) {
        String sql = "INSERT INTO tabel_absensi (id_karyawan, tanggal, jam_masuk, jam_pulang) " +
                     "VALUES ((SELECT id_karyawan FROM tabel_karyawan WHERE nik = ?), ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nik);
            ps.setDate(2, Date.valueOf(absensi.getTanggal()));
            ps.setInt(3, absensi.getJamMasuk());
            ps.setInt(4, absensi.getJamPulang());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Helper method untuk mapping ResultSet ke Karyawan
    private Karyawan mapResultSetToKaryawan(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        String nik = rs.getString("nik");
        String nama = rs.getString("nama");
        String alamat = rs.getString("alamat");
        String telepon = rs.getString("telepon");
        String password = rs.getString("password");
        double gajiPokok = rs.getDouble("gaji_pokok");
        
        Karyawan karyawan;
        
        if (role.equals("Kasir")) {
            double rateLembur = rs.getDouble("rate_lembur");
            karyawan = KaryawanFactory.createKaryawan("Kasir", nik, nama, alamat, telepon, password, gajiPokok, rateLembur);
        } else if (role.equals("Koki")) {
            double ratePerMenu = rs.getDouble("rate_per_menu");
            Koki koki = (Koki) KaryawanFactory.createKaryawan("Koki", nik, nama, alamat, telepon, password, gajiPokok, ratePerMenu);
            koki.setJumlahMenuSelesai(rs.getInt("jumlah_menu_selesai"));
            karyawan = koki;
        } else {
            karyawan = KaryawanFactory.createKaryawan("Admin", nik, nama, alamat, telepon, password, gajiPokok, 0);
        }
        
        // Load absensi
        loadAbsensi(karyawan, rs.getInt("id_karyawan"));
        
        return karyawan;
    }
    
    // Load absensi untuk karyawan
    private void loadAbsensi(Karyawan karyawan, int idKaryawan) {
        String sql = "SELECT * FROM tabel_absensi WHERE id_karyawan = ? ORDER BY tanggal DESC";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idKaryawan);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                AbsensiKaryawan abs = new AbsensiKaryawan(
                    String.valueOf(rs.getInt("id_absensi")),
                    rs.getDate("tanggal").toLocalDate(),
                    rs.getInt("jam_masuk"),
                    rs.getInt("jam_pulang")
                );
                karyawan.getListAbsensi().add(abs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Update jumlah menu selesai untuk Koki
    public void updateMenuSelesai(String nik, int jumlah) {
        String sql = "UPDATE tabel_karyawan SET jumlah_menu_selesai = jumlah_menu_selesai + ? WHERE nik = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, jumlah);
            ps.setString(2, nik);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
// PENUTUP KODE BERUBAH