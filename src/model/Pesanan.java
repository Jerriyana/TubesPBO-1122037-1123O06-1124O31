package model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Pesanan {
    private String idPesanan;
    private int noMeja;
    private LocalDateTime waktuPesan;
    private Kasir kasir;
    private String statusPesanan; // "Aktif" atau "Lunas"
    private List<ItemPesanan> listPesananItem;
    private double pajak;

    public Pesanan(String idPesanan, int noMeja, Kasir kasir) {
        this.idPesanan = idPesanan;
        this.noMeja = noMeja;
        this.waktuPesan = LocalDateTime.now();
        this.kasir = kasir;
        this.statusPesanan = "Aktif";
        this.listPesananItem = new ArrayList<>();
        this.pajak = 0.10;
    }

    /**
     * Menghitung subtotal pesanan sebelum pajak.
     * @return Subtotal pesanan
     */
    public double hitungSubtotal() {
        double subtotal = 0;
        for (ItemPesanan item : listPesananItem) {
            subtotal += item.getMenu().getHarga() * item.getKuantitas();
        }
        return subtotal;
    }

    /**
     * Menghitung pajak dari subtotal pesanan.
     * @return Jumlah pajak
     */
    public double hitungPajak() {
        return hitungSubtotal() * pajak;
    }

    /**
     * Menghitung total pesanan termasuk pajak.
     * @return Total pesanan
     */
    public double hitungTotal() {
        return hitungSubtotal() + hitungPajak();
    }

    public String getIdPesanan() {
        return idPesanan;
    }

    public void setIdPesanan(String idPesanan) {
        this.idPesanan = idPesanan;
    }

    public int getNoMeja() {
        return noMeja;
    }

    public void setNoMeja(int noMeja) {
        this.noMeja = noMeja;
    }

    public LocalDateTime getWaktuPesan() {
        return waktuPesan;
    }

    public void setWaktuPesan(LocalDateTime waktuPesan) {
        this.waktuPesan = waktuPesan;
    }

    public Kasir getKasir() {
        return kasir;
    }

    public void setKasir(Kasir kasir) {
        this.kasir = kasir;
    }

    public String getStatusPesanan() {
        return statusPesanan;
    }

    public void setStatusPesanan(String statusPesanan) {
        this.statusPesanan = statusPesanan;
    }

    public List<ItemPesanan> getListPesananItem() {
        return listPesananItem;
    }

    public void setListPesananItem(List<ItemPesanan> listPesananItem) {
        this.listPesananItem = listPesananItem;
    }

    public double getPajak() {
        return pajak;
    }

    public void setPajak(double pajak) {
        this.pajak = pajak;
    }
}
