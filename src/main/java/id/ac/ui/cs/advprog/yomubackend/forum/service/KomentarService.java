package id.ac.ui.cs.advprog.yomubackend.forum.service;

import id.ac.ui.cs.advprog.yomubackend.forum.model.Komentar;
import id.ac.ui.cs.advprog.yomubackend.forum.model.KomentarRequest;
import java.util.List;

/**
 * Service interface untuk mengelola logika bisnis forum diskusi.
 */
public interface KomentarService {

    /**
     * Membuat komentar baru atau balasan komentar.
     *
     * @param request Data request pembuatan komentar.
     * @return Komentar yang berhasil disimpan.
     */
    Komentar createKomentar(KomentarRequest request);

    /**
     * Mengedit isi komentar yang sudah ada.
     *
     * @param id ID komentar yang akan diedit.
     * @param pelajarId ID pelajar yang meminta pengeditan (untuk otorisasi).
     * @param isiBaru Teks komentar yang baru.
     * @return Komentar yang telah diperbarui.
     */
    Komentar updateKomentar(Long id, String pelajarId, String isiBaru);

    /**
     * Menghapus komentar dari sistem.
     *
     * @param id ID komentar yang akan dihapus.
     * @param pelajarId ID pelajar yang meminta penghapusan (untuk otorisasi).
     */
    void deleteKomentar(Long id, String pelajarId);

    /**
     * Mengambil daftar komentar utama (parent) untuk sebuah bacaan.
     *
     * @param bacaanId ID bacaan terkait.
     * @return List komentar yang tidak memiliki parent.
     */
    List<Komentar> getKomentarUtamaByBacaanId(Long bacaanId);

    void toggleReaksi(Long komentarId, String pelajarId, String jenisReaksi);
}