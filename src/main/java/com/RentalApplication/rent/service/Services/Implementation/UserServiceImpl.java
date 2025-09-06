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
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;


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
    private final JWTService jwtService; //

    private final Validator validator;

    private User getCurrentUserOrThrow(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails ud)) {
            throw new IllegalStateException("Unauthenticated or invalid principal");
        }
        String email = ud.getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Helper: map entity -> DTO
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
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDTO(user);
    }

    @Override
    public void deleteUserAsAdmin(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // if OWNER -> delete his apartments too
        if (Owner.equals(user.getRole().getName())) {
            List<Apartments> apartment = apartmentsRepository.findByOwner_Id(user.getId());
            apartmentsRepository.deleteAll(apartment);
        }

        user.setIsDeleted(true);
        userRepository.save(user);
    }

    // --- Client/Owner operations ---
    @Override
    public ResponseDTO registerUser(RegisterUserDTO dto) {
        // 1) Bean validation (service-level)
        var violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations); // your @ControllerAdvice maps this to 400
        }

        // 2) Fast pre-checks (friendlier than DB error)
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("This email address is already registered");
        }
        if (userRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new IllegalArgumentException("This phone number is already registered");
        }

        // 3) Resolve role (TitleCase: Admin/Owner/Client)
        var role = roleRepository.findByName(dto.getRoleName());
        if (role == null) {
            throw new IllegalArgumentException("Invalid role: " + dto.getRoleName());
        }

        try {
            // 4) Create + save
            User newUser = User.builder()
                    .name(dto.getName())
                    .email(dto.getEmail())
                    .phoneNumber(dto.getPhoneNumber())
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .role(role)
                    .isDeleted(false)
                    .build();

            User savedUser = userRepository.save(newUser);

            // 5) Issue JWT
            String token = jwtService.generateToken(savedUser);
            return ResponseDTO.builder()
                    .id(savedUser.getId())
                    .token(token)
                    .build();

        } catch (DataIntegrityViolationException e) {
            // Race-condition safety: DB unique constraints may still fire
            String msg = e.getMostSpecificCause() != null
                    ? e.getMostSpecificCause().getMessage().toLowerCase()
                    : e.getMessage().toLowerCase();

            if (msg.contains("users.email")) {
                throw new EmailAlreadyExistsException("This email address is already registered");
            } else if (msg.contains("phone")) { // adjust to your constraint/index name if you have one
                throw new IllegalArgumentException("This phone number is already registered");
            }
            throw e; // let your global handler deal with unknown integrity issues
        }
    }

    @Override
    public ResponseDTO login(String username, String password) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationException("Invalid credentials");
        }

        if (user.getIsDeleted()) {
            throw new AuthenticationException("This account is deleted");
        }
        String token = jwtService.generateToken(user);
        return ResponseDTO.builder()
                .id(user.getId())
                .token(token)
                .build();

        // generate JWT token


    }


    @Override
    public UserDTO updateUser(Integer id, @Valid UserDTO dto) {

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || !(auth.getPrincipal() instanceof UserDetails)) {
            throw new AccessDeniedException("Unauthenticated");
        }
        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        User current = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!current.getId().equals(id)) {
            throw new AccessDeniedException(
                    "You can't update another user account");
        }

        // Uniqueness checks -> IllegalArgumentException is fine
        if (userRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new IllegalArgumentException("Email is already in use by another user");
        }
        if (userRepository.existsByPhoneNumberAndIdNot(dto.getPhoneNumber(), id)) {
            throw new IllegalArgumentException("Phone number is already in use by another user");
        }

        // Apply updates
        current.setName(dto.getName());
        current.setEmail(dto.getEmail());
        current.setPhoneNumber(dto.getPhoneNumber());
        current.setUpdatedAt(java.time.LocalDateTime.now());

        return mapToDTO(userRepository.save(current));
    }



    @Override
    public void deleteUser(Integer id) {


        // ---- self check (no controller code needed) ----
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails ud)) {
            throw new AccessDeniedException("Unauthenticated");
        }
        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        User current = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!current.getId().equals(id)) {
            throw new AccessDeniedException("You can't delete another user account");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new IllegalStateException("User is already deleted");
        }

        String roleName = user.getRole().getName();

        // OWNER: soft-delete all their apartments and free any clients
        if (Owner.equalsIgnoreCase(roleName)) {
            List<Apartments> owned = apartmentsRepository.findByOwner_Id(user.getId());
            for (Apartments a : owned) {
                a.setClient(null);                // free any client renting it
                a.setIsDeleted(true);             // soft delete apartment
                a.setUpdatedAt(LocalDateTime.now());
            }
            if (!owned.isEmpty()) {
                apartmentsRepository.saveAll(owned);
            }
        }

        // CLIENT: free ALL apartments they rent (0..n)
        if (Client.equalsIgnoreCase(roleName)) {
            List<Apartments> rented = apartmentsRepository.findByClient_Id(user.getId());
            for (Apartments a : rented) {
                a.setClient(null);                // client_id -> NULL
                a.setUpdatedAt(LocalDateTime.now());
            }
            if (!rented.isEmpty()) {
                apartmentsRepository.saveAll(rented);
            }
        }

        // Soft-delete the user
        user.setIsDeleted(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

}





