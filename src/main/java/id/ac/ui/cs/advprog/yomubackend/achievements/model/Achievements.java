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

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String achievementName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String achievementDescription;

    /** Tipe kondisi yang harus dipenuhi pelajar */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MilestoneType milestoneType;

    /** Nilai target (misal: 10 untuk "selesaikan 10 bacaan") */
    @Column(nullable = false)
    private int milestoneTargetValue;

    @Column
    private String iconUrl;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void updateDetails(String name, String description, String iconUrl) {
        this.achievementName = name;
        this.achievementDescription = description;
        this.iconUrl = iconUrl;
    }

    public boolean isSatisfiedBy(int currentProgress) {
        return currentProgress >= this.milestoneTargetValue;
    }
}

