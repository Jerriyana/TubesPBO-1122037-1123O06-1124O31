package model;

public abstract class Menu {
    private String kodeMenu;
    private String nama;
    private double harga;
    private boolean aktif;

    public Menu(String kodeMenu, String nama, double harga) {
        this.kodeMenu = kodeMenu;
        this.nama = nama;
        this.harga = harga;
        this.aktif = true;
    }
    /**
     * Get the value of kategori
     * @return kategori
     */
    public abstract String getKategori();
    
    public String getKodeMenu() {
        return kodeMenu;
    }
    public void setKodeMenu(String kodeMenu) {
        this.kodeMenu = kodeMenu;
    }
    public String getNama() {
        return nama;
    }
    public void setNama(String nama) {
        this.nama = nama;
    }
    public double getHarga() {
        return harga;
    }
    public void setHarga(double harga) {
        this.harga = harga;
    }
    public boolean isAktif() {
        return aktif;
    }
    public void setAktif(boolean aktif) {
        this.aktif = aktif;
    }

    
}
