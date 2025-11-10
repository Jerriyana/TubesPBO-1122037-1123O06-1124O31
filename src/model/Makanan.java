package model;

import java.util.List;
import java.util.ArrayList;

public class Makanan extends Menu {
    private List<BahanMenu> listBahanDibutuhkan;

    public Makanan(String kodeMenu, String nama, double harga) {
        super(kodeMenu, nama, harga);
        this.listBahanDibutuhkan = new ArrayList<>();
    }

    @Override
    public String getKategori() {
        return "Makanan";
    }

    public List<BahanMenu> getListBahanDibutuhkan() {
        return listBahanDibutuhkan;
    }

    public void setListBahanDibutuhkan(List<BahanMenu> listBahanDibutuhkan) {
        this.listBahanDibutuhkan = listBahanDibutuhkan;
    }

}
