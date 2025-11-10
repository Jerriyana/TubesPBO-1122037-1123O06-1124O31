====================================================================
    PANDUAN LENGKAP STRUKTUR FILE PROYEK SISTEM RESTORAN
====================================================================

📂 STRUKTUR FOLDER YANG HARUS DIBUAT:
--------------------------------------------------------------------

your-project-root/
│
├── src/
│   └── com/
│       └── restaurant/
│           │
│           ├── Main.java
│           │
│           ├── model/
│           │   ├── Karyawan.java          (baris 1-43 dari Models)
│           │   ├── Menu.java              (baris 45-62 dari Models)
│           │   ├── Admin.java             (baris 66-78 dari Models)
│           │   ├── Kasir.java             (baris 80-99 dari Models)
│           │   ├── Koki.java              (baris 101-124 dari Models)
│           │   ├── Makanan.java           (baris 128-141 dari Models)
│           │   ├── Minuman.java           (baris 143-158 dari Models)
│           │   ├── Pesanan.java           (baris 162-217 dari Models)
│           │   ├── ItemPesanan.java       (baris 219-255 dari Models)
│           │   ├── BahanBaku.java         (baris 259-285 dari Models)
│           │   ├── BahanMenu.java         (baris 287-301 dari Models)
│           │   ├── AbsensiKaryawan.java   (baris 303-332 dari Models)
│           │   │
│           │   ├── factory/
│           │   │   ├── KaryawanFactory.java
│           │   │   └── MenuFactory.java
│           │   │
│           │   └── repository/
│           │       ├── KaryawanRepository.java          (interface)
│           │       ├── KaryawanRepositoryImpl.java      (implementation)
│           │       ├── MenuRepository.java              (interface)
│           │       ├── MenuRepositoryImpl.java          (implementation)
│           │       ├── PesananRepository.java           (interface)
│           │       ├── PesananRepositoryImpl.java       (implementation)
│           │       ├── BahanBakuRepository.java         (interface)
│           │       └── BahanBakuRepositoryImpl.java     (implementation)
│           │
│           ├── controller/
│           │   ├── AdminController.java
│           │   ├── KasirController.java
│           │   └── KokiController.java
│           │
│           └── view/
│               ├── LoginView.java
│               ├── AdminView.java
│               ├── KasirView.java
│               └── KokiView.java
│
└── README.md


====================================================================
    OPSI 1: PISAH FILE (RECOMMENDED untuk Production)
====================================================================

Buat file terpisah sesuai struktur di atas, setiap class dalam file sendiri.

KEUNTUNGAN:
✅ Mudah maintain
✅ Sesuai best practice Java
✅ Mudah untuk tracking di Git
✅ Mudah untuk debugging

CARA COMPILE:
cd your-project-root
javac -d bin src/com/restaurant/*.java src/com/restaurant/**/*.java
java -cp bin com.restaurant.Main


====================================================================
    OPSI 2: GABUNG FILE (untuk Testing Cepat)
====================================================================

Jika Anda ingin testing cepat TANPA setup banyak folder, gabungkan jadi
SATU FILE dengan cara berikut:

FILE: RestaurantSystem.java
--------------------------------------------------------------------

// Hapus semua "package com.restaurant.*;" dari semua class
// Gabungkan semua dalam urutan:

1. Import statements (paling atas)
2. Semua Model Classes (Karyawan, Admin, Kasir, dst)
3. Factory Classes
4. Repository Interfaces & Implementations
5. Controller Classes
6. View Classes
7. Main Class (paling bawah)

// Class yang bersifat public hanya:
// - Main (class utama)
// - Semua yang lain jadikan class biasa (non-public)

CARA COMPILE & RUN:
javac RestaurantSystem.java
java Main


====================================================================
    OPSI 3: MENGGUNAKAN IDE (PALING MUDAH)
====================================================================

### VSCODE:
1. Install Extension "Extension Pack for Java"
2. Buat folder baru
3. Klik kanan → "New Java Project"
4. Pilih "No build tools"
5. Copy-paste semua file sesuai struktur
6. Klik Run pada Main.java

### INTELLIJ IDEA:
1. File → New → Project
2. Pilih Java
3. Buat struktur package: com.restaurant
4. Copy-paste semua file
5. Klik Run Main.java

### ECLIPSE:
1. File → New → Java Project
2. Klik kanan src → New → Package: com.restaurant
3. Copy-paste semua file
4. Run As → Java Application (Main.java)


====================================================================
    TIPS PENTING!
====================================================================

1. PERHATIKAN PACKAGE DECLARATION:
   - Setiap file HARUS ada package declaration di baris pertama
   - Harus sesuai dengan lokasi folder
   
   Contoh:
   File: src/com/restaurant/model/Admin.java
   Baris 1 harus: package com.restaurant.model;

2. PERHATIKAN IMPORT:
   - File di package berbeda butuh import
   - Contoh di Controller.java:
     import com.restaurant.model.*;
     import com.restaurant.model.factory.*;
     import com.restaurant.model.repository.*;

3. VISIBILITY MODIFIER:
   - Di file Models, saya sudah buat semua class tanpa "public"
   - Ini karena satu file .java hanya boleh ada SATU public class
   - Jika mau pisah file, tambahkan "public" di setiap class

4. DEPENDENCY ANTAR FILE:
   Main.java → View → Controller → Repository → Model
   
   Compile dari Model dulu, baru ke atas:
   javac model/*.java
   javac model/factory/*.java
   javac model/repository/*.java
   javac controller/*.java
   javac view/*.java
   javac Main.java


====================================================================
    TESTING CHECKLIST
====================================================================

Setelah compile berhasil:

□ Test Login Admin
□ Test Login Kasir
□ Test Login Koki
□ Test Login Gagal (password salah)
□ Test semua fitur Admin (6 fitur)
□ Test semua fitur Kasir (5 fitur)
□ Test semua fitur Koki (3 fitur)


====================================================================
    JIKA ADA ERROR
====================================================================

ERROR: Cannot find symbol
→ Cek import statement
→ Cek package declaration
→ Cek typo nama class/method

ERROR: Package does not exist
→ Pastikan struktur folder benar
→ Compile dari root folder

ERROR: Class not found
→ Cek CLASSPATH
→ Jalankan dari folder yang benar

ERROR: JOptionPane not working
→ Pastikan ada GUI environment
→ Jangan run via SSH/Terminal server


====================================================================
    NEXT STEPS SETELAH TESTING
====================================================================

Setelah semua fitur berjalan lancar:

1. ✅ Tambahkan Javadoc ke semua class & method
2. ✅ Setup Git repository
3. ✅ Buat database MySQL
4. ✅ Implementasikan JDBC di Repository
5. ✅ Test dengan database real
6. ✅ (Opsional) Migrate ke JavaFX untuk GUI yang lebih baik


====================================================================
    SUPPORT
====================================================================

Jika ada masalah, cek:
1. README.md untuk skenario testing
2. Struktur package sudah benar
3. Semua file sudah di-compile
4. JDK version minimal Java 8


Selamat coding! 🚀
====================================================================