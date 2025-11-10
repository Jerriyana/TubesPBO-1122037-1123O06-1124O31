package model;

import java.util.List;
import java.util.ArrayList;

public abstract class Karyawan {
      private String nik;
    private String nama;
    private String alamat;
    private String telepon;
    private String password;
    private double gajiPokok;
    private List<AbsensiKaryawan> listAbsensi;
    
    public Karyawan(String nik, String nama, String alamat, String telepon, String password, double gajiPokok) {
        this.nik = nik;
        this.nama = nama;
        this.alamat = alamat;
        this.telepon = telepon;
        this.password = password;
        this.gajiPokok = gajiPokok;
        this.listAbsensi = new ArrayList<>();
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getGajiPokok() {
        return gajiPokok;
    }

    public void setGajiPokok(double gajiPokok) {
        this.gajiPokok = gajiPokok;
    }

    public List<AbsensiKaryawan> getListAbsensi() {
        return listAbsensi;
    }

    public void setListAbsensi(List<AbsensiKaryawan> listAbsensi) {
        this.listAbsensi = listAbsensi;
    }    

    /**
     * Hitung gaji karyawan
     * @return gaji karyawan
     */
    public abstract double hitungGaji();
    /**
     * Mendapatkan role karyawan
     * @return role karyawan
     */
    public abstract String getRole();
}
