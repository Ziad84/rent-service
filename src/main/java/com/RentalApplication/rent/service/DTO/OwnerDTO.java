package com.RentalApplication.rent.service.DTO;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerDTO {
    private Integer id;
    private String name;
    private String email;
}
