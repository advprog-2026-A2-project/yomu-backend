package id.ac.ui.cs.advprog.yomubackend.forum.service;

import id.ac.ui.cs.advprog.yomubackend.forum.model.Komentar;
import id.ac.ui.cs.advprog.yomubackend.forum.model.KomentarRequest;
import id.ac.ui.cs.advprog.yomubackend.forum.model.Reaksi;
import id.ac.ui.cs.advprog.yomubackend.forum.repository.KomentarRepository;
import id.ac.ui.cs.advprog.yomubackend.forum.repository.ReaksiRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementasi dari antarmuka {@link KomentarService}.
 * Menangani logika bisnis untuk forum komentar, termasuk pembuatan,
 * pengeditan, penghapusan, dan pengambilan data komentar bersarang.
 */
@Service
@RequiredArgsConstructor
public final class KomentarServiceImpl implements KomentarService {

    /** Repository untuk akses data Komentar. */
    private final KomentarRepository komentarRepository;

    /** Repository untuk akses data Reaksi. */
    private final ReaksiRepository reaksiRepository;

    /**
     * Membuat komentar baru atau balasan.
     *
     * @param request data request untuk membuat komentar
     * @return komentar yang berhasil disimpan
     */
    @Override
    public Komentar createKomentar(final KomentarRequest request) {
        final Komentar komentar = new Komentar();
        komentar.setBacaanId(request.getBacaanId());
        komentar.setPelajarId(request.getPelajarId());
        komentar.setIsi(request.getIsi());

        if (request.getParentId() != null) {
            final Komentar parent = komentarRepository
                    .findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Komentar parent tidak ditemukan"));
            komentar.setParent(parent);
        }

        return komentarRepository.save(komentar);
    }

    /**
     * Mengupdate komentar yang sudah ada.
     *
     * @param id ID komentar yang akan diupdate
     * @param pelajarId ID pelajar yang melakukan update
     * @param isiBaru isi komentar yang baru
     * @return komentar yang sudah diupdate
     */
    @Override
    public Komentar updateKomentar(final Long id,
            final String pelajarId, final String isiBaru) {
        final Komentar komentar = komentarRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Komentar tidak ditemukan"));

        if (!komentar.getPelajarId().equals(pelajarId)) {
            throw new SecurityException(
                    "Anda tidak berhak mengedit komentar ini");
        }

        komentar.setIsi(isiBaru);
        return komentarRepository.save(komentar);
    }

    /**
     * Menghapus komentar dari sistem.
     *
     * @param id ID komentar yang akan dihapus
     * @param pelajarId ID pelajar yang melakukan penghapusan
     */
    @Override
    public void deleteKomentar(final Long id, final String pelajarId) {
        final Komentar komentar = komentarRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Komentar tidak ditemukan"));

        if (!komentar.getPelajarId().equals(pelajarId)) {
            throw new SecurityException(
                    "Anda tidak berhak menghapus komentar ini");
        }

        komentarRepository.delete(komentar);
    }

    /**
     * Mengambil semua komentar utama untuk suatu bacaan.
     *
     * @param bacaanId ID bacaan
     * @return daftar komentar utama
     */
    @Override
    public List<Komentar> getKomentarUtamaByBacaanId(
            final Long bacaanId) {
        return komentarRepository
                .findByBacaanIdAndParentIsNullOrderByCreatedAtAsc(
                        bacaanId);
    }

    /**
     * Menambah atau menghapus reaksi pada komentar.
     *
     * @param komentarId ID komentar yang akan direaksi
     * @param pelajarId ID pelajar yang memberikan reaksi
     * @param jenisReaksi jenis reaksi (UPVOTE, DOWNVOTE, atau emoji)
     */
    @Override
    public void toggleReaksi(final Long komentarId,
            final String pelajarId, final String jenisReaksi) {
        final Komentar komentar = komentarRepository
                .findById(komentarId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Komentar tidak ditemukan"));

        if (jenisReaksi.equals("UPVOTE")
                || jenisReaksi.equals("DOWNVOTE")) {
            Optional<Reaksi> existingVote = reaksiRepository
                    .findByKomentarAndPelajarIdAndJenisReaksiIn(
                            komentar, pelajarId,
                            Arrays.asList("UPVOTE", "DOWNVOTE"));

            if (existingVote.isPresent()) {
                Reaksi r = existingVote.get();
                if (r.getJenisReaksi().equals(jenisReaksi)) {
                    reaksiRepository.delete(r);
                } else {
                    r.setJenisReaksi(jenisReaksi);
                    reaksiRepository.save(r);
                }
            } else {
                Reaksi r = new Reaksi();
                r.setKomentar(komentar);
                r.setPelajarId(pelajarId);
                r.setJenisReaksi(jenisReaksi);
                reaksiRepository.save(r);
            }
        } else {
            Optional<Reaksi> existingEmoji = reaksiRepository
                    .findByKomentarAndPelajarIdAndJenisReaksi(
                            komentar, pelajarId, jenisReaksi);

            if (existingEmoji.isPresent()) {
                reaksiRepository.delete(existingEmoji.get());
            } else {
                Reaksi r = new Reaksi();
                r.setKomentar(komentar);
                r.setPelajarId(pelajarId);
                r.setJenisReaksi(jenisReaksi);
                reaksiRepository.save(r);
            }
        }
    }
}

