package model.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import model.Makanan;
import model.Menu;
import model.Minuman;

public class MenuRepositoryImpl implements MenuRepository {
    private List<Menu> dataMenu;
    private Map<String, Integer> menuTerjual; // tracking jumlah terjual

    public MenuRepositoryImpl() {
        this.dataMenu = new ArrayList<>();
        this.menuTerjual = new HashMap<>();
        initDummyData();
    }

    private void initDummyData() {
        // Makanan
        Makanan nasi_goreng = new Makanan("MKN001", "Nasi Goreng Special", 25000);
        Makanan mie_goreng = new Makanan("MKN002", "Mie Goreng", 20000);
        Makanan ayam_bakar = new Makanan("MKN003", "Ayam Bakar", 35000);
        Makanan soto_ayam = new Makanan("MKN004", "Soto Ayam", 22000);
        Makanan gado_gado = new Makanan("MKN005", "Gado-Gado", 18000);

        // Minuman
        Minuman es_teh = new Minuman("MNM001", "Es Teh Manis", 5000, "Medium");
        Minuman jus_jeruk = new Minuman("MNM002", "Jus Jeruk", 12000, "Large");
        Minuman kopi = new Minuman("MNM003", "Kopi Hitam", 8000, "Small");
        Minuman es_kelapa = new Minuman("MNM004", "Es Kelapa Muda", 10000, "Medium");

        dataMenu.add(nasi_goreng);
        dataMenu.add(mie_goreng);
        dataMenu.add(ayam_bakar);
        dataMenu.add(soto_ayam);
        dataMenu.add(gado_gado);
        dataMenu.add(es_teh);
        dataMenu.add(jus_jeruk);
        dataMenu.add(kopi);
        dataMenu.add(es_kelapa);

        // Dummy data penjualan (untuk menu terlaris)
        menuTerjual.put("MKN001", 45);
        menuTerjual.put("MKN002", 38);
        menuTerjual.put("MKN003", 52);
        menuTerjual.put("MNM001", 67);
        menuTerjual.put("MNM002", 29);
    }

    @Override
    public Menu getMenuByKode(String kode) {
        for (Menu m : dataMenu) {
            if (m.getKodeMenu().equals(kode)) {
                return m;
            }
        }
        return null;
    }

    @Override
    public List<Menu> getAllMenu() {
        return new ArrayList<>(dataMenu);
    }

    @Override
    public List<Menu> getMenuAktif() {
        List<Menu> hasil = new ArrayList<>();
        for (Menu m : dataMenu) {
            if (m.isAktif()) {
                hasil.add(m);
            }
        }
        return hasil;
    }

    @Override
    public void addMenu(Menu menu) {
        dataMenu.add(menu);
    }

    @Override
    public void updateMenu(Menu menu) {
        for (int i = 0; i < dataMenu.size(); i++) {
            if (dataMenu.get(i).getKodeMenu().equals(menu.getKodeMenu())) {
                dataMenu.set(i, menu);
                break;
            }
        }
    }

    @Override
    public void deleteMenu(String kode) {
        Menu menu = getMenuByKode(kode);
        if (menu != null) {
            menu.setAktif(false);
        }
    }

    @Override
    public Map<Menu, Integer> getMenuTerlaris(LocalDate startDate, LocalDate endDate) {
        Map<Menu, Integer> result = new HashMap<>();
        for (Map.Entry<String, Integer> entry : menuTerjual.entrySet()) {
            Menu menu = getMenuByKode(entry.getKey());
            if (menu != null) {
                result.put(menu, entry.getValue());
            }
        }
        return result;
    }

    public void incrementMenuTerjual(String kodeMenu, int qty) {
        menuTerjual.put(kodeMenu, menuTerjual.getOrDefault(kodeMenu, 0) + qty);
    }
}
