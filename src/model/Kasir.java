package model;

import java.util.List;

public class Kasir extends Karyawan {
    private double rateLembur;

    public Kasir(String nik, String nama, String alamat, String telepon, String password, double gajiPokok, double rateLembur) {
        super(nik, nama, alamat, telepon, password, gajiPokok);
        this.rateLembur = rateLembur;
    }

    public double getRateLembur() {
        return rateLembur;
    }

    public void setRateLembur(double rateLembur) {
        this.rateLembur = rateLembur;
    }

    @Override
    public double hitungGaji() {
        double totalJamLembur = 0;
        for (AbsensiKaryawan abs : getListAbsensi()) {
            totalJamLembur += abs.hitungJamLembur();
        }
        return getGajiPokok() + (totalJamLembur * rateLembur);
    }

    @Override
    public String getRole() {
        return "Kasir";
    }
}
