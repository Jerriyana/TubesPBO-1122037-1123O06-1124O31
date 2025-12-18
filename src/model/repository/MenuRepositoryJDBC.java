package model.repository;

import model.*;
import model.factory.MenuFactory;
import util.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

// PEMBUKA KODE BERUBAH - Migrasi dari In-Memory ke JDBC
public class MenuRepositoryJDBC implements MenuRepository {
    
    @Override
    public Menu getMenuByKode(String kode) {
        String sql = "SELECT * FROM tabel_menu WHERE kode_menu = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, kode);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToMenu(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<Menu> getAllMenu() {
        List<Menu> list = new ArrayList<>();
        String sql = "SELECT * FROM tabel_menu ORDER BY kode_menu";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(mapResultSetToMenu(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<Menu> getMenuAktif() {
        List<Menu> list = new ArrayList<>();
        String sql = "SELECT * FROM tabel_menu WHERE aktif = TRUE ORDER BY kode_menu";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(mapResultSetToMenu(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public void addMenu(Menu menu) {
        String sql = "INSERT INTO tabel_menu (kode_menu, nama, harga, kategori_menu, opsi_ukuran, aktif) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, menu.getKodeMenu());
            ps.setString(2, menu.getNama());
            ps.setDouble(3, menu.getHarga());
            ps.setString(4, menu.getKategori());
            ps.setString(5, menu instanceof Minuman ? ((Minuman)menu).getOpsiUkuran() : null);
            ps.setBoolean(6, menu.isAktif());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void updateMenu(Menu menu) {
        String sql = "UPDATE tabel_menu SET nama = ?, harga = ?, aktif = ? WHERE kode_menu = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, menu.getNama());
            ps.setDouble(2, menu.getHarga());
            ps.setBoolean(3, menu.isAktif());
            ps.setString(4, menu.getKodeMenu());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void deleteMenu(String kode) {
        String sql = "UPDATE tabel_menu SET aktif = FALSE WHERE kode_menu = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, kode);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Map<Menu, Integer> getMenuTerlaris(LocalDate startDate, LocalDate endDate) {
        Map<Menu, Integer> result = new LinkedHashMap<>();
        
        String sql = "SELECT m.*, COUNT(ip.id_item_pesanan) as jumlah_terjual " +
                     "FROM tabel_menu m " +
                     "JOIN tabel_item_pesanan ip ON m.id_menu = ip.id_menu " +
                     "JOIN tabel_pesanan p ON ip.id_pesanan = p.id_pesanan " +
                     "WHERE p.waktu_pesan BETWEEN ? AND ? AND p.status_pesanan = 'Lunas' " +
                     "GROUP BY m.id_menu, m.kode_menu, m.nama, m.harga, m.kategori_menu, m.opsi_ukuran, m.aktif " +
                     "ORDER BY jumlah_terjual DESC LIMIT 5";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setTimestamp(1, Timestamp.valueOf(startDate.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(endDate.plusDays(1).atStartOfDay()));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Menu menu = mapResultSetToMenu(rs);
                int jumlah = rs.getInt("jumlah_terjual");
                result.put(menu, jumlah);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    // Helper method
    private Menu mapResultSetToMenu(ResultSet rs) throws SQLException {
        String kategori = rs.getString("kategori_menu");
        String kode = rs.getString("kode_menu");
        String nama = rs.getString("nama");
        double harga = rs.getDouble("harga");
        String opsiUkuran = rs.getString("opsi_ukuran");
        boolean aktif = rs.getBoolean("aktif");
        
        Menu menu;
        if (kategori.equals("Makanan")) {
            menu = MenuFactory.createMenu("Makanan", kode, nama, harga, "");
            // Load resep untuk makanan
            loadResep((Makanan) menu, rs.getInt("id_menu"));
        } else {
            menu = MenuFactory.createMenu("Minuman", kode, nama, harga, opsiUkuran);
        }
        
        menu.setAktif(aktif);
        return menu;
    }
    
    // Load resep untuk makanan
    private void loadResep(Makanan makanan, int idMenu) {
        String sql = "SELECT bb.*, r.jumlah_dibutuhkan " +
                     "FROM tabel_resep r " +
                     "JOIN tabel_bahan_baku bb ON r.id_bahan = bb.id_bahan " +
                     "WHERE r.id_menu = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idMenu);
            ResultSet rs = ps.executeQuery();
            
            List<BahanMenu> listBahan = new ArrayList<>();
            while (rs.next()) {
                BahanBaku bahan = new BahanBaku(
                    rs.getString("kode_bahan"),
                    rs.getString("nama_bahan"),
                    rs.getDouble("stok"),
                    rs.getString("satuan")
                );
                double jumlahDibutuhkan = rs.getDouble("jumlah_dibutuhkan");
                listBahan.add(new BahanMenu(bahan, jumlahDibutuhkan));
            }
            makanan.setListBahanDibutuhkan(listBahan);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
// PENUTUP KODE BERUBAH