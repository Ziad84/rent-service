package com.RentalApplication.rent.service.Services.Implementation;


import com.RentalApplication.rent.service.Entity.appartments;
import com.RentalApplication.rent.service.Entity.users;
import com.RentalApplication.rent.service.Repository.AppartmentsRepository;
import com.RentalApplication.rent.service.Repository.rolesRepository;
import com.RentalApplication.rent.service.Repository.usersRepository;
import com.RentalApplication.rent.service.Security.JWTService;
import com.RentalApplication.rent.service.Services.Interfaces.userService;
import com.RentalApplication.rent.service.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class userServiceImpl implements userService {

    private final usersRepository userRepository;
    private final rolesRepository roleRepository;
    private final AppartmentsRepository apartmentsRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService; //

    // Helper: map entity -> DTO
    private userDTO mapToDTO(users user) {
        return userDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .roleName(user.getRole().getName())
                .isDeleted(user.getIsDeleted())
                .build();
    }

    // --- Admin operations ---
    @Override
    public List<userDTO> getAllUsersAsAdmin() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public userDTO getUserByIdAsAdmin(UUID id) {
        users user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDTO(user);
    }

    @Override
    public void deleteUserAsAdmin(UUID id) {
        users user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // if OWNER -> delete his apartments too
        if ("OWNER".equals(user.getRole().getName())) {
            List<appartments> apartment = apartmentsRepository.findByOwner_Id(user.getId());
            apartmentsRepository.deleteAll(apartment);
        }

        user.setIsDeleted(true);
        userRepository.save(user);
    }

    // --- Client/Owner operations ---
    @Override
    public userDTO registerUser(userDTO uDTO) {
        users newUser = users.builder()
                .name(uDTO.getName())
                .email(uDTO.getEmail())
                .phoneNumber(uDTO.getPhoneNumber())
                //.password(passwordEncoder.encode(uDTO.getPassword())) // encrypt password
                .role(roleRepository.findByName(uDTO.getRoleName()))
                .isDeleted(false)
                .build();

        return mapToDTO(userRepository.save(newUser));
    }

    @Override
    public String login(String username, String password) {
        users user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtService.generateToken(user);
        // generate JWT token


    }

    @Override
    public userDTO updateUser(UUID id, userDTO uDTO) {
        users user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(uDTO.getName());
        user.setEmail(uDTO.getEmail());
        user.setPhoneNumber(uDTO.getPhoneNumber());

        if (uDTO.getPassword() != null && !uDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(uDTO.getPassword()));
        }

        return mapToDTO(userRepository.save(user));
    }

    @Override
    public void deleteUser(UUID id) {
        users user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // if OWNER -> delete his apartments too
        if ("OWNER".equals(user.getRole().getName())) {
            List<appartments> apartments = apartmentsRepository.findByOwner_Id(user.getId());
            apartmentsRepository.deleteAll(apartments);
        }

        user.setIsDeleted(true);
        userRepository.save(user);
    }
}
