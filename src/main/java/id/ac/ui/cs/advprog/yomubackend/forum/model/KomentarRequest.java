package id.ac.ui.cs.advprog.yomubackend.forum.model;

import lombok.Data;

/**
 * DTO untuk request pembuatan komentar atau balasan.
 */
@Data
public class KomentarRequest {

    /**
     * ID bacaan terkait.
     */
    private Long bacaanId;

    /**
     * ID pelajar yang mengirimkan komentar.
     */
    private Long pelajarId;

    /**
     * Isi teks komentar.
     */
    private String isi;

    /**
     * ID komentar parent jika request ini adalah balasan.
     */
    private Long parentId;
}
