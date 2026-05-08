package id.ac.ui.cs.advprog.yomubackend.clan.model;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.clan.enums.TierType;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
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
    /** Buat Clan dgn Tier terbawah terlebih dahulu. */
    private TierType tierType = Enum.valueOf(TierType.class, "BRONZE");

    /** Ketua clan. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "ketua_clan_id", nullable = false)
    private User ketuaClan;

    /** Anggota clan. */
    @ManyToMany
    @JoinTable(
            name = "clan_anggota",
            joinColumns = @JoinColumn(name = "clan_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> anggota = new ArrayList<>();

    public void jadikanKetuaSebagaiAnggotaAwal(User user) {
        ketuaClan = user;
        anggota.clear();
        anggota.add(user);
    }
}
