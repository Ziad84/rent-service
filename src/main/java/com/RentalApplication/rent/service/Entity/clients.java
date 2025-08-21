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
@Table(name = "clients")
public class clients {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private UUID id;

   /* @ManyToOne
    @JoinColumn(name = "apartment_id", nullable = true)
    @Column(name = "Appartment_id")
    private Apartment apartment;
*/
    @Column(name = "Client_name")
    private String clientname;

    @Column(name = "phone_number")
    private String phonenumber;

    @Column(name = "email")
    private String email;

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
