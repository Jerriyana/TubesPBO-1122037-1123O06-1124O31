package model.repository;

import java.util.ArrayList;
import java.util.List;

import model.BahanBaku;

public class BahanBakuRepositoryImpl implements BahanBakuRepository {
    private List<BahanBaku> dataBahan;

    public BahanBakuRepositoryImpl() {
        this.dataBahan = new ArrayList<>();
        initDummyData();
    }

    public void initDummyData() {
        dataBahan.add(new BahanBaku("BHN001", "Daging Ayam", 5, "Kg"));
        dataBahan.add(new BahanBaku("BHN002", "Beras", 100, "Kg"));
        dataBahan.add(new BahanBaku("BHN003", "Mie", 30, "Kg"));
        dataBahan.add(new BahanBaku("BHN004", "Sayuran", 25, "Kg"));
        dataBahan.add(new BahanBaku("BHN005", "Telur", 200, "Butir"));
        dataBahan.add(new BahanBaku("BHN006", "Bumbu Dapur", 15, "Kg"));
    }

    @Override
    public BahanBaku getBahanByKode(String kode) {
        for (BahanBaku b : dataBahan) {
            if (b.getKodeBahan().equals(kode)) {
                return b;
            }
        }
        return null;
    }

    @Override
    public List<BahanBaku> getAllBahan() {
        return new ArrayList<>(dataBahan);
    }

    @Override
    public void addBahan(BahanBaku bahan) {
        dataBahan.add(bahan);
    }

    @Override
    public void updateBahan(BahanBaku bahan) {
        for (int i = 0; i < dataBahan.size(); i++) {
            if (dataBahan.get(i).getKodeBahan().equals(bahan.getKodeBahan())) {
                dataBahan.set(i, bahan);
                break;
            }
        }
    }

    @Override
    public void deleteBahan(String kode) {
        dataBahan.removeIf(b -> b.getKodeBahan().equals(kode));
    }
}
