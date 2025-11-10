package model.repository;

import java.time.LocalDate;
import java.util.List;

import model.ItemPesanan;
import model.Pesanan;

public interface PesananRepository {
    Pesanan getPesananByMeja(int noMeja);
    List<Pesanan> getAllPesanan();
    List<Pesanan> getPesananByPeriod(LocalDate startDate, LocalDate endDate);
    void addPesanan(Pesanan pesanan);
    void updatePesanan(Pesanan pesanan);
    List<ItemPesanan> getAntrianMasakan();
}
