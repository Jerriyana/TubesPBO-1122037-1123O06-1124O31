package model.repository;

import model.BahanBaku;
import util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// PEMBUKA KODE BERUBAH - Migrasi dari In-Memory ke JDBC
public class BahanBakuRepositoryJDBC implements BahanBakuRepository {
    
    @Override
    public BahanBaku getBahanByKode(String kode) {
        String sql = "SELECT * FROM tabel_bahan_baku WHERE kode_bahan = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, kode);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new BahanBaku(
                    rs.getString("kode_bahan"),
                    rs.getString("nama_bahan"),
                    rs.getDouble("stok"),
                    rs.getString("satuan")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<BahanBaku> getAllBahan() {
        List<BahanBaku> list = new ArrayList<>();
        String sql = "SELECT * FROM tabel_bahan_baku ORDER BY kode_bahan";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(new BahanBaku(
                    rs.getString("kode_bahan"),
                    rs.getString("nama_bahan"),
                    rs.getDouble("stok"),
                    rs.getString("satuan")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public void addBahan(BahanBaku bahan) {
        String sql = "INSERT INTO tabel_bahan_baku (kode_bahan, nama_bahan, stok, satuan) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, bahan.getKodeBahan());
            ps.setString(2, bahan.getNamaBahan());
            ps.setDouble(3, bahan.getStok());
            ps.setString(4, bahan.getSatuan());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void updateBahan(BahanBaku bahan) {
        String sql = "UPDATE tabel_bahan_baku SET nama_bahan = ?, stok = ?, satuan = ? WHERE kode_bahan = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, bahan.getNamaBahan());
            ps.setDouble(2, bahan.getStok());
            ps.setString(3, bahan.getSatuan());
            ps.setString(4, bahan.getKodeBahan());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void deleteBahan(String kode) {
        String sql = "DELETE FROM tabel_bahan_baku WHERE kode_bahan = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, kode);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
// PENUTUP KODE BERUBAH