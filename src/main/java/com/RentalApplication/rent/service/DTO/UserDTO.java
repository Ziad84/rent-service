package com.RentalApplication.rent.service.DTO;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Integer id;

    @NotBlank(message = "Name is required")
    private String name;


    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Invalid phone number format or it's already used")
    private String phoneNumber;


    private String roleName;
    private boolean isDeleted;



}