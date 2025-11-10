package model;

public class BahanBaku {
    private String kodeBahan;
    private String namaBahan;
    private double stok;
    private String satuan;

    public BahanBaku(String kodeBahan, String namaBahan, double stok, String satuan) {
        this.kodeBahan = kodeBahan;
        this.namaBahan = namaBahan;
        this.stok = stok;
        this.satuan = satuan;
    }

    public String getKodeBahan() {
        return kodeBahan;
    }

    public void setKodeBahan(String kodeBahan) {
        this.kodeBahan = kodeBahan;
    }

    public String getNamaBahan() {
        return namaBahan;
    }

    public void setNamaBahan(String namaBahan) {
        this.namaBahan = namaBahan;
    }

    public double getStok() {
        return stok;
    }

    public void setStok(double stok) {
        this.stok = stok;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    
    
}
