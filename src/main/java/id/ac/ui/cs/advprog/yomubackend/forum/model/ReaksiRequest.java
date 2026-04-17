package id.ac.ui.cs.advprog.yomubackend.forum.model;

import lombok.Data;

/**
 * DTO untuk request memberikan reaksi pada komentar.
 */
@Data
public class ReaksiRequest {

    /** Jenis reaksi yang akan diberikan. */
    private String jenisReaksi;
}

