package com.RentalApplication.rent.service.Entity;


import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "appartments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appartments {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Users owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Users client;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "monthly_rent", nullable = false)
    private Integer monthlyRent;

    @Column(name = "rooms_number")
    private Integer roomsNumber;

    @Column(name = "rented_at")
    private LocalDateTime rentedAt;


    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isDeleted = false;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

}