package com.RentalApplication.rent.service.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "property_owner")
public class propertyowner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private UUID id;

    @Column(name = "Owner_name")
    private String ownerName;

    @Column(name = "Owner_email", unique = true)
    private String ownerEmail;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "is_deleted")
    private String isdeleted;

    /*
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Apartment> apartments;
*/
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;

    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

    }

}
