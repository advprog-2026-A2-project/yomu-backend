package id.ac.ui.cs.advprog.yomubackend.Bacaan.config;

import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.Bacaan;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.Pertanyaan;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.BacaanRepository;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.PertanyaanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** Class untuk mengisi data awal (dummy) ke database. */
@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired private BacaanRepository bacaanRepository;
    @Autowired private PertanyaanRepository pertanyaanRepository;

    /**
     * Menjalankan proses seeding data saat aplikasi pertama kali dijalankan.
     *
     * @param args Argumen command line.
     * @throws Exception Jika terjadi error saat akses database.
     */
    @Override
    public void run(final String... args) throws Exception {
        if (bacaanRepository.count() == 0) {
            final Bacaan teks1 = new Bacaan();
            teks1.setJudul("Sejarah Kemerdekaan Indonesia");
            teks1.setKonten("Proklamasi Kemerdekaan Indonesia dilaksanakan pada hari Jumat, "
                    + "17 Agustus 1945 tahun Masehi, atau tanggal 17 Agustus 2605 menurut tahun Jepang. "
                    + "Teks proklamasi dibacakan oleh Soekarno didampingi Mohammad Hatta.");
            bacaanRepository.save(teks1);

            final Pertanyaan soal1 = new Pertanyaan();
            soal1.setSoal("Kapan Proklamasi Kemerdekaan Indonesia dilaksanakan?");
            soal1.setOpsiA("17 Agustus 1945");
            soal1.setOpsiB("18 Agustus 1945");
            soal1.setOpsiC("17 Agustus 2605");
            soal1.setOpsiD("Jawaban A dan C benar");
            soal1.setKunciJawaban("A");
            soal1.setBacaan(teks1);
            pertanyaanRepository.save(soal1);

            final Bacaan teks2 = new Bacaan();
            teks2.setJudul("Mengenal Hewan Mamalia");
            teks2.setKonten("Mamalia adalah kelas hewan vertebrata yang dicirikan oleh "
                    + "adanya kelenjar susu.");
            bacaanRepository.save(teks2);

            System.out.println("✅ Data bacaan & Kuis berhasil dimasukkan!");
        }
    }
}
// Baris kosong di sini penting untuk Checkstyle!