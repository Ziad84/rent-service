package com.RentalApplication.rent.service.DTO;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentRequestDTO {

    private Integer apartmentId;
}
