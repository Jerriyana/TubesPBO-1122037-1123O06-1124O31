package model.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import model.ItemPesanan;
import model.Kasir;
import model.Pesanan;

public class PesananRepositoryImpl implements PesananRepository {
    private List<Pesanan> dataPesanan;
    private int itemCounter = 1;
    private MenuRepository menuRepo;
    private KaryawanRepository karyawanRepo;

    public PesananRepositoryImpl() {
        this.dataPesanan = new ArrayList<>();
    }

    public void setDependencies(MenuRepository menuRepo, KaryawanRepository karyawanRepo) {
        this.menuRepo = menuRepo;
        this.karyawanRepo = karyawanRepo;
        initDummyData();
    }

    private void initDummyData() {
        if (menuRepo == null || karyawanRepo == null)
            return;

        // Ambil kasir dummy
        Kasir kasir1 = (Kasir) karyawanRepo.getKaryawanByNik("KSR001");

        // Pesanan 1 - Meja 3 (Aktif)
        Pesanan pesanan1 = new Pesanan("ORD001", 3, kasir1);
        pesanan1.setWaktuPesan(LocalDateTime.now().minusMinutes(15));

        ItemPesanan item1 = new ItemPesanan("ITEM001", menuRepo.getMenuByKode("MKN001"), 2, "Pedas");
        item1.setWaktuDibuat(LocalDateTime.now().minusMinutes(15));
        item1.setStatusItem("Menunggu");

        ItemPesanan item2 = new ItemPesanan("ITEM002", menuRepo.getMenuByKode("MKN003"), 1, "");
        item2.setWaktuDibuat(LocalDateTime.now().minusMinutes(14));
        item2.setStatusItem("Menunggu");

        ItemPesanan item3 = new ItemPesanan("ITEM003", menuRepo.getMenuByKode("MNM001"), 3, "");
        item3.setWaktuDibuat(LocalDateTime.now().minusMinutes(13));
        item3.setStatusItem("Menunggu");

        pesanan1.getListPesananItem().add(item1);
        pesanan1.getListPesananItem().add(item2);
        pesanan1.getListPesananItem().add(item3);

        // Pesanan 2 - Meja 5 (Aktif)
        Pesanan pesanan2 = new Pesanan("ORD002", 5, kasir1);
        pesanan2.setWaktuPesan(LocalDateTime.now().minusMinutes(10));

        ItemPesanan item4 = new ItemPesanan("ITEM004", menuRepo.getMenuByKode("MKN002"), 2, "Tanpa cabe");
        item4.setWaktuDibuat(LocalDateTime.now().minusMinutes(10));
        item4.setStatusItem("Dimasak");

        ItemPesanan item5 = new ItemPesanan("ITEM005", menuRepo.getMenuByKode("MKN004"), 1, "");
        item5.setWaktuDibuat(LocalDateTime.now().minusMinutes(9));
        item5.setStatusItem("Menunggu");

        pesanan2.getListPesananItem().add(item4);
        pesanan2.getListPesananItem().add(item5);

        // Pesanan 3 - Meja 7 (Aktif)
        Pesanan pesanan3 = new Pesanan("ORD003", 7, kasir1);
        pesanan3.setWaktuPesan(LocalDateTime.now().minusMinutes(5));

        ItemPesanan item6 = new ItemPesanan("ITEM006", menuRepo.getMenuByKode("MKN005"), 3, "");
        item6.setWaktuDibuat(LocalDateTime.now().minusMinutes(5));
        item6.setStatusItem("Menunggu");

        pesanan3.getListPesananItem().add(item6);

        dataPesanan.add(pesanan1);
        dataPesanan.add(pesanan2);
        dataPesanan.add(pesanan3);

        itemCounter = 7; // Update counter
    }

    @Override
    public Pesanan getPesananByMeja(int noMeja) {
        for (Pesanan p : dataPesanan) {
            if (p.getNoMeja() == noMeja && p.getStatusPesanan().equals("Aktif")) {
                return p; // langsung return begitu ketemu
            }
        }
        return null; // kalau ga ada yang cocok
    }

    @Override
    public List<Pesanan> getAllPesanan() {
        return new ArrayList<>(dataPesanan);
    }

    @Override
    public List<Pesanan> getPesananByPeriod(LocalDate startDate, LocalDate endDate) {
        List<Pesanan> hasil = new ArrayList<>();
        for (Pesanan p : dataPesanan) {
            LocalDate tglPesan = p.getWaktuPesan().toLocalDate();
            if (!tglPesan.isBefore(startDate) && !tglPesan.isAfter(endDate)) {
                hasil.add(p);
            }
        }
        return hasil;
    }

    @Override
    public void addPesanan(Pesanan pesanan) {
        dataPesanan.add(pesanan);
    }

    @Override
    public void updatePesanan(Pesanan pesanan) {
        for (int i = 0; i < dataPesanan.size(); i++) {
            if (dataPesanan.get(i).getIdPesanan().equals(pesanan.getIdPesanan())) {
                dataPesanan.set(i, pesanan);
                break;
            }
        }
    }

    @Override
    public List<ItemPesanan> getAntrianMasakan() {
        List<ItemPesanan> antrian = new ArrayList<>();
        for (Pesanan p : dataPesanan) {
            if (p.getStatusPesanan().equals("Aktif")) {
                for (ItemPesanan item : p.getListPesananItem()) {
                    if (item.getStatusItem().equals("Menunggu") || item.getStatusItem().equals("Dimasak")) {
                        antrian.add(item);
                    }
                }
            }
        }
        // Sort by waktu dibuat (FIFO)
        antrian.sort(Comparator.comparing(ItemPesanan::getWaktuDibuat));
        return antrian;
    }

    public String generateItemId() {
        return "ITEM" + String.format("%03d", itemCounter++);
    }
}
