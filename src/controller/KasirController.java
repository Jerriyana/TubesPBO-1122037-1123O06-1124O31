package controller;
import model.Menu;
import model.Pesanan;
import model.Kasir;
import model.ItemPesanan;
import model.repository.MenuRepository;
import model.repository.PesananRepository;
import model.repository.PesananRepositoryImpl;
import model.repository.MenuRepositoryImpl;
import java.util.List;

public class KasirController {
    private MenuRepository menuRepo;
    private PesananRepository pesananRepo;
    private Kasir kasirLogin;
    private int pesananCounter = 1;
    
    public KasirController(MenuRepository menuRepo, PesananRepository pesananRepo, Kasir kasirLogin) {
        this.menuRepo = menuRepo;
        this.pesananRepo = pesananRepo;
        this.kasirLogin = kasirLogin;
    }
    
    // Fitur 1: Buat Pesanan Baru
    public String buatPesananBaru(int noMeja) {
        Pesanan existing = pesananRepo.getPesananByMeja(noMeja);
        if (existing != null) {
            return "Meja " + noMeja + " sudah memiliki pesanan aktif!";
        }
        
        String idPesanan = "ORD" + String.format("%03d", pesananCounter++);
        Pesanan pesanan = new Pesanan(idPesanan, noMeja, kasirLogin);
        pesananRepo.addPesanan(pesanan);
        
        return "Pesanan baru untuk Meja " + noMeja + " berhasil dibuat!\nID Pesanan: " + idPesanan;
    }
    
    public String tambahItemPesanan(int noMeja, String kodeMenu, int qty, String catatan) {
        Pesanan pesanan = pesananRepo.getPesananByMeja(noMeja);
        if (pesanan == null) {
            return "Tidak ada pesanan aktif untuk meja " + noMeja + "!\nBuat pesanan baru terlebih dahulu.";
        }
        
        Menu menu = menuRepo.getMenuByKode(kodeMenu);
        if (menu == null) {
            return "Menu dengan kode " + kodeMenu + " tidak ditemukan!";
        }
        
        if (!menu.isAktif()) {
            return "Menu " + menu.getNama() + " sedang tidak tersedia!";
        }
        
        String itemId = ((PesananRepositoryImpl)pesananRepo).generateItemId();
        ItemPesanan item = new ItemPesanan(itemId, menu, qty, catatan);
        pesanan.getListPesananItem().add(item);
        
        return "Item berhasil ditambahkan!\n" + menu.getNama() + " x" + qty;
    }
    
    // Fitur 2: Print Tagihan/Bill
    public String printTagihan(int noMeja) {
        Pesanan pesanan = pesananRepo.getPesananByMeja(noMeja);
        if (pesanan == null) {
            return "Tidak ada pesanan aktif untuk meja " + noMeja + "!";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("========== TAGIHAN ==========\n");
        sb.append("ID Pesanan: ").append(pesanan.getIdPesanan()).append("\n");
        sb.append("No. Meja  : ").append(pesanan.getNoMeja()).append("\n");
        sb.append("Kasir     : ").append(pesanan.getKasir().getNama()).append("\n");
        sb.append("Waktu     : ").append(pesanan.getWaktuPesan()).append("\n");
        sb.append("=============================\n\n");
        
        for (ItemPesanan item : pesanan.getListPesananItem()) {
            sb.append(item.getMenu().getNama());
            sb.append(" x").append(item.getKuantitas());
            sb.append(" @ Rp ").append(String.format("%,d", (int)item.getMenu().getHarga()));
            sb.append(" = Rp ").append(String.format("%,d", (int)(item.getMenu().getHarga() * item.getKuantitas())));
            sb.append("\n");
            if (item.getCatatan() != null && !item.getCatatan().isEmpty()) {
                sb.append("  Catatan: ").append(item.getCatatan()).append("\n");
            }
        }
        
        sb.append("\n-----------------------------\n");
        sb.append("Subtotal : Rp ").append(String.format("%,d", (int)pesanan.hitungSubtotal())).append("\n");
        sb.append("Pajak 10%: Rp ").append(String.format("%,d", (int)pesanan.hitungPajak())).append("\n");
        sb.append("=============================\n");
        sb.append("TOTAL    : Rp ").append(String.format("%,d", (int)pesanan.hitungTotal())).append("\n");
        sb.append("=============================\n");
        
        return sb.toString();
    }
    
    // Fitur 3: Proses Pembayaran
    public String prosesPembayaran(int noMeja, double uangTunai) {
        Pesanan pesanan = pesananRepo.getPesananByMeja(noMeja);
        if (pesanan == null) {
            return "Tidak ada pesanan aktif untuk meja " + noMeja + "!";
        }

        // Cegah pembayaran jika masih ada item yang belum berstatus "Siap"
        boolean semuaSiap = true;
        for (ItemPesanan item : pesanan.getListPesananItem()) {
            if (!"Siap".equals(item.getStatusItem())) {
                semuaSiap = false;
                break;
            }
        }
        if (!semuaSiap) {
            return "Masih ada item yang belum berstatus SIAP.\n"
                    + "Silakan selesaikan proses di dapur terlebih dahulu sebelum melakukan pembayaran.";
        }
        
        double total = pesanan.hitungTotal();
        
        if (uangTunai < total) {
            return "Uang tidak cukup!\nTotal: Rp " + String.format("%,d", (int)total) + 
                   "\nUang Tunai: Rp " + String.format("%,d", (int)uangTunai);
        }
        
        double kembalian = uangTunai - total;
        pesanan.setStatusPesanan("Lunas");
        pesananRepo.updatePesanan(pesanan);
        
        // Update menu terjual
        for (ItemPesanan item : pesanan.getListPesananItem()) {
            ((MenuRepositoryImpl)menuRepo).incrementMenuTerjual(item.getMenu().getKodeMenu(), item.getKuantitas());
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("===== PEMBAYARAN BERHASIL =====\n");
        sb.append("Total     : Rp ").append(String.format("%,d", (int)total)).append("\n");
        sb.append("Uang Tunai: Rp ").append(String.format("%,d", (int)uangTunai)).append("\n");
        sb.append("Kembalian : Rp ").append(String.format("%,d", (int)kembalian)).append("\n");
        sb.append("\nTerima kasih atas kunjungan Anda!");
        
        return sb.toString();
    }
    
    // Fitur 4: Lihat Status Pesanan Meja
    public String lihatStatusPesanan(int noMeja) {
        Pesanan pesanan = pesananRepo.getPesananByMeja(noMeja);
        if (pesanan == null) {
            return "Tidak ada pesanan aktif untuk meja " + noMeja + "!";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("===== STATUS PESANAN MEJA ").append(noMeja).append(" =====\n\n");
        
        for (ItemPesanan item : pesanan.getListPesananItem()) {
            sb.append("• ").append(item.getMenu().getNama());
            sb.append(" x").append(item.getKuantitas());
            sb.append(" - Status: ").append(item.getStatusItem());
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    // Fitur 5: Batalkan Item Pesanan
    public String batalkanItem(int noMeja, String kodeMenu) {
        Pesanan pesanan = pesananRepo.getPesananByMeja(noMeja);
        if (pesanan == null) {
            return "Tidak ada pesanan aktif untuk meja " + noMeja + "!";
        }
        
        ItemPesanan itemToRemove = null;
        for (ItemPesanan item : pesanan.getListPesananItem()) {
            if (item.getMenu().getKodeMenu().equals(kodeMenu)) {
                if (item.getStatusItem().equals("Menunggu")) {
                    itemToRemove = item;
                    break;
                } else {
                    return "Item tidak bisa dibatalkan!\nStatus: " + item.getStatusItem();
                }
            }
        }
        
        if (itemToRemove == null) {
            return "Item dengan kode " + kodeMenu + " tidak ditemukan atau sudah diproses!";
        }
        
        pesanan.getListPesananItem().remove(itemToRemove);
        pesananRepo.updatePesanan(pesanan);
        
        return "Item berhasil dibatalkan!\n" + itemToRemove.getMenu().getNama();
    }
    
    public List<Menu> getMenuAktif() {
        return menuRepo.getMenuAktif();
    }

    // Fitur tambahan: melihat daftar meja dengan pesanan aktif
    public String getDaftarMejaAktif() {
        StringBuilder sb = new StringBuilder("=== DAFTAR MEJA DENGAN PESANAN AKTIF ===\n\n");
        List<Pesanan> semua = pesananRepo.getAllPesanan();
        boolean ada = false;
        for (Pesanan p : semua) {
            if ("Aktif".equals(p.getStatusPesanan())) {
                ada = true;
                sb.append("Meja ").append(p.getNoMeja())
                  .append(" - ID: ").append(p.getIdPesanan())
                  .append(" - Status Bayar: ").append(p.getStatusPesanan())
                  .append("\n");
            }
        }
        if (!ada) {
            sb.append("Belum ada pesanan aktif.\n");
        }
        return sb.toString();
    }
}