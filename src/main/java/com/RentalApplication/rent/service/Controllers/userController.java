package com.RentalApplication.rent.service.Controllers;


import com.RentalApplication.rent.service.Services.Interfaces.userService;
import com.RentalApplication.rent.service.dto.userDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class userController {


    private final userService usersService;

    // --- Authentication ---
    @PostMapping("/register")
    public ResponseEntity<userDTO> register(@RequestBody userDTO dto) {
        return ResponseEntity.ok(usersService.registerUser(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        return ResponseEntity.ok(usersService.login(username, password));
    }

    // --- Admin Endpoints ---
    @GetMapping("/admin")
    public ResponseEntity<List<userDTO>> getAllUsersAsAdmin() {
        return ResponseEntity.ok(usersService.getAllUsersAsAdmin());
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<userDTO> getUserByIdAsAdmin(@PathVariable UUID id) {
        return ResponseEntity.ok(usersService.getUserByIdAsAdmin(id));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteUserAsAdmin(@PathVariable UUID id) {
        usersService.deleteUserAsAdmin(id);
        return ResponseEntity.noContent().build();
    }

    // --- User Endpoints ---
    @PutMapping("/{id}")
    public ResponseEntity<userDTO> updateUser(@PathVariable UUID id, @RequestBody userDTO dto) {
        return ResponseEntity.ok(usersService.updateUser(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        usersService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
