package com.RentalApplication.rent.service.Services.Implementation;


import com.RentalApplication.rent.service.Entity.Apartments;
import com.RentalApplication.rent.service.Entity.User;
import com.RentalApplication.rent.service.Exceptions.AccessDeniedException;
import com.RentalApplication.rent.service.Exceptions.AuthenticationException;
import com.RentalApplication.rent.service.Exceptions.EmailAlreadyExistsException;
import com.RentalApplication.rent.service.Repository.AppartmentsRepository;
import com.RentalApplication.rent.service.Repository.RolesRepository;
import com.RentalApplication.rent.service.Repository.UserRepository;
import com.RentalApplication.rent.service.Security.JWTService;
import com.RentalApplication.rent.service.Services.Interfaces.UserService;
import com.RentalApplication.rent.service.DTO.*;
import com.RentalApplication.rent.service.Utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.RentalApplication.rent.service.Roles.Role.Client;
import static com.RentalApplication.rent.service.Roles.Role.Owner;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RolesRepository roleRepository;
    private final AppartmentsRepository apartmentsRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;





    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .roleName(user.getRole().getName())
                .isDeleted(user.getIsDeleted())
                .build();
    }


    @Override
    public List<UserDTO> getAllUsersAsAdmin() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserByIdAsAdmin(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDTO(user);
    }

    @Override
    public void deleteUserAsAdmin(Integer id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new IllegalStateException("User is already deleted");
        }

        String roleName = user.getRole().getName();

        if (Owner.equals(roleName)) {
            List<Apartments> owned = apartmentsRepository.findByOwner_Id(user.getId());
            for (Apartments a : owned) {
                a.setClient(null);
                a.setIsDeleted(true);
                a.setUpdatedAt(LocalDateTime.now());
            }
            if (!owned.isEmpty()) {
                apartmentsRepository.saveAll(owned);
            }
        }

        if (Client.equals(roleName)) {
            List<Apartments> rented = apartmentsRepository.findByClient_Id(user.getId());
            for (Apartments a : rented) {
                a.setClient(null);
                a.setUpdatedAt(LocalDateTime.now());
            }
            if (!rented.isEmpty()) {
                apartmentsRepository.saveAll(rented);
            }
        }

        user.setIsDeleted(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);


    }


    @Override
    public ResponseDTO registerUser( RegisterUserDTO dto) {


        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("This email address is already registered");
        }
        if (userRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new IllegalArgumentException("This phone number is already registered");
        }


        var role = roleRepository.findByName(dto.getRoleName());
        if (role == null) {
            throw new IllegalArgumentException("Invalid role: " + dto.getRoleName());
        }


            User newUser = User.builder()
                    .name(dto.getName())
                    .email(dto.getEmail())
                    .phoneNumber(dto.getPhoneNumber())
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .role(role)
                    .isDeleted(false)
                    .build();

            User savedUser = userRepository.save(newUser);


            String token = jwtService.generateToken(savedUser);
            return ResponseDTO.builder()
                    .token(token)
                    .build();


    }

    @Override
    public ResponseDTO login(String username, String password) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new AuthenticationException("Email is not correct"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationException("password is not correct");
        }

        if (user.getIsDeleted()) {
            throw new AuthenticationException("This account is deleted");
        }
        String token = jwtService.generateToken(user);
        return ResponseDTO.builder()
                .token(token)
                .build();




    }


    @Override
    public UserDTO updateUser( Integer id,  UserDTO dto) {

        User current = SecurityUtils.getCurrentUser(userRepository);

        if (!current.getId().equals(id)) {
            throw new AccessDeniedException("You can't update another user account");
        }


        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email is already used by another user");
        }



        if (userRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number is already used by another user");
        }


        current.setName(dto.getName());
        current.setEmail(dto.getEmail());
        current.setPhoneNumber(dto.getPhoneNumber());
        current.setUpdatedAt(java.time.LocalDateTime.now());

        return mapToDTO(userRepository.save(current));
    }



    @Override
    public void deleteUser(Integer id) {

        User current = SecurityUtils.getCurrentUser(userRepository);
        String roleName = current.getRole().getName();

        if (!current.getId().equals(id)) {
            throw new AccessDeniedException("You can't delete another user account");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new IllegalStateException("User is already deleted");
        }


        if (Owner.equals(roleName)) {
            List<Apartments> owned = apartmentsRepository.findByOwner_Id(user.getId());
            for (Apartments a : owned) {
                a.setClient(null);
                a.setIsDeleted(true);
                a.setUpdatedAt(LocalDateTime.now());
            }
            if (!owned.isEmpty()) {
                apartmentsRepository.saveAll(owned);
            }
        }


        if (Client.equals(roleName)) {
            List<Apartments> rented = apartmentsRepository.findByClient_Id(user.getId());
            for (Apartments a : rented) {
                a.setClient(null);
                a.setUpdatedAt(LocalDateTime.now());
            }
            if (!rented.isEmpty()) {
                apartmentsRepository.saveAll(rented);
            }
        }


        user.setIsDeleted(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

}





