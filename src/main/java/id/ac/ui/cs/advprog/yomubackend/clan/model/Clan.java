package id.ac.ui.cs.advprog.yomubackend.clan.model;

import id.ac.ui.cs.advprog.yomubackend.achievements.enums.MilestoneType;
import id.ac.ui.cs.advprog.yomubackend.clan.enums.TierType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "clan")
public class Clan {
    /** ID dari clan. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nama clan. */
    @Column(nullable = false)
    private String nama;

    /** Tipe tier. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)

    /** Buat Clan dgn Tier terbawah terlebih dahulu */
    private TierType tierType = Enum.valueOf(TierType.class, "BRONZE");
}
