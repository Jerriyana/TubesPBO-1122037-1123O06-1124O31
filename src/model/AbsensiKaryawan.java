package model;

import java.time.LocalDate;
public class AbsensiKaryawan {
    private String idAbsensi;
    private LocalDate tanggal;
    private int jamMasuk;
    private int jamPulang;

    public AbsensiKaryawan(String idAbsensi, LocalDate tanggal, int jamMasuk, int jamPulang) {
        this.idAbsensi = idAbsensi;
        this.tanggal = tanggal;
        this.jamMasuk = jamMasuk;
        this.jamPulang = jamPulang;
    }

    public String getIdAbsensi() {
        return idAbsensi;
    }

    public void setIdAbsensi(String idAbsensi) {
        this.idAbsensi = idAbsensi;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public int getJamMasuk() {
        return jamMasuk;
    }

    public void setJamMasuk(int jamMasuk) {
        this.jamMasuk = jamMasuk;
    }

    public int getJamPulang() {
        return jamPulang;
    }

    public void setJamPulang(int jamPulang) {
        this.jamPulang = jamPulang;
    }

    /**
     * Hitung jam lembur karyawan berdasarkan jam masuk dan jam pulang
     * @return jumlah jam lembur
     */
    public double hitungJamLembur(){
        int jamKerja = jamPulang - jamMasuk;
        int jamNormal = 8;
        return Math.max(0, jamKerja - jamNormal);
    }
    
}
