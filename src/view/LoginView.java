package view;

import controller.*;
import model.*;
import model.repository.*;
import util.DatabaseManager; // Pastikan package util benar

import javax.swing.JOptionPane;

public class LoginView {
    // Gunakan Interface untuk tipe datanya (Polymorphism)
    private static KaryawanRepository karyawanRepo = null;
    private static MenuRepository menuRepo = null;
    private static PesananRepository pesananRepo = null;
    private static BahanBakuRepository bahanRepo = null;
    private static KasRepository kasRepo = null;
    
    public LoginView() {
        // Inisialisasi Repositories (Singleton - Hanya sekali dibuat)
        if (karyawanRepo == null) {
            
            // 1. Cek Koneksi Database Dulu
            if (!DatabaseManager.testConnection()) {
                JOptionPane.showMessageDialog(null, 
                    "Gagal koneksi ke database!\nPastikan PostgreSQL running di port 5433.", 
                    "Error Database", 
                    JOptionPane.ERROR_MESSAGE);
                System.exit(0); // Matikan aplikasi jika DB mati
                return;
            }
            
            // 2. Inisialisasi Repository JDBC
            karyawanRepo = new KaryawanRepositoryJDBC();
            menuRepo = new MenuRepositoryJDBC();
            bahanRepo = new BahanBakuRepositoryJDBC();
            
            // Inject dependency ke PesananRepo
            pesananRepo = new PesananRepositoryJDBC(karyawanRepo, menuRepo);
            
            kasRepo = new KasRepository();
        }
        
        showLogin();
    }
    
    private void showLogin() {
        String nik = JOptionPane.showInputDialog(null, 
            "=== SISTEM RESTORAN ===\n\nMasukkan NIK:", 
            "Login", 
            JOptionPane.QUESTION_MESSAGE);
            
        if (nik == null || nik.trim().isEmpty()) {
            int retry = JOptionPane.showConfirmDialog(null, 
                "NIK tidak boleh kosong!\nKeluar dari aplikasi?", 
                "Konfirmasi", 
                JOptionPane.YES_NO_OPTION);
            if (retry == JOptionPane.NO_OPTION) {
                showLogin();
            } else {
                System.exit(0);
            }
            return;
        }
        
        String password = JOptionPane.showInputDialog(null, 
            "NIK: " + nik + "\n\nMasukkan Password:", 
            "Login", 
            JOptionPane.QUESTION_MESSAGE);
            
        if (password == null || password.trim().isEmpty()) {
             // Jika cancel password, kembali ke input NIK
            showLogin();
            return;
        }
        
        // Authenticate ke Database
        Karyawan karyawan = karyawanRepo.authenticate(nik, password);
        
        if (karyawan == null) {
            JOptionPane.showMessageDialog(null, 
                "NIK atau Password salah!", 
                "Login Gagal", 
                JOptionPane.ERROR_MESSAGE);
            showLogin(); // Ulangi login
            return;
        }
        
        JOptionPane.showMessageDialog(null, 
            "Login berhasil!\n\nSelamat datang, " + karyawan.getNama() + "\nRole: " + karyawan.getRole(),
            "Sukses",
            JOptionPane.INFORMATION_MESSAGE);
            
        // Route ke View yang sesuai
        // Note: Controller dibuat di dalam View masing-masing
        if (karyawan instanceof Admin) {
            new AdminView((Admin) karyawan, karyawanRepo, menuRepo, pesananRepo, bahanRepo);
        } else if (karyawan instanceof Kasir) {
            new KasirView((Kasir) karyawan, menuRepo, pesananRepo, bahanRepo, kasRepo);
        } else if (karyawan instanceof Koki) {
            new KokiView((Koki) karyawan, pesananRepo, bahanRepo);
        }
    }
}