package model.repository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.AbsensiKaryawan;
import model.Admin;
import model.Karyawan;
import model.Kasir;
import model.Koki;

public class KaryawanRepositoryImpl implements KaryawanRepository {
    private List<Karyawan> dataKaryawan;

     public KaryawanRepositoryImpl() {
        this.dataKaryawan = new ArrayList<>();
        initDummyData();
    }

    private void initDummyData() {                
        // Admin
        Admin admin = new Admin("ADM001", "Budi Santoso", "Jl. Merdeka No.1", "08111111111", "admin123", 8000000);
        admin.getListAbsensi().add(new AbsensiKaryawan("ABS001", LocalDate.now().minusDays(1), 8, 17));
        admin.getListAbsensi().add(new AbsensiKaryawan("ABS002", LocalDate.now().minusDays(2), 8, 16));
        
        // Kasir
        Kasir kasir1 = new Kasir("KSR001", "Siti Aminah", "Jl. Raya No.2", "08122222222", "kasir123", 5000000, 50000);
        kasir1.getListAbsensi().add(new AbsensiKaryawan("ABS003", LocalDate.now().minusDays(1), 8, 19)); // 3 jam lembur
        kasir1.getListAbsensi().add(new AbsensiKaryawan("ABS004", LocalDate.now().minusDays(2), 8, 18)); // 2 jam lembur
        
        Kasir kasir2 = new Kasir("KSR002", "Rina Wati", "Jl. Sudirman No.3", "08133333333", "kasir456", 5000000, 50000);
        
        // Koki
        Koki koki1 = new Koki("KOK001", "Agus Wijaya", "Jl. Gatot No.4", "08144444444", "koki123", 6000000, 15000);
        koki1.setJumlahMenuSelesai(45);
        
        Koki koki2 = new Koki("KOK002", "Dedi Kurniawan", "Jl. Ahmad Yani No.5", "08155555555", "koki456", 6000000, 15000);
        koki2.setJumlahMenuSelesai(38);
        
        dataKaryawan.add(admin);
        dataKaryawan.add(kasir1);
        dataKaryawan.add(kasir2);
        dataKaryawan.add(koki1);
        dataKaryawan.add(koki2);
    }

    @Override
    public Karyawan getKaryawanByNik(String nik) {
        return dataKaryawan.stream()
            .filter(k -> k.getNik().equals(nik))
            .findFirst()
            .orElse(null);
    }

    @Override
    public List<Karyawan> getAllKaryawan() {
        return new ArrayList<>(dataKaryawan);
    }

    @Override
    public Karyawan authenticate(String nik, String password) {
        return dataKaryawan.stream()
            .filter(k -> k.getNik().equals(nik) && k.getPassword().equals(password))
            .findFirst()
            .orElse(null);
    }

    @Override
    public void addAbsensi(String nik, AbsensiKaryawan absensi) {
        Karyawan k = getKaryawanByNik(nik);
        if (k != null) {
            k.getListAbsensi().add(absensi);
        }
    }
}
