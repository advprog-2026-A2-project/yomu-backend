package id.ac.ui.cs.advprog.yomubackend.Bacaan.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.Bacaan;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.Pertanyaan;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.BacaanRepository;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.PertanyaanRepository;
import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;

/** Class untuk seeding data dummy. */
@Component
public class DataSeeder implements CommandLineRunner {

    /** Repository untuk Bacaan. */
    @Autowired
    private BacaanRepository bacaanRepository;

    /** Repository untuk Pertanyaan. */
    @Autowired
    private PertanyaanRepository pertanyaanRepository;

    /** Repository untuk User. */
    @Autowired
    private UserRepository userRepository;

    /** Password encoder untuk mengenkripsi password. */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Menjalankan proses seeding data saat aplikasi pertama kali dijalankan.
     *
     * @param args Argumen command line.
     * @throws Exception Jika terjadi error saat akses database.
     */
    @Override
    public void run(final String... args) throws Exception {
        seedAdminUser();
        seedReadingData();
    }

    /**
     * Membuat akun admin jika belum ada.
     */
    private void seedAdminUser() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            final User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@yomu.local");
            admin.setPhoneNumber("081234567890");
            admin.setDisplayName("Administrator");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
            System.out.println("✅ Admin user berhasil dibuat!");
            System.out.println("   Username: admin");
            System.out.println("   Password: admin123");
        }
    }

    /**
     * Membuat data bacaan dan pertanyaan jika belum ada.
     */
    private void seedReadingData() {
        if (bacaanRepository.count() == 0) {
            final Bacaan teks1 = new Bacaan();
            teks1.setJudul("Sejarah Kemerdekaan Indonesia");
            teks1.setKonten("Proklamasi Kemerdekaan Indonesia dilaksanakan "
                    + "pada hari Jumat, 17 Agustus 1945 tahun Masehi, atau "
                    + "tanggal 17 Agustus 2605 menurut tahun Jepang. "
                    + "Teks proklamasi dibacakan oleh Soekarno didampingi "
                    + "Mohammad Hatta.");
            bacaanRepository.save(teks1);

            final Pertanyaan soal1 = new Pertanyaan();
            soal1.setSoal("Kapan Proklamasi Kemerdekaan Indonesia "
                    + "dilaksanakan?");
            soal1.setOpsiA("17 Agustus 1945");
            soal1.setOpsiB("18 Agustus 1945");
            soal1.setOpsiC("17 Agustus 2605");
            soal1.setOpsiD("Jawaban A dan C benar");
            soal1.setKunciJawaban("A");
            soal1.setBacaan(teks1);
            pertanyaanRepository.save(soal1);

            final Bacaan teks2 = new Bacaan();
            teks2.setJudul("Mengenal Hewan Mamalia");
            teks2.setKonten("Mamalia adalah kelas hewan vertebrata "
                    + "yang dicirikan oleh adanya kelenjar susu.");
            bacaanRepository.save(teks2);

            System.out.println("✅ Data bacaan & Kuis berhasil dimasukkan!");
        }
    }
}
