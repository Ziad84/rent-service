package com.RentalApplication.rent.service.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDTO {
    private Integer id;
    private String name;
    private String email;
    private String phoneNumber;
    private String roleName;
    private Boolean isDeleted;


}