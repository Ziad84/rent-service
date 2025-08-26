package com.RentalApplication.rent.service.Services.Implementation;


import com.RentalApplication.rent.service.Entity.Appartments;
import com.RentalApplication.rent.service.Entity.Users;
import com.RentalApplication.rent.service.Exceptions.AuthenticationException;
import com.RentalApplication.rent.service.Exceptions.EmailAlreadyExistsException;
import com.RentalApplication.rent.service.Repository.AppartmentsRepository;
import com.RentalApplication.rent.service.Repository.RolesRepository;
import com.RentalApplication.rent.service.Repository.UsersRepository;
import com.RentalApplication.rent.service.Security.JWTService;
import com.RentalApplication.rent.service.Services.Interfaces.UserService;
import com.RentalApplication.rent.service.DTO.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsersRepository userRepository;
    private final RolesRepository roleRepository;
    private final AppartmentsRepository apartmentsRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService; //

    // Helper: map entity -> DTO
    private UserDTO mapToDTO(Users user) {
        return UserDTO.builder()
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
    public List<UserDTO> getAllUsersAsAdmin() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserByIdAsAdmin(Integer id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDTO(user);
    }

    @Override
    public void deleteUserAsAdmin(Integer id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // if OWNER -> delete his apartments too
        if ("OWNER".equals(user.getRole().getName())) {
            List<Appartments> apartment = apartmentsRepository.findByOwner_Id(user.getId());
            apartmentsRepository.deleteAll(apartment);
        }

        user.setIsDeleted(true);
        userRepository.save(user);
    }

    // --- Client/Owner operations ---
    @Override
    public ResponseDTO registerUser(RegisterUserDTO dto) {
       try {
           Users newUser = Users.builder()
                   .name(dto.getName())
                   .email(dto.getEmail())
                   .phoneNumber(dto.getPhoneNumber())
                   .password(passwordEncoder.encode(dto.getPassword())) // encrypt password
                   .role(roleRepository.findByName(dto.getRoleName()))
                   .isDeleted(false)
                   .build();

           Users savedUser = userRepository.save(newUser);
           String token = jwtService.generateToken(savedUser);

           return ResponseDTO.builder()
                   .token(token)
                   .build();
       }
       catch (DataIntegrityViolationException e) {
           if (e.getMessage().toLowerCase().contains("users.email")) {
               throw new EmailAlreadyExistsException("This email address is already registered");
           }
           // Re-throw if it's a different constraint violation
           throw e;
       }


    }

    @Override
    public ResponseDTO login(String username, String password) {
        Users user = userRepository.findByEmail(username)
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationException("Invalid credentials");
        }

        if (user.getIsDeleted()){
            throw new AuthenticationException("This account is deleted");
        }
        String token = jwtService.generateToken(user);
        return ResponseDTO.builder()
                .token(token)
                .build();

        // generate JWT token


    }

    @Override
    public UserDTO updateUser(Integer id, UserDTO uDTO) {
        Users user = userRepository.findById(id)
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
    public void deleteUser(Integer id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // if OWNER -> delete his apartments too
        if ("OWNER".equals(user.getRole().getName())) {
            List<Appartments> apartments = apartmentsRepository.findByOwner_Id(user.getId());
            apartmentsRepository.deleteAll(apartments);
        }

        user.setIsDeleted(true);
        userRepository.save(user);
    }
}
