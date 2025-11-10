package model.factory;
import model.Menu;
import model.Makanan;
import model.Minuman;


public class MenuFactory {
    
    public static Menu createMenu(String kategori, String kodeMenu, String nama, double harga, String extra) {
        switch (kategori.toLowerCase()) {
            case "makanan":
                return new Makanan(kodeMenu, nama, harga);
            
            case "minuman":
                return new Minuman(kodeMenu, nama, harga, extra);
            
            default:
                throw new IllegalArgumentException("Kategori menu tidak valid: " + kategori);
        }
    }
}
