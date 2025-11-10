package view;
import javax.swing.JOptionPane;
import controller.KokiController;
import model.Koki;
import model.repository.BahanBakuRepository;
import model.repository.PesananRepository;
import model.BahanBaku;

public class KokiView {
    private Koki koki;
    private KokiController controller;
    
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
                    return;
            }
        }
    }
    
    private void printAntrianMasakan() {
        String result = controller.printAntrianMasakan();
        JOptionPane.showMessageDialog(null, result, "Antrian Masakan", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateStatusMasakan() {
        String idItem = JOptionPane.showInputDialog("ID Item Pesanan:");
        String[] statusOptions = {"Menunggu", "Dimasak", "Siap"};
        String status = (String) JOptionPane.showInputDialog(null, "Pilih Status Baru:", "Update Status",
            JOptionPane.QUESTION_MESSAGE, null, statusOptions, statusOptions[1]);
        
        if (idItem != null && status != null) {
            String result = controller.updateStatusMasakan(idItem, status);
            JOptionPane.showMessageDialog(null, result);
        }
    }
    
    private void cekSisaBahan() {
        String kode = JOptionPane.showInputDialog("Kode Bahan:");
        if (kode != null) {
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