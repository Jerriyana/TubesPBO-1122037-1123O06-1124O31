package controller;

import model.ItemPesanan;
import model.Pesanan;
import model.BahanBaku;
import model.repository.PesananRepository;
import model.repository.BahanBakuRepository;
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
        List<ItemPesanan> antrian = pesananRepo.getAntrianMasakan();

        if (antrian.isEmpty()) {
            return "Tidak ada antrian masakan saat ini.";
        }

        String hasil = "===== ANTRIAN MASAKAN (FIFO) =====\n\n";
        int no = 1;

        for (ItemPesanan item : antrian) {
            hasil += no++ + ". ID: " + item.getIdItemPesanan();
            hasil += " - " + item.getMenu().getNama();
            hasil += " x" + item.getKuantitas();
            hasil += " - Status: " + item.getStatusItem();
            hasil += "\n   Waktu: " + item.getWaktuDibuat();

            if (item.getCatatan() != null && !item.getCatatan().isEmpty()) {
                hasil += "\n   Catatan: " + item.getCatatan();
            }

            hasil += "\n\n";
        }

        return hasil;
    }

    /**
     * Mengembalikan list antrian masakan untuk kebutuhan view (misalnya dropdown
     * pemilihan item).
     */
    public List<ItemPesanan> getAntrianMasakanList() {
        return pesananRepo.getAntrianMasakan();
    }

    // Fitur 2: Update Status Masakan
    public String updateStatusMasakan(String idItem, String statusBaru) {
        List<Pesanan> allPesanan = pesananRepo.getAllPesanan();

        ItemPesanan targetItem = null;
        Pesanan targetPesanan = null;

        for (Pesanan p : allPesanan) {
            for (ItemPesanan item : p.getListPesananItem()) {
                if (item.getIdItemPesanan().equals(idItem)) {
                    targetItem = item;
                    targetPesanan = p;
                    break;
                }
            }
            if (targetItem != null)
                break;
        }

        if (targetItem == null) {
            return "Item dengan ID " + idItem + " tidak ditemukan!";
        }

        String currentStatus = targetItem.getStatusItem();

        // Jika sudah siap, tidak boleh diubah lagi
        if ("Siap".equals(currentStatus)) {
            return "Item sudah berstatus Siap dan tidak dapat diubah lagi.";
        }

        String[] validStatus = { "Menunggu", "Dimasak", "Siap" };
        boolean valid = false;
        for (String s : validStatus) {
            if (s.equalsIgnoreCase(statusBaru)) {
                statusBaru = s;
                valid = true;
                break;
            }
        }

        if (!valid) {
            return "Status tidak valid! Gunakan: Menunggu, Dimasak, atau Siap";
        }

        // Aturan transisi:
        // Menunggu -> Dimasak
        // Dimasak -> Siap
        // Tidak boleh kembali ke Menunggu dan tidak boleh lompat Menunggu -> Siap
        if ("Menunggu".equals(currentStatus) && "Siap".equals(statusBaru)) {
            return "Transisi tidak valid! Dari Menunggu hanya boleh ke Dimasak.";
        }
        if ("Dimasak".equals(currentStatus) && "Menunggu".equals(statusBaru)) {
            return "Transisi tidak valid! Dari Dimasak hanya boleh ke Siap.";
        }
        if (currentStatus.equals(statusBaru)) {
            return "Status sudah " + currentStatus + ".";
        }

        targetItem.setStatusItem(statusBaru);
        pesananRepo.updatePesanan(targetPesanan);

        return "Status berhasil diupdate!\n" + targetItem.getMenu().getNama() +
                " -> " + statusBaru;
    }

    // Fitur 3: Cek Sisa Bahan
    public String cekSisaBahan(String kodeBahan) {
        BahanBaku bahan = bahanRepo.getBahanByKode(kodeBahan);

        if (bahan == null) {
            return "Bahan dengan kode " + kodeBahan + " tidak ditemukan!";
        }

        String hasil = "===== STOK BAHAN BAKU =====\n";
        hasil += "Kode  : " + bahan.getKodeBahan() + "\n";
        hasil += "Nama  : " + bahan.getNamaBahan() + "\n";
        hasil += "Stok  : " + bahan.getStok() + " " + bahan.getSatuan() + "\n";

        if (bahan.getStok() < 10) {
            hasil += "\n⚠️ PERINGATAN: Stok menipis!";
        }

        return hasil;
    }

    public List<BahanBaku> getAllBahan() {
        return bahanRepo.getAllBahan();
    }
}