package com.RentalApplication.rent.service.Controllers;


import com.RentalApplication.rent.service.DTO.*;
import com.RentalApplication.rent.service.Entity.User;
import com.RentalApplication.rent.service.Exceptions.AccessDeniedException;
import com.RentalApplication.rent.service.Repository.UserRepository;
import com.RentalApplication.rent.service.Security.JWTService;
import com.RentalApplication.rent.service.Services.Interfaces.ApartmentsService;
import com.RentalApplication.rent.service.Services.Interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {


    private final UserService usersService;

    private final JWTService jwtService;

    private final UserRepository userRepository;

    private final ApartmentsService apartmentsService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@RequestBody RegisterUserDTO dto) {
        ResponseDTO res = usersService.registerUser(dto);  // contains token (and id if you added it)
        return ResponseEntity.ok(res);


    }


    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        ResponseDTO responseDTO = usersService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(responseDTO);
    }


    // --- Admin Endpoints ---
    @GetMapping("/admin")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<List<UserDTO>> getAllUsersAsAdmin() {
        return ResponseEntity.ok(usersService.getAllUsersAsAdmin());
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<UserDTO> getUserByIdAsAdmin(@PathVariable Integer id) {
        return ResponseEntity.ok(usersService.getUserByIdAsAdmin(id));
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Void> deleteUserAsAdmin(@PathVariable Integer id) {
        usersService.deleteUserAsAdmin(id);
        return ResponseEntity.noContent().build();
    }



    @PutMapping("/client/{id}")
    @PreAuthorize("hasRole('Client')")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Integer id,
            @RequestBody UserDTO dto) {
        return ResponseEntity.ok(usersService.updateUser(id, dto));
    }



    @DeleteMapping("/client/{id}")
    @PreAuthorize("hasRole('Client')")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Integer id) {
        usersService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "Account has been deleted"));
    }



    @PutMapping("/owner/{id}")
    @PreAuthorize("hasRole('Owner')")
    public ResponseEntity<UserDTO> updateOwner(
            @PathVariable Integer id,
            @RequestBody UserDTO dto) {
        return ResponseEntity.ok(usersService.updateUser(id, dto));
    }


    @DeleteMapping("/owner/{id}")
    @PreAuthorize("hasRole('Owner')")
    public ResponseEntity<Map<String, String>> deleteOwner(@PathVariable Integer id) {
        usersService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "Account has been deleted"));
    }
}