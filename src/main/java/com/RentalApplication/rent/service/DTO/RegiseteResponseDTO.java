package com.RentalApplication.rent.service.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class RegiseteResponseDTO {
    private String message;
    private boolean success;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> errors;


}
