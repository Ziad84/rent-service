package com.RentalApplication.rent.service.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApartmentsDTO {
    private Integer id;
    private String title;
    private Integer monthlyRent;
    private Integer roomsNumber;
    private Integer  ownerId;
    private Integer  clientId;
    private LocalDateTime rentedAt;
    private Boolean isDeleted;
}
