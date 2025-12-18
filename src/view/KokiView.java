package view;

import controller.KokiController;
import model.*;
import model.repository.*;
import javax.swing.JOptionPane;

public class KokiView {
    private Koki koki;
    private KokiController controller;
    
    // Gunakan Interface untuk parameter repository
    public KokiView(Koki koki, PesananRepository pesananRepo, BahanBakuRepository bahanRepo) {
        this.koki = koki;
        this.controller = new KokiController(pesananRepo, bahanRepo);
        showDashboard();
    }
    
    private void showDashboard() {
        String[] options = {
            "1. Print Antrian Masakan",
            "2. Update Status Masakan",
            "3. Cek Sisa Bahan",
            "4. Lihat Semua Bahan",
            "5. Logout"
        };
        
        while (true) {
            String choice = (String) JOptionPane.showInputDialog(
                null,
                "=== KOKI DASHBOARD ===\nSelamat datang, " + koki.getNama() + "\n\nPilih Menu:",
                "Koki Menu",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );
            
            if (choice == null) break;
            
            switch (choice.charAt(0)) {
                case '1': printAntrianMasakan(); break;
                case '2': updateStatusMasakan(); break;
                case '3': cekSisaBahan(); break;
                case '4': lihatSemuaBahan(); break;
                case '5': 
                    JOptionPane.showMessageDialog(null, "Logout berhasil!");
                    new LoginView();
                    return;
            }
        }
    }
    
    private void printAntrianMasakan() {
        String result = controller.printAntrianMasakan();
        JOptionPane.showMessageDialog(null, result, "Antrian Masakan", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateStatusMasakan() {
        // Tampilkan ringkasan antrian agar Koki tau ID mana yang mau diproses
        String ringkasan = controller.printRingkasanAntrian();
        if (ringkasan.equals("Tidak ada antrian saat ini.")) {
            JOptionPane.showMessageDialog(null, ringkasan);
            return;
        }
        
        // Input ID dengan teks area (biar bisa liat ringkasan)
        String idItem = JOptionPane.showInputDialog(null, 
            ringkasan + "\n\nMasukkan ID Item Pesanan:", 
            "Update Status", 
            JOptionPane.QUESTION_MESSAGE);
            
        if (idItem == null || idItem.trim().isEmpty()) return;
        
        // REVISI LOGIKA:
        // Kita tidak perlu akses Repo langsung. Tampilkan saja opsi status.
        // Jika Koki memilih status yang salah (misal loncat dari Menunggu ke Siap),
        // Controller akan me-return pesan error "Transisi tidak valid".
        
        String[] statusOptions = {"Dimasak", "Siap"};
        String status = (String) JOptionPane.showInputDialog(null, 
            "Pilih Status Baru untuk " + idItem + ":", 
            "Pilih Status",
            JOptionPane.QUESTION_MESSAGE, null, statusOptions, statusOptions[0]);
            
        if (status != null) {
            // Controller yang melakukan validasi dan eksekusi
            String result = controller.updateStatusMasakan(idItem, status);
            JOptionPane.showMessageDialog(null, result);
        }
    }
    
    private void cekSisaBahan() {
        String kode = JOptionPane.showInputDialog("Kode Bahan:");
        if (kode != null && !kode.trim().isEmpty()) {
            String result = controller.cekSisaBahan(kode);
            JOptionPane.showMessageDialog(null, result, "Stok Bahan", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void lihatSemuaBahan() {
        StringBuilder sb = new StringBuilder("=== DAFTAR BAHAN BAKU ===\n\n");
        for (BahanBaku b : controller.getAllBahan()) {
            sb.append(b.getKodeBahan()).append(" - ").append(b.getNamaBahan());
            sb.append("\n  Stok: ").append(b.getStok()).append(" ").append(b.getSatuan());
            if (b.getStok() < 10) {
                sb.append(" ⚠️ MENIPIS");
            }
            sb.append("\n\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Bahan Baku", JOptionPane.INFORMATION_MESSAGE);
    }
}