package model;

public class Admin extends Karyawan{
    public Admin(String nik, String nama, String alamat, String telepon, String password, double gajiPokok) {
        super(nik, nama, alamat, telepon, password, gajiPokok);
    }

    @Override
    public double hitungGaji() {
        return getGajiPokok();
    }

    @Override
    public String getRole() {
        return "Admin";
    }
}
