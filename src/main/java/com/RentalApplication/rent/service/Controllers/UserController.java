package com.RentalApplication.rent.service.Controllers;


import com.RentalApplication.rent.service.DTO.*;
import com.RentalApplication.rent.service.Services.Interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;



@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {


    private final UserService usersService;



    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@Valid @RequestBody RegisterUserDTO dto) {
        ResponseDTO responseDTO = usersService.registerUser(dto);
        return ResponseEntity.ok(responseDTO);


    }


    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        ResponseDTO responseDTO = usersService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(responseDTO);
    }



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
    public ResponseEntity<Map<String, String>> deleteUserAsAdmin(@PathVariable Integer id) {
        usersService.deleteUserAsAdmin(id);
        return  ResponseEntity.ok(Map.of("message", "user has been deleted"));
    }



    @PutMapping("/client/{id}")
    @PreAuthorize("hasRole('Client')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Integer id, @RequestBody UserDTO dto) {

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
    public ResponseEntity<UserDTO> updateOwner(@PathVariable Integer id, @RequestBody UserDTO dto) {
        return ResponseEntity.ok(usersService.updateUser(id, dto));
    }


    @DeleteMapping("/owner/{id}")
    @PreAuthorize("hasRole('Owner')")
    public ResponseEntity<Map<String, String>> deleteOwner(@PathVariable Integer id) {
        usersService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "Account has been deleted"));
    }
}