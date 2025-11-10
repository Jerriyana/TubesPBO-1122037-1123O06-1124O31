package view;

import javax.swing.JOptionPane;

import model.Admin;
import model.Karyawan;
import model.Kasir;
import model.Koki;
import model.repository.BahanBakuRepository;
import model.repository.BahanBakuRepositoryImpl;
import model.repository.KaryawanRepository;
import model.repository.KaryawanRepositoryImpl;
import model.repository.MenuRepository;
import model.repository.MenuRepositoryImpl;
import model.repository.PesananRepository;
import model.repository.PesananRepositoryImpl;

public class LoginView {
    private KaryawanRepository karyawanRepo;
    private MenuRepository menuRepo;
    private PesananRepository pesananRepo;
    private BahanBakuRepository bahanRepo;

    public LoginView() {
        // Inisialisasi Repositories (Singleton pattern bisa diterapkan di sini)
        this.karyawanRepo = new KaryawanRepositoryImpl();
        this.menuRepo = new MenuRepositoryImpl();
        this.bahanRepo = new BahanBakuRepositoryImpl();
        
        // PesananRepo butuh dependency untuk dummy data
        PesananRepositoryImpl pesananRepoImpl = new PesananRepositoryImpl();
        pesananRepoImpl.setDependencies(menuRepo, karyawanRepo);
        this.pesananRepo = pesananRepoImpl;
        
        showLogin();
    }

    private void showLogin() {
        String nik = JOptionPane.showInputDialog(null,
                "=== SISTEM RESTORAN ===\n\nMasukkan NIK:",
                "Login",
                JOptionPane.QUESTION_MESSAGE);

        if (nik == null || nik.trim().isEmpty()) {
            int retry = JOptionPane.showConfirmDialog(null,
                    "NIK tidak boleh kosong!\nCoba lagi?",
                    "Error",
                    JOptionPane.YES_NO_OPTION);
            if (retry == JOptionPane.YES_OPTION) {
                showLogin();
            }
            return;
        }

        String password = JOptionPane.showInputDialog(null,
                "NIK: " + nik + "\n\nMasukkan Password:",
                "Login",
                JOptionPane.QUESTION_MESSAGE);

        if (password == null || password.trim().isEmpty()) {
            int retry = JOptionPane.showConfirmDialog(null,
                    "Password tidak boleh kosong!\nCoba lagi?",
                    "Error",
                    JOptionPane.YES_NO_OPTION);
            if (retry == JOptionPane.YES_OPTION) {
                showLogin();
            }
            return;
        }

        Karyawan karyawan = karyawanRepo.authenticate(nik, password);

        if (karyawan == null) {
            int retry = JOptionPane.showConfirmDialog(null,
                    "NIK atau Password salah!\n\nCoba lagi?",
                    "Login Gagal",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE);

            if (retry == JOptionPane.YES_OPTION) {
                showLogin();
            }
            return;
        }

        JOptionPane.showMessageDialog(null,
                "Login berhasil!\n\nSelamat datang, " + karyawan.getNama() + "\nRole: " + karyawan.getRole(),
                "Sukses",
                JOptionPane.INFORMATION_MESSAGE);

        // Route ke dashboard sesuai role
        if (karyawan instanceof Admin) {
            new AdminView((Admin) karyawan, karyawanRepo, menuRepo, pesananRepo, bahanRepo);
        } else if (karyawan instanceof Kasir) {
            new KasirView((Kasir) karyawan, menuRepo, pesananRepo);
        } else if (karyawan instanceof Koki) {
            new KokiView((Koki) karyawan, pesananRepo, bahanRepo);
        }
    }
}
