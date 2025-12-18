package view;
import javax.swing.JOptionPane;
import controller.KasirController;
import model.Kasir;
import model.Menu;
import model.repository.MenuRepository;
import model.repository.PesananRepository;


public class KasirView {
    private Kasir kasir;
    private KasirController controller;
    
    public KasirView(Kasir kasir, MenuRepository menuRepo, PesananRepository pesananRepo) {
        this.kasir = kasir;
        this.controller = new KasirController(menuRepo, pesananRepo, kasir);
        showDashboard();
    }
    
    private void showDashboard() {
        String[] options = {
            "1. Buat Pesanan Baru",
            "2. Tambah Item ke Pesanan",
            "3. Print Tagihan",
            "4. Proses Pembayaran",
            "5. Lihat Status Pesanan",
            "6. Batalkan Item",
            "7. Lihat Menu",
            "8. Lihat Meja Aktif",
            "9. Logout"
        };
        
        while (true) {
            String choice = (String) JOptionPane.showInputDialog(
                null,
                "=== KASIR DASHBOARD ===\nSelamat datang, " + kasir.getNama() + "\n\nPilih Menu:",
                "Kasir Menu",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );
            
            if (choice == null) break;
            
            switch (choice.charAt(0)) {
                case '1': buatPesananBaru(); break;
                case '2': tambahItemPesanan(); break;
                case '3': printTagihan(); break;
                case '4': prosesPembayaran(); break;
                case '5': lihatStatusPesanan(); break;
                case '6': batalkanItem(); break;
                case '7': lihatMenu(); break;
                case '8': lihatMejaAktif(); break;
                case '9':
                    JOptionPane.showMessageDialog(null, "Logout berhasil!");
                    return;
            }
        }
    }
    
    private void buatPesananBaru() {
        String noMejaStr = JOptionPane.showInputDialog("Masukkan No. Meja:");
        if (noMejaStr != null) {
            try {
                int noMeja = Integer.parseInt(noMejaStr);
                String result = controller.buatPesananBaru(noMeja);
                JOptionPane.showMessageDialog(null, result);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Nomor meja tidak valid!");
            }
        }
    }
    
    private void tambahItemPesanan() {
        String noMejaStr = JOptionPane.showInputDialog("No. Meja:");
        String kodeMenu = JOptionPane.showInputDialog("Kode Menu:");
        String qtyStr = JOptionPane.showInputDialog("Jumlah:");
        String catatan = JOptionPane.showInputDialog("Catatan (optional):");
        
        if (noMejaStr != null && kodeMenu != null && qtyStr != null) {
            try {
                int noMeja = Integer.parseInt(noMejaStr);
                int qty = Integer.parseInt(qtyStr);
                String result = controller.tambahItemPesanan(noMeja, kodeMenu, qty, catatan == null ? "" : catatan);
                JOptionPane.showMessageDialog(null, result);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Input tidak valid!");
            }
        }
    }
    
    private void printTagihan() {
        String noMejaStr = JOptionPane.showInputDialog("No. Meja:");
        if (noMejaStr != null) {
            try {
                int noMeja = Integer.parseInt(noMejaStr);
                String result = controller.printTagihan(noMeja);
                JOptionPane.showMessageDialog(null, result, "Tagihan", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Nomor meja tidak valid!");
            }
        }
    }
    
    private void prosesPembayaran() {
        String noMejaStr = JOptionPane.showInputDialog("No. Meja:");
        String uangStr = JOptionPane.showInputDialog("Uang Tunai:");
        
        if (noMejaStr != null && uangStr != null) {
            try {
                int noMeja = Integer.parseInt(noMejaStr);
                double uang = Double.parseDouble(uangStr);
                String result = controller.prosesPembayaran(noMeja, uang);
                JOptionPane.showMessageDialog(null, result, "Pembayaran", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Input tidak valid!");
            }
        }
    }
    
    private void lihatStatusPesanan() {
        String noMejaStr = JOptionPane.showInputDialog("No. Meja:");
        if (noMejaStr != null) {
            try {
                int noMeja = Integer.parseInt(noMejaStr);
                String result = controller.lihatStatusPesanan(noMeja);
                JOptionPane.showMessageDialog(null, result, "Status Pesanan", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Nomor meja tidak valid!");
            }
        }
    }
    
    private void batalkanItem() {
        String noMejaStr = JOptionPane.showInputDialog("No. Meja:");
        String kodeMenu = JOptionPane.showInputDialog("Kode Menu yang akan dibatalkan:");
        
        if (noMejaStr != null && kodeMenu != null) {
            try {
                int noMeja = Integer.parseInt(noMejaStr);
                String result = controller.batalkanItem(noMeja, kodeMenu);
                JOptionPane.showMessageDialog(null, result);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Nomor meja tidak valid!");
            }
        }
    }
    
    private void lihatMenu() {
        StringBuilder sb = new StringBuilder("=== DAFTAR MENU AKTIF ===\n\n");
        for (Menu m : controller.getMenuAktif()) {
            sb.append(m.getKodeMenu()).append(" - ").append(m.getNama());
            sb.append("\n  Rp ").append(String.format("%,d", (int)m.getHarga()));
            sb.append(" (").append(m.getKategori()).append(")");
            sb.append("\n\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Menu", JOptionPane.INFORMATION_MESSAGE);
    }

    private void lihatMejaAktif() {
        String result = controller.getDaftarMejaAktif();
        JOptionPane.showMessageDialog(null, result, "Meja Aktif", JOptionPane.INFORMATION_MESSAGE);
    }
}