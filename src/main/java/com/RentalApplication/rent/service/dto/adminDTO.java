package com.RentalApplication.rent.service.dto;

import jdk.jshell.Snippet;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class adminDTO {
    private UUID id;
    private String name;
    private String email;
    private String phoneNumber;
    private String roleName;
    private Boolean isDeleted;


}