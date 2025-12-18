package view;
import javax.swing.JOptionPane;
import controller.KokiController;
import model.Koki;
import model.repository.BahanBakuRepository;
import model.repository.PesananRepository;
import model.BahanBaku;
import model.ItemPesanan;

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
        // Ambil antrian untuk ditampilkan sebagai pilihan, supaya koki tidak perlu
        // menghafal ID secara manual
        java.util.List<ItemPesanan> antrian = controller.getAntrianMasakanList();
        if (antrian.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Tidak ada antrian masakan saat ini.");
            return;
        }

        String[] itemOptions = new String[antrian.size()];
        for (int i = 0; i < antrian.size(); i++) {
            ItemPesanan item = antrian.get(i);
            itemOptions[i] = (i + 1) + ". " + item.getMenu().getNama() + " x" + item.getKuantitas()
                    + " (Status: " + item.getStatusItem() + ", ID: " + item.getIdItemPesanan() + ")";
        }

        String selected = (String) JOptionPane.showInputDialog(
                null,
                "Pilih item yang akan diupdate:",
                "Pilih Item Pesanan",
                JOptionPane.QUESTION_MESSAGE,
                null,
                itemOptions,
                itemOptions[0]);

        if (selected == null) {
            // User cancel pemilihan item
            return;
        }

        // Cari index berdasarkan prefix "n. " di awal string
        int dotIndex = selected.indexOf('.');
        int index = 0;
        try {
            index = Integer.parseInt(selected.substring(0, dotIndex)) - 1;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Pilihan item tidak valid!");
            return;
        }

        if (index < 0 || index >= antrian.size()) {
            JOptionPane.showMessageDialog(null, "Pilihan item tidak valid!");
            return;
        }

        ItemPesanan target = antrian.get(index);

        // Tentukan opsi status yang diperbolehkan berdasarkan status saat ini
        String currentStatus = target.getStatusItem();
        String[] statusOptions;
        if ("Menunggu".equals(currentStatus)) {
            statusOptions = new String[] { "Dimasak" };
        } else if ("Dimasak".equals(currentStatus)) {
            statusOptions = new String[] { "Siap" };
        } else {
            JOptionPane.showMessageDialog(null, "Item sudah berstatus Siap dan tidak dapat diubah lagi.");
            return;
        }

        String status = (String) JOptionPane.showInputDialog(
                null,
                "Status saat ini: " + currentStatus + "\nPilih status baru:",
                "Update Status",
                JOptionPane.QUESTION_MESSAGE,
                null,
                statusOptions,
                statusOptions[0]);

        if (status == null) {
            // User cancel pemilihan status
            return;
        }

        String result = controller.updateStatusMasakan(target.getIdItemPesanan(), status);
        JOptionPane.showMessageDialog(null, result);
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