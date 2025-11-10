package model.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import model.Menu;

public interface MenuRepository {
    Menu getMenuByKode(String kode);
    List<Menu> getAllMenu();
    List<Menu> getMenuAktif();
    void addMenu(Menu menu);
    void updateMenu(Menu menu);
    void deleteMenu(String kode);
    Map<Menu, Integer> getMenuTerlaris(LocalDate startDate, LocalDate endDate);
}
