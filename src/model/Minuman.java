package model;

public class Minuman extends Menu{
    private String opsiUkuran;

    public Minuman(String kodeMenu, String nama, double harga, String opsiUkuran) {
        super(kodeMenu, nama, harga);
        this.opsiUkuran = opsiUkuran;
    }

    @Override
    public String getKategori() {
        return "Minuman";
    }

    public String getOpsiUkuran() {
        return opsiUkuran;
    }

    public void setOpsiUkuran(String opsiUkuran) {
        this.opsiUkuran = opsiUkuran;
    }    
}
