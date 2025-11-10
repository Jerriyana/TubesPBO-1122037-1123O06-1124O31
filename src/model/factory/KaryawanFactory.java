package model.factory;

import model.Admin;
import model.Karyawan;
import model.Kasir;
import model.Koki;

public class KaryawanFactory {
    public static Karyawan createKaryawan(String role, String nik, String nama, String alamat,
            String telepon, String password, double gajiPokok,
            double rateExtra) {
        switch (role.toLowerCase()) {
            case "admin":            
                return new Admin(nik, nama, alamat, telepon, password, gajiPokok);

            case "kasir":
                return new Kasir(nik, nama, alamat, telepon, password, gajiPokok, rateExtra);

            case "koki":                
                return new Koki(nik, nama, alamat, telepon, password, gajiPokok, rateExtra);

            default:
                throw new IllegalArgumentException("Role tidak valid: " + role);
        }
    }
}
