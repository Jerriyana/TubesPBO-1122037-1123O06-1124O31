package model.repository;

import java.util.List;

import model.BahanBaku;

public interface BahanBakuRepository {
    BahanBaku getBahanByKode(String kode);
    List<BahanBaku> getAllBahan();
    void addBahan(BahanBaku bahan);
    void updateBahan(BahanBaku bahan);
    void deleteBahan(String kode);
}
