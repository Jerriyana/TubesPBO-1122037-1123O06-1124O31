-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               PostgreSQL 17.6 on x86_64-windows, compiled by msvc-19.44.35213, 64-bit
-- Server OS:                    
-- HeidiSQL Version:             12.11.0.7065
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES  */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- Dumping structure for table public.tabel_absensi
CREATE TABLE IF NOT EXISTS "tabel_absensi" (
	"id_absensi" SERIAL NOT NULL,
	"id_karyawan" INTEGER NOT NULL,
	"tanggal" DATE NOT NULL,
	"jam_masuk" INTEGER NOT NULL,
	"jam_pulang" INTEGER NOT NULL,
	PRIMARY KEY ("id_absensi"),
	CONSTRAINT "tabel_absensi_id_karyawan_fkey" FOREIGN KEY ("id_karyawan") REFERENCES "tabel_karyawan" ("id_karyawan") ON UPDATE NO ACTION ON DELETE CASCADE
);

-- Dumping data for table public.tabel_absensi: 5 rows
/*!40000 ALTER TABLE "tabel_absensi" DISABLE KEYS */;
INSERT INTO "tabel_absensi" ("id_absensi", "id_karyawan", "tanggal", "jam_masuk", "jam_pulang") VALUES
	(1, 1, '2025-12-17', 8, 17),
	(2, 1, '2025-12-16', 8, 16),
	(3, 2, '2025-12-17', 8, 19),
	(4, 2, '2025-12-16', 8, 18),
	(5, 4, '2025-12-17', 8, 17);
/*!40000 ALTER TABLE "tabel_absensi" ENABLE KEYS */;

-- Dumping structure for table public.tabel_bahan_baku
CREATE TABLE IF NOT EXISTS "tabel_bahan_baku" (
	"id_bahan" SERIAL NOT NULL,
	"kode_bahan" VARCHAR(20) NOT NULL,
	"nama_bahan" VARCHAR(100) NOT NULL,
	"stok" NUMERIC(12,2) NOT NULL,
	"satuan" VARCHAR(20) NOT NULL,
	PRIMARY KEY ("id_bahan"),
	UNIQUE ("kode_bahan")
);

-- Dumping data for table public.tabel_bahan_baku: -1 rows
/*!40000 ALTER TABLE "tabel_bahan_baku" DISABLE KEYS */;
INSERT INTO "tabel_bahan_baku" ("id_bahan", "kode_bahan", "nama_bahan", "stok", "satuan") VALUES
	(1, 'BHN001', 'Daging Ayam', 50.00, 'Kg'),
	(2, 'BHN002', 'Beras', 100.00, 'Kg'),
	(3, 'BHN003', 'Mie', 30.00, 'Kg'),
	(4, 'BHN004', 'Sayuran', 25.00, 'Kg'),
	(5, 'BHN005', 'Telur', 200.00, 'Butir'),
	(6, 'BHN006', 'Bumbu Dapur', 15.00, 'Kg');
/*!40000 ALTER TABLE "tabel_bahan_baku" ENABLE KEYS */;

-- Dumping structure for table public.tabel_item_pesanan
CREATE TABLE IF NOT EXISTS "tabel_item_pesanan" (
	"id_item_pesanan" SERIAL NOT NULL,
	"kode_item" VARCHAR(20) NOT NULL,
	"id_pesanan" INTEGER NOT NULL,
	"id_menu" INTEGER NOT NULL,
	"kuantitas" INTEGER NOT NULL,
	"catatan" TEXT NULL DEFAULT NULL,
	"status_item" VARCHAR(20) NOT NULL,
	"waktu_dibuat" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY ("id_item_pesanan"),
	UNIQUE ("kode_item"),
	CONSTRAINT "tabel_item_pesanan_id_menu_fkey" FOREIGN KEY ("id_menu") REFERENCES "tabel_menu" ("id_menu") ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT "tabel_item_pesanan_id_pesanan_fkey" FOREIGN KEY ("id_pesanan") REFERENCES "tabel_pesanan" ("id_pesanan") ON UPDATE NO ACTION ON DELETE CASCADE,
	CONSTRAINT "tabel_item_pesanan_status_item_check" CHECK (((status_item)::text = ANY ((ARRAY['Menunggu'::character varying, 'Dimasak'::character varying, 'Siap'::character varying])::text[])))
)
CREATE INDEX "idx_item_status" ON "" ("status_item");;

-- Dumping data for table public.tabel_item_pesanan: -1 rows
/*!40000 ALTER TABLE "tabel_item_pesanan" DISABLE KEYS */;
INSERT INTO "tabel_item_pesanan" ("id_item_pesanan", "kode_item", "id_pesanan", "id_menu", "kuantitas", "catatan", "status_item", "waktu_dibuat") VALUES
	(1, 'ITEM001', 1, 1, 2, 'Pedas', 'Menunggu', '2025-12-18 13:18:59.327338'),
	(2, 'ITEM002', 1, 3, 1, '', 'Menunggu', '2025-12-18 13:19:59.327338'),
	(3, 'ITEM003', 1, 6, 3, '', 'Menunggu', '2025-12-18 13:20:59.327338'),
	(4, 'ITEM004', 2, 2, 2, 'Tanpa cabe', 'Dimasak', '2025-12-18 13:23:59.327338'),
	(5, 'ITEM005', 2, 4, 1, '', 'Menunggu', '2025-12-18 13:24:59.327338'),
	(6, 'ITEM006', 3, 5, 3, '', 'Menunggu', '2025-12-18 13:28:59.327338');
/*!40000 ALTER TABLE "tabel_item_pesanan" ENABLE KEYS */;

-- Dumping structure for table public.tabel_karyawan
CREATE TABLE IF NOT EXISTS "tabel_karyawan" (
	"id_karyawan" SERIAL NOT NULL,
	"nik" VARCHAR(20) NOT NULL,
	"nama" VARCHAR(100) NOT NULL,
	"alamat" TEXT NULL DEFAULT NULL,
	"telepon" VARCHAR(20) NULL DEFAULT NULL,
	"password" VARCHAR(100) NOT NULL,
	"role" VARCHAR(20) NOT NULL,
	"gaji_pokok" NUMERIC(12,2) NOT NULL,
	"rate_lembur" NUMERIC(12,2) NULL DEFAULT 0,
	"rate_per_menu" NUMERIC(12,2) NULL DEFAULT 0,
	"jumlah_menu_selesai" INTEGER NULL DEFAULT 0,
	PRIMARY KEY ("id_karyawan"),
	UNIQUE ("nik"),
	CONSTRAINT "tabel_karyawan_role_check" CHECK (((role)::text = ANY ((ARRAY['Admin'::character varying, 'Kasir'::character varying, 'Koki'::character varying])::text[])))
)
CREATE INDEX "idx_karyawan_nik" ON "" ("nik");;

-- Dumping data for table public.tabel_karyawan: -1 rows
/*!40000 ALTER TABLE "tabel_karyawan" DISABLE KEYS */;
INSERT INTO "tabel_karyawan" ("id_karyawan", "nik", "nama", "alamat", "telepon", "password", "role", "gaji_pokok", "rate_lembur", "rate_per_menu", "jumlah_menu_selesai") VALUES
	(1, 'ADM001', 'Budi Santoso', 'Jl. Merdeka No.1', '08111111111', 'admin123', 'Admin', 8000000.00, 0.00, 0.00, 0),
	(2, 'KSR001', 'Siti Aminah', 'Jl. Raya No.2', '08122222222', 'kasir123', 'Kasir', 5000000.00, 50000.00, 0.00, 0),
	(3, 'KSR002', 'Rina Wati', 'Jl. Sudirman No.3', '08133333333', 'kasir456', 'Kasir', 5000000.00, 50000.00, 0.00, 0),
	(4, 'KOK001', 'Agus Wijaya', 'Jl. Gatot No.4', '08144444444', 'koki123', 'Koki', 6000000.00, 0.00, 15000.00, 45),
	(5, 'KOK002', 'Dedi Kurniawan', 'Jl. Ahmad Yani No.5', '08155555555', 'koki456', 'Koki', 6000000.00, 0.00, 15000.00, 38);
/*!40000 ALTER TABLE "tabel_karyawan" ENABLE KEYS */;

-- Dumping structure for table public.tabel_kas
CREATE TABLE IF NOT EXISTS "tabel_kas" (
	"id_kas" SERIAL NOT NULL,
	"saldo" NUMERIC(15,2) NOT NULL,
	"last_updated" TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY ("id_kas")
);

-- Dumping data for table public.tabel_kas: -1 rows
/*!40000 ALTER TABLE "tabel_kas" DISABLE KEYS */;
INSERT INTO "tabel_kas" ("id_kas", "saldo", "last_updated") VALUES
	(1, 10000000.00, '2025-12-18 13:33:59.295366');
/*!40000 ALTER TABLE "tabel_kas" ENABLE KEYS */;

-- Dumping structure for table public.tabel_menu
CREATE TABLE IF NOT EXISTS "tabel_menu" (
	"id_menu" SERIAL NOT NULL,
	"kode_menu" VARCHAR(20) NOT NULL,
	"nama" VARCHAR(100) NOT NULL,
	"harga" NUMERIC(12,2) NOT NULL,
	"kategori_menu" VARCHAR(20) NOT NULL,
	"opsi_ukuran" VARCHAR(20) NULL DEFAULT NULL,
	"aktif" BOOLEAN NULL DEFAULT true,
	PRIMARY KEY ("id_menu"),
	UNIQUE ("kode_menu"),
	CONSTRAINT "tabel_menu_kategori_menu_check" CHECK (((kategori_menu)::text = ANY ((ARRAY['Makanan'::character varying, 'Minuman'::character varying])::text[])))
)
CREATE INDEX "idx_menu_kode" ON "" ("kode_menu");;

-- Dumping data for table public.tabel_menu: -1 rows
/*!40000 ALTER TABLE "tabel_menu" DISABLE KEYS */;
INSERT INTO "tabel_menu" ("id_menu", "kode_menu", "nama", "harga", "kategori_menu", "opsi_ukuran", "aktif") VALUES
	(1, 'MKN001', 'Nasi Goreng Special', 25000.00, 'Makanan', NULL, 'true'),
	(2, 'MKN002', 'Mie Goreng', 20000.00, 'Makanan', NULL, 'true'),
	(3, 'MKN003', 'Ayam Bakar', 35000.00, 'Makanan', NULL, 'true'),
	(4, 'MKN004', 'Soto Ayam', 22000.00, 'Makanan', NULL, 'true'),
	(5, 'MKN005', 'Gado-Gado', 18000.00, 'Makanan', NULL, 'true'),
	(6, 'MNM001', 'Es Teh Manis', 5000.00, 'Minuman', 'Medium', 'true'),
	(7, 'MNM002', 'Jus Jeruk', 12000.00, 'Minuman', 'Large', 'true'),
	(8, 'MNM003', 'Kopi Hitam', 8000.00, 'Minuman', 'Small', 'true'),
	(9, 'MNM004', 'Es Kelapa Muda', 10000.00, 'Minuman', 'Medium', 'true');
/*!40000 ALTER TABLE "tabel_menu" ENABLE KEYS */;

-- Dumping structure for table public.tabel_pesanan
CREATE TABLE IF NOT EXISTS "tabel_pesanan" (
	"id_pesanan" SERIAL NOT NULL,
	"kode_pesanan" VARCHAR(20) NOT NULL,
	"id_karyawan" INTEGER NOT NULL,
	"no_meja" INTEGER NOT NULL,
	"waktu_pesan" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	"status_pesanan" VARCHAR(20) NOT NULL,
	"total_bayar" NUMERIC(12,2) NULL DEFAULT NULL,
	"pajak" NUMERIC(5,2) NULL DEFAULT 0.10,
	PRIMARY KEY ("id_pesanan"),
	UNIQUE ("kode_pesanan"),
	CONSTRAINT "tabel_pesanan_id_karyawan_fkey" FOREIGN KEY ("id_karyawan") REFERENCES "tabel_karyawan" ("id_karyawan") ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT "tabel_pesanan_status_pesanan_check" CHECK (((status_pesanan)::text = ANY ((ARRAY['Aktif'::character varying, 'Lunas'::character varying])::text[])))
)
CREATE INDEX "idx_pesanan_meja" ON "" ("no_meja", "status_pesanan");;

-- Dumping data for table public.tabel_pesanan: -1 rows
/*!40000 ALTER TABLE "tabel_pesanan" DISABLE KEYS */;
INSERT INTO "tabel_pesanan" ("id_pesanan", "kode_pesanan", "id_karyawan", "no_meja", "waktu_pesan", "status_pesanan", "total_bayar", "pajak") VALUES
	(1, 'ORD001', 2, 3, '2025-12-18 13:18:59.310799', 'Aktif', NULL, 0.10),
	(2, 'ORD002', 2, 5, '2025-12-18 13:23:59.310799', 'Aktif', NULL, 0.10),
	(3, 'ORD003', 2, 7, '2025-12-18 13:28:59.310799', 'Aktif', NULL, 0.10);
/*!40000 ALTER TABLE "tabel_pesanan" ENABLE KEYS */;

-- Dumping structure for table public.tabel_resep
CREATE TABLE IF NOT EXISTS "tabel_resep" (
	"id_resep" SERIAL NOT NULL,
	"id_menu" INTEGER NOT NULL,
	"id_bahan" INTEGER NOT NULL,
	"jumlah_dibutuhkan" NUMERIC(12,2) NOT NULL,
	PRIMARY KEY ("id_resep"),
	CONSTRAINT "tabel_resep_id_bahan_fkey" FOREIGN KEY ("id_bahan") REFERENCES "tabel_bahan_baku" ("id_bahan") ON UPDATE NO ACTION ON DELETE CASCADE,
	CONSTRAINT "tabel_resep_id_menu_fkey" FOREIGN KEY ("id_menu") REFERENCES "tabel_menu" ("id_menu") ON UPDATE NO ACTION ON DELETE CASCADE
);

-- Dumping data for table public.tabel_resep: -1 rows
/*!40000 ALTER TABLE "tabel_resep" DISABLE KEYS */;
INSERT INTO "tabel_resep" ("id_resep", "id_menu", "id_bahan", "jumlah_dibutuhkan") VALUES
	(1, 1, 2, 0.20),
	(2, 1, 5, 2.00),
	(3, 1, 6, 0.05),
	(4, 2, 3, 0.15),
	(5, 2, 4, 0.10),
	(6, 2, 6, 0.03),
	(7, 3, 1, 0.25),
	(8, 3, 6, 0.05),
	(9, 4, 1, 0.15),
	(10, 4, 4, 0.10),
	(11, 4, 6, 0.03);
/*!40000 ALTER TABLE "tabel_resep" ENABLE KEYS */;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
