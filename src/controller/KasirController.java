package controller;

import model.*;
import model.repository.*;
import java.util.List;
import java.util.stream.Collectors;

public class KasirController {
    private MenuRepository menuRepo;
    private PesananRepository pesananRepo;
    private BahanBakuRepository bahanRepo;
    private KasRepository kasRepo;
    private Kasir kasirLogin;
    
    public KasirController(MenuRepository menuRepo, PesananRepository pesananRepo, 
                          BahanBakuRepository bahanRepo, KasRepository kasRepo, 
                          Kasir kasirLogin) {
        this.menuRepo = menuRepo;
        this.pesananRepo = pesananRepo;
        this.bahanRepo = bahanRepo;
        this.kasRepo = kasRepo;
        this.kasirLogin = kasirLogin;
    }
    
    // Fitur 1: Buat Pesanan Baru
    public String buatPesananBaru(int noMeja) {
        Pesanan existing = pesananRepo.getPesananByMeja(noMeja);
        if (existing != null) {
            return "Meja " + noMeja + " sudah memiliki pesanan aktif!";
        }
        
        // Generate ID dari Database
        String idPesanan = pesananRepo.generateKodePesanan();
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
        
        // VALIDASI STOK (Fitur Baru)
        // Hanya cek stok jika menu adalah Makanan (Minuman diasumsikan selalu ada/tidak track bahan)
        if (menu instanceof Makanan) {
            Makanan makanan = (Makanan) menu;
            for (BahanMenu bm : makanan.getListBahanDibutuhkan()) {
                BahanBaku bahan = bahanRepo.getBahanByKode(bm.getBahan().getKodeBahan());
                double kebutuhan = bm.getJumlahDibutuhkan() * qty;
                
                // Cek stok di database
                if (bahan.getStok() < kebutuhan) {
                    return "Stok " + bahan.getNamaBahan() + " tidak cukup!\n" +
                           "Butuh: " + kebutuhan + " " + bahan.getSatuan() + "\n" +
                           "Tersedia: " + bahan.getStok() + " " + bahan.getSatuan();
                }
            }
        }
        
        String itemId = pesananRepo.generateKodeItem();
        ItemPesanan item = new ItemPesanan(itemId, menu, qty, catatan);
        
        // Simpan ke Database
        pesananRepo.addItemPesanan(pesanan.getIdPesanan(), item);
        
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
        
        // VALIDASI: Semua item harus Siap
        for (ItemPesanan item : pesanan.getListPesananItem()) {
            if (!item.getStatusItem().equals("Siap")) {
                return "Tidak bisa bayar! Masih ada pesanan yang belum siap.\n" +
                       item.getMenu().getNama() + " - Status: " + item.getStatusItem();
            }
        }
        
        double total = pesanan.hitungTotal();
        
        if (uangTunai < total) {
            return "Uang tidak cukup!\nTotal: Rp " + String.format("%,d", (int)total) + 
                   "\nUang Tunai: Rp " + String.format("%,d", (int)uangTunai);
        }
        
        double kembalian = uangTunai - total;
        
        // VALIDASI KAS: Cek apakah kas cukup untuk kembalian
        if (!kasRepo.cekKembalianCukup(kembalian)) {
            return "Kas tidak cukup untuk kembalian!\n" +
                   "Kembalian: Rp " + String.format("%,d", (int)kembalian) + "\n" +
                   "Saldo Kas: Rp " + String.format("%,d", (int)kasRepo.getSaldo());
        }
        
        pesanan.setStatusPesanan("Lunas");
        pesananRepo.updatePesanan(pesanan);
        
        // Update saldo kas di Database
        kasRepo.tambahUangMasuk(total, kembalian);
        
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
        
        // Hapus dari database
        pesananRepo.deleteItemPesanan(itemToRemove.getIdItemPesanan());
        
        return "Item berhasil dibatalkan!\n" + itemToRemove.getMenu().getNama();
    }
    
    // Fitur 6 (BARU): Lihat Meja Aktif
    public String lihatMejaAktif() {
        List<Pesanan> allPesanan = pesananRepo.getAllPesanan();
        
        // Filter pakai Stream API
        List<Pesanan> aktif = allPesanan.stream()
            .filter(p -> p.getStatusPesanan().equals("Aktif"))
            .collect(Collectors.toList());
        
        if (aktif.isEmpty()) {
            return "Tidak ada meja dengan pesanan aktif saat ini.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("===== MEJA DENGAN PESANAN AKTIF =====\n\n");
        
        for (Pesanan p : aktif) {
            sb.append("Meja ").append(p.getNoMeja());
            sb.append(" - ").append(p.getIdPesanan());
            sb.append(" - Rp ").append(String.format("%,d", (int)p.hitungTotal()));
            sb.append(" (").append(p.getListPesananItem().size()).append(" item)\n");
        }
        
        return sb.toString();
    }
    
    public List<Menu> getMenuAktif() {
        return menuRepo.getMenuAktif();
    }
}