package controller;

import model.*;
import model.repository.*;
import java.util.ArrayList;
import java.util.List;

public class KokiController {
    private PesananRepository pesananRepo;
    private BahanBakuRepository bahanRepo;
    
    public KokiController(PesananRepository pesananRepo, BahanBakuRepository bahanRepo) {
        this.pesananRepo = pesananRepo;
        this.bahanRepo = bahanRepo;
    }
    
    // Fitur 1: Print Antrian Masakan
    public String printAntrianMasakan() {
        // Mengambil antrian dari database (Status Menunggu/Dimasak)
        List<ItemPesanan> antrian = pesananRepo.getAntrianMasakan();
        
        if (antrian.isEmpty()) {
            return "Tidak ada antrian masakan saat ini.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("===== ANTRIAN MASAKAN (FIFO) =====\n\n");
        
        int no = 1;
        for (ItemPesanan item : antrian) {
            sb.append(no++).append(". ID: ").append(item.getIdItemPesanan());
            sb.append(" - ").append(item.getMenu().getNama());
            sb.append(" x").append(item.getKuantitas());
            sb.append(" - Status: ").append(item.getStatusItem());
            sb.append("\n   Waktu: ").append(item.getWaktuDibuat());
            
            if (item.getCatatan() != null && !item.getCatatan().isEmpty()) {
                sb.append("\n   Catatan: ").append(item.getCatatan());
            }
            
            // Tampilkan bahan yang dibutuhkan jika Makanan (Fitur Baru)
            if (item.getMenu() instanceof Makanan) {
                Makanan makanan = (Makanan) item.getMenu();
                if (!makanan.getListBahanDibutuhkan().isEmpty()) {
                    sb.append("\n   Bahan: ");
                    List<String> bahanList = new ArrayList<>();
                    for (BahanMenu bm : makanan.getListBahanDibutuhkan()) {
                        double total = bm.getJumlahDibutuhkan() * item.getKuantitas();
                        bahanList.add(bm.getBahan().getNamaBahan() + " " + total + bm.getBahan().getSatuan());
                    }
                    sb.append(String.join(", ", bahanList));
                }
            }
            
            sb.append("\n\n");
        }
        
        return sb.toString();
    }
    
    // NEW: Print ringkasan antrian (untuk update status)
    public String printRingkasanAntrian() {
        List<ItemPesanan> antrian = pesananRepo.getAntrianMasakan();
        
        if (antrian.isEmpty()) {
            return "Tidak ada antrian saat ini.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Antrian saat ini:\n");
        
        int no = 1;
        for (ItemPesanan item : antrian) {
            sb.append(no++).append(". ").append(item.getIdItemPesanan());
            sb.append(" - ").append(item.getMenu().getNama());
            sb.append(" x").append(item.getKuantitas());
            sb.append(" [").append(item.getStatusItem()).append("]\n");
        }
        
        return sb.toString();
    }
    
    // Fitur 2: Update Status Masakan
    public String updateStatusMasakan(String idItem, String statusBaru) {
        ItemPesanan targetItem = pesananRepo.getItemByKode(idItem);
        
        if (targetItem == null) {
            return "Item dengan ID " + idItem + " tidak ditemukan!";
        }
        
        String statusSekarang = targetItem.getStatusItem();
        
        // Validasi transisi status (Workflow)
        if (statusSekarang.equals("Menunggu") && !statusBaru.equals("Dimasak")) {
            return "Transisi tidak valid! Dari 'Menunggu' hanya bisa ke 'Dimasak'.";
        }
        
        if (statusSekarang.equals("Dimasak") && !statusBaru.equals("Siap")) {
            return "Transisi tidak valid! Dari 'Dimasak' hanya bisa ke 'Siap'.";
        }
        
        if (statusSekarang.equals("Siap")) {
            return "Item sudah dalam status 'Siap', tidak bisa diubah lagi!";
        }
        
        // Update status di database
        pesananRepo.updateItemStatus(idItem, statusBaru);
        
        // JIKA status jadi Siap, kurangi stok bahan (AUTO-DEDUCTION)
        if (statusBaru.equals("Siap") && targetItem.getMenu() instanceof Makanan) {
            Makanan makanan = (Makanan) targetItem.getMenu();
            for (BahanMenu bm : makanan.getListBahanDibutuhkan()) {
                BahanBaku bahan = bahanRepo.getBahanByKode(bm.getBahan().getKodeBahan());
                double pengurangan = bm.getJumlahDibutuhkan() * targetItem.getKuantitas();
                
                // Update stok baru ke database
                bahan.setStok(bahan.getStok() - pengurangan);
                bahanRepo.updateBahan(bahan);
                
                // Opsional: Update Jumlah Menu Selesai Koki (jika mau dilacak)
                // Ini butuh akses ke KaryawanRepository, tapi tidak wajib sekarang.
            }
        }
        
        return "Status berhasil diupdate!\n" + targetItem.getMenu().getNama() +
               " -> " + statusBaru;
    }
    
    // Helper: Get next valid status
    public String getNextValidStatus(String statusSekarang) {
        if (statusSekarang.equals("Menunggu")) return "Dimasak";
        if (statusSekarang.equals("Dimasak")) return "Siap";
        return null;
    }
    
    // Fitur 3: Cek Sisa Bahan
    public String cekSisaBahan(String kodeBahan) {
        BahanBaku bahan = bahanRepo.getBahanByKode(kodeBahan);
        
        if (bahan == null) {
            return "Bahan dengan kode " + kodeBahan + " tidak ditemukan!";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("===== STOK BAHAN BAKU =====\n");
        sb.append("Kode  : ").append(bahan.getKodeBahan()).append("\n");
        sb.append("Nama  : ").append(bahan.getNamaBahan()).append("\n");
        sb.append("Stok  : ").append(bahan.getStok()).append(" ").append(bahan.getSatuan()).append("\n");
        
        if (bahan.getStok() < 10) {
            sb.append("\n⚠️ PERINGATAN: Stok menipis!");
        }
        
        return sb.toString();
    }
    
    public List<BahanBaku> getAllBahan() {
        return bahanRepo.getAllBahan();
    }
}