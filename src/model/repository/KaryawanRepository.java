package model.repository;

import java.util.List;

import model.AbsensiKaryawan;
import model.Karyawan;

public interface KaryawanRepository {
    Karyawan getKaryawanByNik(String nik);
    List<Karyawan> getAllKaryawan();
    Karyawan authenticate(String nik, String password);
    void addAbsensi(String nik, AbsensiKaryawan absensi);
}
