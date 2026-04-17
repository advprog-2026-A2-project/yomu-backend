package id.ac.ui.cs.advprog.yomubackend.forum.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entitas yang merepresentasikan reaksi (emoji/upvote/downvote) pada
 * komentar.
 */
@Entity
@Table(name = "reaksi_komentar")
@Getter
@Setter
@NoArgsConstructor
public class Reaksi {

    /** ID unik reaksi. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ID pelajar yang memberikan reaksi. */
    @Column(nullable = false)
    private String pelajarId;

    /** Jenis reaksi (UPVOTE, DOWNVOTE, atau emoji). */
    @Column(nullable = false)
    private String jenisReaksi;

    /** Komentar yang direaksi. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "komentar_id", nullable = false)
    @JsonIgnore
    private Komentar komentar;
}

