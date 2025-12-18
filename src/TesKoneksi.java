import util.DatabaseManager;

public class TesKoneksi {
    public static void main(String[] args) {
        System.out.println("Mencoba menghubungkan ke database...");
        
        // Memanggil fungsi test dari DatabaseManager
        boolean isConnected = DatabaseManager.testConnection();
        
        if (isConnected) {
            System.out.println("✅ BERHASIL: Aplikasi terhubung ke Database PostgreSQL!");
        } else {
            System.out.println("❌ GAGAL: Cek kembali port, username, password, atau nama database.");
        }
    }
}