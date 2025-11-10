package model;

import java.util.List;

public class Koki extends Karyawan {
    private double ratePerMenu;
    private int jumlahMenuSelesai;

    public Koki(String nik, String nama, String alamat, String telepon, String password, double gajiPokok, double ratePerMenu) {
        super(nik, nama, alamat, telepon, password, gajiPokok);
        this.ratePerMenu = ratePerMenu;
        this.jumlahMenuSelesai = 0;
    }

    public double getRatePerMenu() {
        return ratePerMenu;
    }

    public void setRatePerMenu(double ratePerMenu) {
        this.ratePerMenu = ratePerMenu;
    }

    public int getJumlahMenuSelesai() {
        return jumlahMenuSelesai;
    }

    public void setJumlahMenuSelesai(int jumlahMenuSelesai) {
        this.jumlahMenuSelesai = jumlahMenuSelesai;
    }

    @Override
    public double hitungGaji() {
        return getGajiPokok() + (jumlahMenuSelesai * ratePerMenu);
    }

    @Override
    public String getRole() {
        return "Koki";
    }

}
