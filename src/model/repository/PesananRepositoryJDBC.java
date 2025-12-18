package model.repository;

import model.*;
import util.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// PEMBUKA KODE BERUBAH - Migrasi dari In-Memory ke JDBC
public class PesananRepositoryJDBC implements PesananRepository {
    private KaryawanRepository karyawanRepo;
    private MenuRepository menuRepo;
    
    public PesananRepositoryJDBC(KaryawanRepository karyawanRepo, MenuRepository menuRepo) {
        this.karyawanRepo = karyawanRepo;
        this.menuRepo = menuRepo;
    }
    
    @Override
    public Pesanan getPesananByMeja(int noMeja) {
        String sql = "SELECT * FROM tabel_pesanan WHERE no_meja = ? AND status_pesanan = 'Aktif'";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, noMeja);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPesanan(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<Pesanan> getAllPesanan() {
        List<Pesanan> list = new ArrayList<>();
        String sql = "SELECT * FROM tabel_pesanan ORDER BY waktu_pesan DESC";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(mapResultSetToPesanan(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<Pesanan> getPesananByPeriod(LocalDate startDate, LocalDate endDate) {
        List<Pesanan> list = new ArrayList<>();
        String sql = "SELECT * FROM tabel_pesanan WHERE waktu_pesan BETWEEN ? AND ? ORDER BY waktu_pesan DESC";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setTimestamp(1, Timestamp.valueOf(startDate.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(endDate.plusDays(1).atStartOfDay()));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                list.add(mapResultSetToPesanan(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public void addPesanan(Pesanan pesanan) {
        String sql = "INSERT INTO tabel_pesanan (kode_pesanan, id_karyawan, no_meja, waktu_pesan, status_pesanan, pajak) " +
                     "VALUES (?, (SELECT id_karyawan FROM tabel_karyawan WHERE nik = ?), ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, pesanan.getIdPesanan());
            ps.setString(2, pesanan.getKasir().getNik());
            ps.setInt(3, pesanan.getNoMeja());
            ps.setTimestamp(4, Timestamp.valueOf(pesanan.getWaktuPesan()));
            ps.setString(5, pesanan.getStatusPesanan());
            ps.setDouble(6, pesanan.getPajak());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void updatePesanan(Pesanan pesanan) {
        String sql = "UPDATE tabel_pesanan SET status_pesanan = ?, total_bayar = ? WHERE kode_pesanan = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, pesanan.getStatusPesanan());
            ps.setDouble(2, pesanan.hitungTotal());
            ps.setString(3, pesanan.getIdPesanan());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public List<ItemPesanan> getAntrianMasakan() {
        List<ItemPesanan> list = new ArrayList<>();
        String sql = "SELECT ip.* FROM tabel_item_pesanan ip " +
                     "JOIN tabel_pesanan p ON ip.id_pesanan = p.id_pesanan " +
                     "WHERE p.status_pesanan = 'Aktif' AND ip.status_item IN ('Menunggu', 'Dimasak') " +
                     "ORDER BY ip.waktu_dibuat ASC";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(mapResultSetToItemPesanan(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // Helper methods
    private Pesanan mapResultSetToPesanan(ResultSet rs) throws SQLException {
        String kodePesanan = rs.getString("kode_pesanan");
        int noMeja = rs.getInt("no_meja");
        int idKaryawan = rs.getInt("id_karyawan");
        
        // Get kasir by id
        Kasir kasir = getKasirById(idKaryawan);
        
        Pesanan pesanan = new Pesanan(kodePesanan, noMeja, kasir);
        pesanan.setWaktuPesan(rs.getTimestamp("waktu_pesan").toLocalDateTime());
        pesanan.setStatusPesanan(rs.getString("status_pesanan"));
        pesanan.setPajak(rs.getDouble("pajak"));
        
        // Load items
        loadItemPesanan(pesanan, rs.getInt("id_pesanan"));
        
        return pesanan;
    }
    
    private Kasir getKasirById(int idKaryawan) {
        String sql = "SELECT nik FROM tabel_karyawan WHERE id_karyawan = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idKaryawan);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return (Kasir) karyawanRepo.getKaryawanByNik(rs.getString("nik"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private void loadItemPesanan(Pesanan pesanan, int idPesanan) {
        String sql = "SELECT * FROM tabel_item_pesanan WHERE id_pesanan = ? ORDER BY waktu_dibuat";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idPesanan);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                pesanan.getListPesananItem().add(mapResultSetToItemPesanan(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private ItemPesanan mapResultSetToItemPesanan(ResultSet rs) throws SQLException {
        String kodeItem = rs.getString("kode_item");
        int idMenu = rs.getInt("id_menu");
        Menu menu = getMenuById(idMenu);
        int kuantitas = rs.getInt("kuantitas");
        String catatan = rs.getString("catatan");
        
        ItemPesanan item = new ItemPesanan(kodeItem, menu, kuantitas, catatan);
        item.setStatusItem(rs.getString("status_item"));
        item.setWaktuDibuat(rs.getTimestamp("waktu_dibuat").toLocalDateTime());
        
        return item;
    }
    
    private Menu getMenuById(int idMenu) {
        String sql = "SELECT kode_menu FROM tabel_menu WHERE id_menu = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idMenu);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return menuRepo.getMenuByKode(rs.getString("kode_menu"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Add item pesanan
    public void addItemPesanan(String kodePesanan, ItemPesanan item) {
        String sql = "INSERT INTO tabel_item_pesanan (kode_item, id_pesanan, id_menu, kuantitas, catatan, status_item, waktu_dibuat) " +
                     "VALUES (?, (SELECT id_pesanan FROM tabel_pesanan WHERE kode_pesanan = ?), " +
                     "(SELECT id_menu FROM tabel_menu WHERE kode_menu = ?), ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, item.getIdItemPesanan());
            ps.setString(2, kodePesanan);
            ps.setString(3, item.getMenu().getKodeMenu());
            ps.setInt(4, item.getKuantitas());
            ps.setString(5, item.getCatatan());
            ps.setString(6, item.getStatusItem());
            ps.setTimestamp(7, Timestamp.valueOf(item.getWaktuDibuat()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Update item status
    public void updateItemStatus(String kodeItem, String statusBaru) {
        String sql = "UPDATE tabel_item_pesanan SET status_item = ? WHERE kode_item = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, statusBaru);
            ps.setString(2, kodeItem);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Get item by kode
    public ItemPesanan getItemByKode(String kodeItem) {
        String sql = "SELECT * FROM tabel_item_pesanan WHERE kode_item = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, kodeItem);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToItemPesanan(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Delete item pesanan
    public void deleteItemPesanan(String kodeItem) {
        String sql = "DELETE FROM tabel_item_pesanan WHERE kode_item = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, kodeItem);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Generate kode pesanan
    public String generateKodePesanan() {
        String sql = "SELECT COUNT(*) + 1 as next_num FROM tabel_pesanan";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return "ORD" + String.format("%03d", rs.getInt("next_num"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "ORD001";
    }
    
    // Generate kode item
    public String generateKodeItem() {
        String sql = "SELECT COUNT(*) + 1 as next_num FROM tabel_item_pesanan";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return "ITEM" + String.format("%03d", rs.getInt("next_num"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "ITEM001";
    }
}
// PENUTUP KODE BERUBAH