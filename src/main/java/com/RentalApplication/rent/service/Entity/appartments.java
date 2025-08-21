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
public class appartments {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private UUID id;

/*
    @ManyToOne
    @JoinColumn(name = "property_manager_id")
    private PropertyOwner owner;
*/

    @Column(name = "Title")
    private String title;

    @Column(name = "rooms_number")
    private int roomsnumber;

    @Column(name="monthly_rent")
    private int monthlyrent;

    @Column(name = "Rented_at")
    private LocalDateTime RentedAt;

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


}