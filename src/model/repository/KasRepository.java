package model.repository;

import util.DatabaseManager;
import java.sql.*;

// PEMBUKA KODE BERUBAH - Repository baru untuk manajemen kas
public class KasRepository {
    
    public double getSaldo() {
        String sql = "SELECT saldo FROM tabel_kas ORDER BY id_kas DESC LIMIT 1";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble("saldo");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public void updateSaldo(double saldoBaru) {
        String sql = "UPDATE tabel_kas SET saldo = ?, last_updated = CURRENT_TIMESTAMP WHERE id_kas = (SELECT id_kas FROM tabel_kas ORDER BY id_kas DESC LIMIT 1)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDouble(1, saldoBaru);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean cekKembalianCukup(double kembalian) {
        return getSaldo() >= kembalian;
    }
    
    public void tambahUangMasuk(double totalBayar, double kembalian) {
        double saldoSekarang = getSaldo();
        double saldoBaru = saldoSekarang + totalBayar - kembalian;
        updateSaldo(saldoBaru);
    }
}
// PENUTUP KODE BERUBAH