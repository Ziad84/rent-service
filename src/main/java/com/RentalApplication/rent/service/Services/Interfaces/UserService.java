package com.RentalApplication.rent.service.Services.Interfaces;

import com.RentalApplication.rent.service.DTO.RegisterUserDTO;
import com.RentalApplication.rent.service.DTO.ResponseDTO;
import com.RentalApplication.rent.service.DTO.UserDTO;

import java.util.List;

public interface UserService {

    List<UserDTO> getAllUsersAsAdmin();
    UserDTO getUserByIdAsAdmin(Integer id);
    void deleteUserAsAdmin(Integer id);

    ResponseDTO registerUser( RegisterUserDTO registerDTO);
    ResponseDTO login(String username, String password);
    UserDTO updateUser( Integer id, UserDTO userDTO);
    void deleteUser(Integer id);



}