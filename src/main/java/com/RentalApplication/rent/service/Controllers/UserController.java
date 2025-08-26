package com.RentalApplication.rent.service.Controllers;


import com.RentalApplication.rent.service.DTO.*;
import com.RentalApplication.rent.service.Services.Interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {


    private final UserService usersService;

    // --- Authentication ---
    @PostMapping("/register")
    public ResponseEntity<RegiseteResponseDTO> register( @Valid @RequestBody RegisterUserDTO dto) {
        try {
            usersService.registerUser(dto);
            return ResponseEntity.ok(RegiseteResponseDTO.builder()
                    .success(true)
                    .message("New account has been created successfully")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(RegiseteResponseDTO.builder()
                    .success(false)
                    .message("Error creating account: " + e.getMessage())
                    .build());
        }


    }


  @PostMapping("/login")
  public ResponseEntity<ResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
      ResponseDTO responseDTO = usersService.login(loginRequest.getEmail(), loginRequest.getPassword());
      return ResponseEntity.ok(responseDTO);
  }


    // --- Admin Endpoints ---
    @GetMapping("/admin")
    public ResponseEntity<List<UserDTO>> getAllUsersAsAdmin() {
        return ResponseEntity.ok(usersService.getAllUsersAsAdmin());
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<UserDTO> getUserByIdAsAdmin(@PathVariable Integer id) {
        return ResponseEntity.ok(usersService.getUserByIdAsAdmin(id));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteUserAsAdmin(@PathVariable Integer id) {
        usersService.deleteUserAsAdmin(id);
        return ResponseEntity.noContent().build();
    }

    // --- User Endpoints ---
    @PutMapping("/client/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Integer id, @RequestBody UserDTO dto) {
        return ResponseEntity.ok(usersService.updateUser(id, dto));
    }

    @DeleteMapping("/client/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        usersService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }




}
