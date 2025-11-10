package model;

public class BahanMenu {
    private BahanBaku bahan;
    private double jumlahDibutuhkan;

    public BahanMenu(BahanBaku bahan, double jumlahDibutuhkan) {
        this.bahan = bahan;
        this.jumlahDibutuhkan = jumlahDibutuhkan;
    }

    public BahanBaku getBahan() {
        return bahan;
    }

    public void setBahan(BahanBaku bahan) {
        this.bahan = bahan;
    }

    public double getJumlahDibutuhkan() {
        return jumlahDibutuhkan;
    }

    public void setJumlahDibutuhkan(double jumlahDibutuhkan) {
        this.jumlahDibutuhkan = jumlahDibutuhkan;
    }

    
    
}
