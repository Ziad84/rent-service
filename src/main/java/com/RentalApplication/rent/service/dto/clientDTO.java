package com.RentalApplication.rent.service.dto;


import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class clientDTO {
    private UUID id;
    private String name;
    private String email;
}