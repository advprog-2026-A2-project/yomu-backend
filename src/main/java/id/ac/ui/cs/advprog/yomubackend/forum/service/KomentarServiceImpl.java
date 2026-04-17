package id.ac.ui.cs.advprog.yomubackend.forum.service;

import id.ac.ui.cs.advprog.yomubackend.forum.model.Komentar;
import id.ac.ui.cs.advprog.yomubackend.forum.model.KomentarRequest;
import id.ac.ui.cs.advprog.yomubackend.forum.model.Reaksi;
import id.ac.ui.cs.advprog.yomubackend.forum.repository.KomentarRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import id.ac.ui.cs.advprog.yomubackend.forum.repository.ReaksiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementasi dari antarmuka {@link KomentarService}.
 * Menangani logika bisnis untuk forum komentar, termasuk pembuatan,
 * pengeditan, penghapusan, dan pengambilan data komentar bersarang.
 */
@Service
@RequiredArgsConstructor
public class KomentarServiceImpl implements KomentarService {

    /**
     * Repository untuk mengakses data entitas Komentar dari database.
     */
    private final KomentarRepository komentarRepository;
    private final ReaksiRepository reaksiRepository;

    @Override
    public Komentar createKomentar(final KomentarRequest request) {
        final Komentar komentar = new Komentar();
        komentar.setBacaanId(request.getBacaanId());

        // pelajarId diset menggunakan tipe String (UUID)
        komentar.setPelajarId(request.getPelajarId());
        komentar.setIsi(request.getIsi());

        // Memeriksa apakah komentar ini adalah sebuah balasan (nested)
        if (request.getParentId() != null) {
            final Komentar parent = komentarRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Komentar parent tidak ditemukan"));
            komentar.setParent(parent);
        }

        return komentarRepository.save(komentar);
    }

    @Override
    public Komentar updateKomentar(final Long id, final String pelajarId, final String isiBaru) {
        final Komentar komentar = komentarRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Komentar tidak ditemukan"));

        // Memastikan bahwa hanya pemilik komentar yang dapat melakukan pengeditan.
        // Karena pelajarId bertipe String, kita menggunakan metode .equals().
        if (!komentar.getPelajarId().equals(pelajarId)) {
            throw new SecurityException("Anda tidak berhak mengedit komentar ini");
        }

        komentar.setIsi(isiBaru);
        return komentarRepository.save(komentar);
    }

    @Override
    public void deleteKomentar(final Long id, final String pelajarId) {
        final Komentar komentar = komentarRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Komentar tidak ditemukan"));

        // Memastikan bahwa hanya pemilik komentar yang dapat melakukan penghapusan.
        // Karena pelajarId bertipe String, kita menggunakan metode .equals().
        if (!komentar.getPelajarId().equals(pelajarId)) {
            throw new SecurityException("Anda tidak berhak menghapus komentar ini");
        }

        komentarRepository.delete(komentar);
    }

    @Override
    public List<Komentar> getKomentarUtamaByBacaanId(final Long bacaanId) {
        return komentarRepository.findByBacaanIdAndParentIsNullOrderByCreatedAtAsc(bacaanId);
    }

    @Override
    public void toggleReaksi(final Long komentarId, final String pelajarId, final String jenisReaksi) {
        final Komentar komentar = komentarRepository.findById(komentarId)
                .orElseThrow(() -> new IllegalArgumentException("Komentar tidak ditemukan"));

        // Logika 1: Upvote dan Downvote (Saling menimpa / Mutually Exclusive)
        if (jenisReaksi.equals("UPVOTE") || jenisReaksi.equals("DOWNVOTE")) {
            Optional<Reaksi> existingVote = reaksiRepository
                    .findByKomentarAndPelajarIdAndJenisReaksiIn(komentar, pelajarId, Arrays.asList("UPVOTE", "DOWNVOTE"));

            if (existingVote.isPresent()) {
                Reaksi r = existingVote.get();
                if (r.getJenisReaksi().equals(jenisReaksi)) {
                    reaksiRepository.delete(r); // Batal vote jika diklik tombol yang sama
                } else {
                    r.setJenisReaksi(jenisReaksi); // Switch vote jika berbeda
                    reaksiRepository.save(r);
                }
            } else {
                Reaksi r = new Reaksi();
                r.setKomentar(komentar);
                r.setPelajarId(pelajarId);
                r.setJenisReaksi(jenisReaksi);
                reaksiRepository.save(r);
            }
        }
        // Logika 2: Reaksi Emoji (Bisa lebih dari 1 emoji berbeda, klik lagi untuk batal)
        else {
            Optional<Reaksi> existingEmoji = reaksiRepository
                    .findByKomentarAndPelajarIdAndJenisReaksi(komentar, pelajarId, jenisReaksi);

            if (existingEmoji.isPresent()) {
                reaksiRepository.delete(existingEmoji.get()); // Batal emoji
            } else {
                Reaksi r = new Reaksi();
                r.setKomentar(komentar);
                r.setPelajarId(pelajarId);
                r.setJenisReaksi(jenisReaksi);
                reaksiRepository.save(r); // Tambah emoji
            }
        }
    }
}
