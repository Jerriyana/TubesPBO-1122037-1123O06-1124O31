package controller;

import model.Karyawan;
import model.Kasir;
import model.Koki;
import model.AbsensiKaryawan;
import model.Menu;
import model.Pesanan;
import model.BahanBaku;
import model.factory.MenuFactory;
import model.repository.KaryawanRepository;
import model.repository.MenuRepository;
import model.repository.PesananRepository;
import model.repository.BahanBakuRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
    public String printDataKaryawan(String nik) {
        Karyawan k = karyawanRepo.getKaryawanByNik(nik);
        if (k == null) {
            return "Karyawan dengan NIK " + nik + " tidak ditemukan!";
        }

        String result = "===== DATA KARYAWAN =====\n";
        result += "NIK      : " + k.getNik() + "\n";
        result += "Nama     : " + k.getNama() + "\n";
        result += "Alamat   : " + k.getAlamat() + "\n";
        result += "Telepon  : " + k.getTelepon() + "\n";
        result += "Role     : " + k.getRole() + "\n";
        result += "Gaji Pokok: Rp " + String.format("%,.0f", k.getGajiPokok()) + "\n";

        if (k instanceof Kasir) {
            result += "Rate Lembur: Rp " + String.format("%,.0f", ((Kasir) k).getRateLembur()) + "/jam\n";
        } else if (k instanceof Koki) {
            result += "Rate Per Menu: Rp " + String.format("%,.0f", ((Koki) k).getRatePerMenu()) + "\n";
            result += "Menu Selesai: " + ((Koki) k).getJumlahMenuSelesai() + "\n";
        }

        return result;
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

        String result = "===== LAPORAN PENDAPATAN =====\n";
        result += "Periode: " + startDate + " s/d " + endDate + "\n";
        result += "Jumlah Transaksi: " + jumlahTransaksi + "\n";
        result += "Total Omzet: Rp " + String.format("%,.0f", totalOmzet) + "\n";
        return result;
    }

    // Fitur 4: Print Menu Terlaris
    public String printMenuTerlaris(LocalDate startDate, LocalDate endDate) {
        Map<Menu, Integer> menuTerlaris = menuRepo.getMenuTerlaris(startDate, endDate);

        // Ubah entrySet ke list biasa
        List<Map.Entry<Menu, Integer>> sortedList = new ArrayList<>(menuTerlaris.entrySet());

        // Urutkan manual (descending berdasarkan jumlah terjual)
        Collections.sort(sortedList, new Comparator<Map.Entry<Menu, Integer>>() {
            @Override
            public int compare(Map.Entry<Menu, Integer> e1, Map.Entry<Menu, Integer> e2) {
                return e2.getValue().compareTo(e1.getValue());
            }
        });

        // Ambil maksimal 5 data teratas
        String result = "===== 5 MENU TERLARIS =====\n";
        result += "Periode: " + startDate + " s/d " + endDate + "\n\n";

        int rank = 1;
        for (Map.Entry<Menu, Integer> entry : sortedList) {
            result += rank + ". " + entry.getKey().getNama() + " - Terjual: " + entry.getValue() + " porsi\n";
            rank++;
            if (rank > 5)
                break; // batas top 5
        }

        return result;
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
        String result = "===== PERHITUNGAN GAJI =====\n";
        result += "NIK      : " + k.getNik() + "\n";
        result += "Nama     : " + k.getNama() + "\n";
        result += "Role     : " + k.getRole() + "\n";
        result += "Gaji Pokok: Rp " + String.format("%,.0f", k.getGajiPokok()) + "\n";

        if (k instanceof Kasir) {
            Kasir kasir = (Kasir) k;
            double totalLembur = 0;
            for (AbsensiKaryawan abs : kasir.getListAbsensi()) {
                totalLembur += abs.hitungJamLembur();
            }
            double bonusLembur = totalLembur * kasir.getRateLembur();
            result += "Jam Lembur: " + totalLembur + " jam\n";
            result += "Rate Lembur: Rp " + String.format("%,.0f", kasir.getRateLembur()) + "/jam\n";
            result += "Bonus Lembur: Rp " + String.format("%,.0f", bonusLembur) + "\n";
        } else if (k instanceof Koki) {
            Koki koki = (Koki) k;
            double bonusMenu = koki.getJumlahMenuSelesai() * koki.getRatePerMenu();
            result += "Menu Selesai: " + koki.getJumlahMenuSelesai() + "\n";
            result += "Rate Per Menu: Rp " + String.format("%,.0f", koki.getRatePerMenu()) + "\n";
            result += "Bonus Menu: Rp " + String.format("%,.0f", bonusMenu) + "\n";
        }

        result += "\nTOTAL GAJI: Rp " + String.format("%,.0f", gaji) + "\n";
        return result;
    }
}
