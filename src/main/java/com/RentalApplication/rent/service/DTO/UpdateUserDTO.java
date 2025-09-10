package com.RentalApplication.rent.service.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO {

    private int status;
    private String message;
    private String path;
    private LocalDateTime timestamp;
}
