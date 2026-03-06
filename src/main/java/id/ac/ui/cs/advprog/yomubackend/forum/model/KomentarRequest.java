package id.ac.ui.cs.advprog.yomubackend.forum.model;

import lombok.Data;

@Data
public class KomentarRequest {
    private Long bacaanId;
    private Long pelajarId;
    private String isi;

    // Jika null, berarti ini komentar utama.
    // Jika ada isinya, berarti ini balasan untuk komentar dengan ID tersebut.
    private Long parentId;
}