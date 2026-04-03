package com.financerpg.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "bosses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Boss {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @Column(nullable = false)
    @Builder.Default
    private String name = "Boss ประจำเดือน";

    @Column(nullable = false)
    private BigDecimal targetAmount;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal currentSaved = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private int hpMax = 100;

    @Column(nullable = false)
    @Builder.Default
    private int hpCurrent = 100;

    @Column(nullable = false)
    @Builder.Default
    private int level = 1;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BossStatus status = BossStatus.ACTIVE;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum BossStatus {
        ACTIVE, WIN, LOSE
    }
}