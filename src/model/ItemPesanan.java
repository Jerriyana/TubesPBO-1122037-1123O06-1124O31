package model;

import java.time.LocalDateTime;

public class ItemPesanan {
    private String idItemPesanan;
    private Menu menu;
    private int kuantitas;
    private String statusItem; // "Menunggu", "Dimasak", "Siap"
    private String catatan;
    private LocalDateTime waktuDibuat;

    public ItemPesanan(String idItemPesanan, Menu menu, int kuantitas, String catatan) {
        this.idItemPesanan = idItemPesanan;
        this.menu = menu;
        this.kuantitas = kuantitas;
        this.statusItem = "Menunggu";
        this.catatan = catatan;
        this.waktuDibuat = LocalDateTime.now();
    }

    public String getIdItemPesanan() {
        return idItemPesanan;
    }

    public void setIdItemPesanan(String idItemPesanan) {
        this.idItemPesanan = idItemPesanan;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public int getKuantitas() {
        return kuantitas;
    }

    public void setKuantitas(int kuantitas) {
        this.kuantitas = kuantitas;
    }

    public String getStatusItem() {
        return statusItem;
    }

    public void setStatusItem(String statusItem) {
        this.statusItem = statusItem;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public LocalDateTime getWaktuDibuat() {
        return waktuDibuat;
    }

    public void setWaktuDibuat(LocalDateTime waktuDibuat) {
        this.waktuDibuat = waktuDibuat;
    }    
}
