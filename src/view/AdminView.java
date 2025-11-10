package view;

import controller.AdminController;
import model.Admin;
import model.BahanBaku;
import model.Menu;
import model.repository.BahanBakuRepository;
import model.repository.KaryawanRepository;
import model.repository.MenuRepository;
import model.repository.PesananRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.swing.JOptionPane;

public class AdminView {
    private Admin admin;
    private AdminController controller;

    public AdminView(Admin admin, KaryawanRepository karyawanRepo, MenuRepository menuRepo,
            PesananRepository pesananRepo, BahanBakuRepository bahanRepo) {
        this.admin = admin;
        this.controller = new AdminController(karyawanRepo, menuRepo, pesananRepo, bahanRepo);
        showDashboard();
    }

    private void showDashboard() {
        String[] options = {
                "1. Print Data Karyawan",
                "2. CRUD Menu",
                "3. Print Laporan Pendapatan",
                "4. Print Menu Terlaris",
                "5. CRUD Stok Bahan Baku",
                "6. Print Gaji Karyawan",
                "7. Logout"
        };

        while (true) {
            String choice = (String) JOptionPane.showInputDialog(
                    null,
                    "=== ADMIN DASHBOARD ===\nSelamat datang, " + admin.getNama() + "\n\nPilih Menu:",
                    "Admin Menu",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (choice == null)
                break;

            switch (choice.charAt(0)) {
                case '1':
                    printDataKaryawan();
                    break;
                case '2':
                    crudMenu();
                    break;
                case '3':
                    printLaporanPendapatan();
                    break;
                case '4':
                    printMenuTerlaris();
                    break;
                case '5':
                    crudStokBahan();
                    break;
                case '6':
                    printGajiKaryawan();
                    break;
                case '7':
                    JOptionPane.showMessageDialog(null, "Logout berhasil!");
                    return;
            }
        }
    }

    private void printDataKaryawan() {
        String nik = JOptionPane.showInputDialog("Masukkan NIK Karyawan:");
        if (nik != null && !nik.trim().isEmpty()) {
            String result = controller.printDataKaryawan(nik);
            JOptionPane.showMessageDialog(null, result, "Data Karyawan", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void crudMenu() {
        String[] options = { "Lihat Semua Menu", "Tambah Menu", "Update Menu", "Nonaktifkan Menu", "Kembali" };
        String choice = (String) JOptionPane.showInputDialog(null, "CRUD Menu:", "Menu Management",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == null || choice.equals("Kembali"))
            return;

        switch (choice) {
            case "Lihat Semua Menu":
                StringBuilder sb = new StringBuilder("=== DAFTAR MENU ===\n\n");
                for (Menu m : controller.getAllMenu()) {
                    sb.append(m.getKodeMenu()).append(" - ").append(m.getNama());
                    sb.append(" - Rp ").append(String.format("%,d", (int) m.getHarga()));
                    sb.append(" (").append(m.getKategori()).append(")");
                    sb.append(m.isAktif() ? " [AKTIF]" : " [NONAKTIF]");
                    sb.append("\n");
                }
                JOptionPane.showMessageDialog(null, sb.toString());
                break;

            case "Tambah Menu":
                String kategori = JOptionPane.showInputDialog("Kategori (Makanan/Minuman):");
                String kode = JOptionPane.showInputDialog("Kode Menu:");
                String nama = JOptionPane.showInputDialog("Nama Menu:");
                String hargaStr = JOptionPane.showInputDialog("Harga:");
                String extra = "";
                if (kategori != null && kategori.equalsIgnoreCase("minuman")) {
                    extra = JOptionPane.showInputDialog("Opsi Ukuran:");
                }

                if (kode != null && nama != null && hargaStr != null) {
                    try {
                        double harga = Double.parseDouble(hargaStr);
                        String result = controller.addMenu(kategori, kode, nama, harga, extra);
                        JOptionPane.showMessageDialog(null, result);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Harga tidak valid!");
                    }
                }
                break;

            case "Update Menu":
                String kodeUpdate = JOptionPane.showInputDialog("Kode Menu yang akan diupdate:");
                String namaBaru = JOptionPane.showInputDialog("Nama Baru:");
                String hargaBaruStr = JOptionPane.showInputDialog("Harga Baru:");

                if (kodeUpdate != null && namaBaru != null && hargaBaruStr != null) {
                    try {
                        double hargaBaru = Double.parseDouble(hargaBaruStr);
                        String result = controller.updateMenu(kodeUpdate, namaBaru, hargaBaru);
                        JOptionPane.showMessageDialog(null, result);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Harga tidak valid!");
                    }
                }
                break;

            case "Nonaktifkan Menu":
                String kodeDelete = JOptionPane.showInputDialog("Kode Menu yang akan dinonaktifkan:");
                if (kodeDelete != null) {
                    String result = controller.deleteMenu(kodeDelete);
                    JOptionPane.showMessageDialog(null, result);
                }
                break;
        }
    }

    private void printLaporanPendapatan() {
        try {
            String startStr = JOptionPane.showInputDialog("Tanggal Mulai (yyyy-MM-dd):");
            String endStr = JOptionPane.showInputDialog("Tanggal Akhir (yyyy-MM-dd):");

            if (startStr != null && endStr != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate start = LocalDate.parse(startStr, formatter);
                LocalDate end = LocalDate.parse(endStr, formatter);

                String result = controller.printLaporanPendapatan(start, end);
                JOptionPane.showMessageDialog(null, result, "Laporan Pendapatan", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(null, "Format tanggal salah! Gunakan yyyy-MM-dd");
        }
    }

    private void printMenuTerlaris() {
        try {
            String startStr = JOptionPane.showInputDialog("Tanggal Mulai (yyyy-MM-dd):");
            String endStr = JOptionPane.showInputDialog("Tanggal Akhir (yyyy-MM-dd):");

            if (startStr != null && endStr != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate start = LocalDate.parse(startStr, formatter);
                LocalDate end = LocalDate.parse(endStr, formatter);

                String result = controller.printMenuTerlaris(start, end);
                JOptionPane.showMessageDialog(null, result, "Menu Terlaris", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(null, "Format tanggal salah! Gunakan yyyy-MM-dd");
        }
    }

    private void crudStokBahan() {
        String[] options = { "Lihat Semua Bahan", "Tambah Bahan", "Update Stok", "Hapus Bahan", "Kembali" };
        String choice = (String) JOptionPane.showInputDialog(null, "CRUD Stok Bahan:", "Bahan Baku Management",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == null || choice.equals("Kembali"))
            return;

        switch (choice) {
            case "Lihat Semua Bahan":
                StringBuilder sb = new StringBuilder("=== STOK BAHAN BAKU ===\n\n");
                for (BahanBaku b : controller.getAllBahan()) {
                    sb.append(b.getKodeBahan()).append(" - ").append(b.getNamaBahan());
                    sb.append(" - ").append(b.getStok()).append(" ").append(b.getSatuan());
                    sb.append("\n");
                }
                JOptionPane.showMessageDialog(null, sb.toString());
                break;

            case "Tambah Bahan":
                String kode = JOptionPane.showInputDialog("Kode Bahan:");
                String nama = JOptionPane.showInputDialog("Nama Bahan:");
                String stokStr = JOptionPane.showInputDialog("Stok:");
                String satuan = JOptionPane.showInputDialog("Satuan:");

                if (kode != null && nama != null && stokStr != null && satuan != null) {
                    try {
                        double stok = Double.parseDouble(stokStr);
                        String result = controller.addBahan(kode, nama, stok, satuan);
                        JOptionPane.showMessageDialog(null, result);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Stok tidak valid!");
                    }
                }
                break;

            case "Update Stok":
                String kodeUpdate = JOptionPane.showInputDialog("Kode Bahan:");
                String stokBaruStr = JOptionPane.showInputDialog("Stok Baru:");

                if (kodeUpdate != null && stokBaruStr != null) {
                    try {
                        double stokBaru = Double.parseDouble(stokBaruStr);
                        String result = controller.updateBahan(kodeUpdate, stokBaru);
                        JOptionPane.showMessageDialog(null, result);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Stok tidak valid!");
                    }
                }
                break;

            case "Hapus Bahan":
                String kodeDelete = JOptionPane.showInputDialog("Kode Bahan yang akan dihapus:");
                if (kodeDelete != null) {
                    String result = controller.deleteBahan(kodeDelete);
                    JOptionPane.showMessageDialog(null, result);
                }
                break;
        }
    }

    private void printGajiKaryawan() {
        String nik = JOptionPane.showInputDialog("Masukkan NIK Karyawan:");
        if (nik != null && !nik.trim().isEmpty()) {
            String result = controller.printGajiKaryawan(nik);
            JOptionPane.showMessageDialog(null, result, "Perhitungan Gaji", JOptionPane.INFORMATION_MESSAGE);
        }
    }

}
