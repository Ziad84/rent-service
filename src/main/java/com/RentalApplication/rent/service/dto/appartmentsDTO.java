package com.RentalApplication.rent.service.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class appartmentsDTO {
    private UUID id;
    private String title;
    private Integer monthlyRent;
    private Integer roomsNumber;
    private UUID  ownerId;
    private UUID  clientId;
    private LocalDateTime rentedAt;
    private Boolean isDeleted;
}
