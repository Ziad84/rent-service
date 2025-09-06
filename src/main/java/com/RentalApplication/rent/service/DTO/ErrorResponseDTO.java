package com.RentalApplication.rent.service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class ErrorResponseDTO {

    private final int status;
    private final String message;

}
