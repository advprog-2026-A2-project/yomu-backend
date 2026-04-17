package id.ac.ui.cs.advprog.yomubackend.achievements.model;

import id.ac.ui.cs.advprog.yomubackend.achievements.enums.MilestoneType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "achievements")
@Getter @Setter
public class Achievements {

    /** Id Achievements */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private String id;

    /** Nama Achievements */
    @Column(nullable = false)
    private String achievementName;

    /** Deskripsi Achievements */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String achievementDescription;

    /** Tipe kondisi yang harus dipenuhi pelajar */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MilestoneType milestoneType;

    /** Nilai target (misal: 10 untuk "selesaikan 10 bacaan") */
    @Column(nullable = false)
    private int milestoneTargetValue;

    /** Gambar Achievements */
    @Column
    private String iconUrl;

    /** Waktu Achievements dibuat */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Waktu Achievements diupdate */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /** Update nama dan deskripsi achievement oleh Admin */
    public void updateDetails(String name, String description, String iconUrl) {
        this.achievementName = name;
        this.achievementDescription = description;
        this.iconUrl = iconUrl;
    }

    /** Cek apakah progress yang diberikan sudah memenuhi milestone */
    public boolean isSatisfiedBy(int currentProgress) {
        return currentProgress >= this.milestoneTargetValue;
    }
}

