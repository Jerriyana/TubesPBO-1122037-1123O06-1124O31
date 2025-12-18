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
    
    // TAMBAHAN WAJIB (Agar method di JDBC bisa dipanggil Controller)
    void addItemPesanan(String kodePesanan, ItemPesanan item);
    void updateItemStatus(String kodeItem, String statusBaru);
    ItemPesanan getItemByKode(String kodeItem);
    void deleteItemPesanan(String kodeItem);
    String generateKodePesanan();
    String generateKodeItem();
}