package com.RentalApplication.rent.service.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Roles role;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

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

        createdBy = "System";
        updatedBy = "System";

    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
        updatedBy = "System";
    }


}
