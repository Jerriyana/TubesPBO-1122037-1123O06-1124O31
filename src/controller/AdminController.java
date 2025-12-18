package controller;

import model.*;
import model.factory.*;
import model.repository.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

// PEMBUKA KODE BERUBAH - Minor updates untuk database support
public class AdminController {
    private KaryawanRepository karyawanRepo;
    private MenuRepository menuRepo;
    private PesananRepository pesananRepo;
    private BahanBakuRepository bahanRepo;
    
    public AdminController(KaryawanRepository karyawanRepo, MenuRepository menuRepo, 
                          PesananRepository pesananRepo, BahanBakuRepository bahanRepo) {
        this.karyawanRepo = karyawanRepo;
        this.menuRepo = menuRepo;
        this.pesananRepo = pesananRepo;
        this.bahanRepo = bahanRepo;
    }
    
    // Fitur 1: Print Data Karyawan
    public String printAllKaryawan() {
        List<Karyawan> listKaryawan = karyawanRepo.getAllKaryawan();
        
        if (listKaryawan.isEmpty()) {
            return "Tidak ada data karyawan!";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("===== DAFTAR SEMUA KARYAWAN =====\n\n");
        
        for (Karyawan k : listKaryawan) {
            sb.append("NIK: ").append(k.getNik()).append(" | ");
            sb.append("Nama: ").append(k.getNama()).append(" | ");
            sb.append("Role: ").append(k.getRole()).append("\n");
        }
        
        return sb.toString();
    }
    
    public String printDataKaryawan(String nik) {
        Karyawan k = karyawanRepo.getKaryawanByNik(nik);
        if (k == null) {
            return "Karyawan dengan NIK " + nik + " tidak ditemukan!";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("===== DATA KARYAWAN =====\n");
        sb.append("NIK      : ").append(k.getNik()).append("\n");
        sb.append("Nama     : ").append(k.getNama()).append("\n");
        sb.append("Alamat   : ").append(k.getAlamat()).append("\n");
        sb.append("Telepon  : ").append(k.getTelepon()).append("\n");
        sb.append("Role     : ").append(k.getRole()).append("\n");
        sb.append("Gaji Pokok: Rp ").append(String.format("%,.0f", k.getGajiPokok())).append("\n");
        
        if (k instanceof Kasir) {
            sb.append("Rate Lembur: Rp ").append(String.format("%,.0f", ((Kasir)k).getRateLembur())).append("/jam\n");
        } else if (k instanceof Koki) {
            sb.append("Rate Per Menu: Rp ").append(String.format("%,.0f", ((Koki)k).getRatePerMenu())).append("\n");
            sb.append("Menu Selesai: ").append(((Koki)k).getJumlahMenuSelesai()).append("\n");
        }
        
        return sb.toString();
    }
    
    // Fitur 2: CRUD Menu
    public List<Menu> getAllMenu() {
        return menuRepo.getAllMenu();
    }
    
    public String addMenu(String kategori, String kodeMenu, String nama, double harga, String extra) {
        try {
            Menu menu = MenuFactory.createMenu(kategori, kodeMenu, nama, harga, extra);
            menuRepo.addMenu(menu);
            return "Menu berhasil ditambahkan!";
        } catch (Exception e) {
            return "Gagal menambah menu: " + e.getMessage();
        }
    }
    
    public String updateMenu(String kodeMenu, String namaBaru, double hargaBaru) {
        Menu menu = menuRepo.getMenuByKode(kodeMenu);
        if (menu == null) {
            return "Menu tidak ditemukan!";
        }
        menu.setNama(namaBaru);
        menu.setHarga(hargaBaru);
        menuRepo.updateMenu(menu);
        return "Menu berhasil diupdate!";
    }
    
    public String deleteMenu(String kodeMenu) {
        menuRepo.deleteMenu(kodeMenu);
        return "Menu berhasil dinonaktifkan!";
    }
    
    // Fitur 3: Print Laporan Pendapatan
    public String printLaporanPendapatan(LocalDate startDate, LocalDate endDate) {
        List<Pesanan> pesananPeriod = pesananRepo.getPesananByPeriod(startDate, endDate);
        
        double totalOmzet = 0;
        int jumlahTransaksi = 0;
        
        for (Pesanan p : pesananPeriod) {
            if (p.getStatusPesanan().equals("Lunas")) {
                totalOmzet += p.hitungTotal();
                jumlahTransaksi++;
            }
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("===== LAPORAN PENDAPATAN =====\n");
        sb.append("Periode: ").append(startDate).append(" s/d ").append(endDate).append("\n");
        sb.append("Jumlah Transaksi: ").append(jumlahTransaksi).append("\n");
        sb.append("Total Omzet: Rp ").append(String.format("%,.0f", totalOmzet)).append("\n");
        
        return sb.toString();
    }
    
    // Fitur 4: Print Menu Terlaris
    public String printMenuTerlaris(LocalDate startDate, LocalDate endDate) {
        Map<Menu, Integer> menuTerlaris = menuRepo.getMenuTerlaris(startDate, endDate);
        
        List<Map.Entry<Menu, Integer>> sortedList = menuTerlaris.entrySet().stream()
            .sorted(Map.Entry.<Menu, Integer>comparingByValue().reversed())
            .limit(5)
            .collect(Collectors.toList());
        
        StringBuilder sb = new StringBuilder();
        sb.append("===== 5 MENU TERLARIS =====\n");
        sb.append("Periode: ").append(startDate).append(" s/d ").append(endDate).append("\n\n");
        
        int rank = 1;
        for (Map.Entry<Menu, Integer> entry : sortedList) {
            sb.append(rank++).append(". ").append(entry.getKey().getNama());
            sb.append(" - Terjual: ").append(entry.getValue()).append(" porsi\n");
        }
        
        return sb.toString();
    }
    
    // Fitur 5: CRUD Stok Bahan Baku
    public List<BahanBaku> getAllBahan() {
        return bahanRepo.getAllBahan();
    }
    
    public String addBahan(String kodeBahan, String namaBahan, double stok, String satuan) {
        BahanBaku bahan = new BahanBaku(kodeBahan, namaBahan, stok, satuan);
        bahanRepo.addBahan(bahan);
        return "Bahan baku berhasil ditambahkan!";
    }
    
    public String updateBahan(String kodeBahan, double stokBaru) {
        BahanBaku bahan = bahanRepo.getBahanByKode(kodeBahan);
        if (bahan == null) {
            return "Bahan baku tidak ditemukan!";
        }
        bahan.setStok(stokBaru);
        bahanRepo.updateBahan(bahan);
        return "Stok bahan baku berhasil diupdate!";
    }
    
    public String deleteBahan(String kodeBahan) {
        bahanRepo.deleteBahan(kodeBahan);
        return "Bahan baku berhasil dihapus!";
    }
    
    // Fitur 6: Print Gaji Karyawan
    public String printGajiKaryawan(String nik) {
        Karyawan k = karyawanRepo.getKaryawanByNik(nik);
        if (k == null) {
            return "Karyawan dengan NIK " + nik + " tidak ditemukan!";
        }
        
        double gaji = k.hitungGaji();
        
        StringBuilder sb = new StringBuilder();
        sb.append("===== PERHITUNGAN GAJI =====\n");
        sb.append("NIK      : ").append(k.getNik()).append("\n");
        sb.append("Nama     : ").append(k.getNama()).append("\n");
        sb.append("Role     : ").append(k.getRole()).append("\n");
        sb.append("Gaji Pokok: Rp ").append(String.format("%,.0f", k.getGajiPokok())).append("\n");
        
        if (k instanceof Kasir) {
            Kasir kasir = (Kasir) k;
            double totalLembur = 0;
            for (AbsensiKaryawan abs : kasir.getListAbsensi()) {
                totalLembur += abs.hitungJamLembur();
            }
            double bonusLembur = totalLembur * kasir.getRateLembur();
            sb.append("Jam Lembur: ").append(totalLembur).append(" jam\n");
            sb.append("Rate Lembur: Rp ").append(String.format("%,.0f", kasir.getRateLembur())).append("/jam\n");
            sb.append("Bonus Lembur: Rp ").append(String.format("%,.0f", bonusLembur)).append("\n");
        } else if (k instanceof Koki) {
            Koki koki = (Koki) k;
            double bonusMenu = koki.getJumlahMenuSelesai() * koki.getRatePerMenu();
            sb.append("Menu Selesai: ").append(koki.getJumlahMenuSelesai()).append("\n");
            sb.append("Rate Per Menu: Rp ").append(String.format("%,.0f", koki.getRatePerMenu())).append("\n");
            sb.append("Bonus Menu: Rp ").append(String.format("%,.0f", bonusMenu)).append("\n");
        }
        
        sb.append("\nTOTAL GAJI: Rp ").append(String.format("%,.0f", gaji)).append("\n");
        
        return sb.toString();
    }
}
// PENUTUP KODE BERUBAH