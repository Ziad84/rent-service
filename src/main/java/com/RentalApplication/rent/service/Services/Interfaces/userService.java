package com.RentalApplication.rent.service.Services.Interfaces;

import com.RentalApplication.rent.service.dto.adminDTO;
import com.RentalApplication.rent.service.dto.clientDTO;
import com.RentalApplication.rent.service.dto.userDTO;

import java.util.List;
import java.util.UUID;

public interface userService {

    // --- Admin operations ---
    List<userDTO> getAllUsersAsAdmin();
    userDTO getUserByIdAsAdmin(UUID id);
    void deleteUserAsAdmin(UUID id);

    // --- Client/Owner operations ---
    userDTO registerUser(userDTO usersDTO);       // register as OWNER or CLIENT
    String login(String username, String password); // returns JWT token
    userDTO updateUser(UUID id, userDTO usersDTO);   // user updates their profile
    void deleteUser(UUID id);                       // user deletes their account

}