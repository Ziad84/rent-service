package com.RentalApplication.rent.service;


import com.RentalApplication.rent.service.DTO.RegisterUserDTO;
import com.RentalApplication.rent.service.DTO.ResponseDTO;
import com.RentalApplication.rent.service.Entity.Roles;
import com.RentalApplication.rent.service.Entity.User;
import com.RentalApplication.rent.service.Exceptions.AuthenticationException;
import com.RentalApplication.rent.service.Exceptions.EmailAlreadyExistsException;
import com.RentalApplication.rent.service.Repository.AppartmentsRepository;
import com.RentalApplication.rent.service.Repository.RolesRepository;
import com.RentalApplication.rent.service.Repository.UserRepository;
import com.RentalApplication.rent.service.Security.JWTService;
import com.RentalApplication.rent.service.Services.Implementation.UserServiceImpl;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



    @ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
    class UserLoginRegisterTest {

        @Mock
        private UserRepository userRepository;
        @Mock
        private RolesRepository roleRepository;
        @Mock
        private AppartmentsRepository apartmentsRepository;
        @Mock
        private PasswordEncoder passwordEncoder;
        @Mock
        private JWTService jwtService;

        private Validator validator;
        private UserServiceImpl service;

        @BeforeEach
        void setUp() {
            validator = Validation.buildDefaultValidatorFactory().getValidator();
            service = new UserServiceImpl(userRepository, roleRepository, apartmentsRepository, passwordEncoder, jwtService);
        }

        @Test
        void registerUser_success_returnsToken() {
            RegisterUserDTO dto = new RegisterUserDTO();
            dto.setName("Ziad");
            dto.setEmail("ziad.albalwi@gmail.com");
            dto.setPhoneNumber("0559746485");
            dto.setPassword("secret1234");
            dto.setRoleName("Client");

            Roles clientRole = new Roles();
            clientRole.setId(3);
            clientRole.setName("Client");

            when(roleRepository.findByName("Client")).thenReturn(clientRole);
            when(passwordEncoder.encode("secret1234")).thenReturn("ENC");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> {
                User u = inv.getArgument(0);
                u.setId(42);
                return u;
            });
            when(jwtService.generateToken(any(User.class))).thenReturn("jwt-123");

            ResponseDTO res = service.registerUser(dto);

            assertNotNull(res);
            assertEquals("jwt-123", res.getToken());
            verify(userRepository).save(any(User.class));
        }

        @Test
        void registerUser_duplicateEmail_translatedToCustomException() {
            RegisterUserDTO dto = new RegisterUserDTO();
            dto.setName("Ziad");
            dto.setEmail("ziad.albalwi@gmail.com");
            dto.setPhoneNumber("0559746493");
            dto.setPassword("Secret1234");
            dto.setRoleName("Client");

            when(userRepository.existsByEmail("ziad.albalwi@gmail.com")).thenReturn(true);

            assertThrows(EmailAlreadyExistsException.class, () -> service.registerUser(dto));

            verify(userRepository, never()).save(any());

            verify(roleRepository, never()).findByName(anyString());
            verify(passwordEncoder, never()).encode(anyString());
        }

        @Test
        void registerUser_invalidRole_throwsIllegalArgument() {
            RegisterUserDTO dto = new RegisterUserDTO();
            dto.setName("Ziad");
            dto.setEmail("Ziad@gmail.com");
            dto.setPhoneNumber("0559746492");
            dto.setPassword("12345678");
            dto.setRoleName("Adminn");

            when(roleRepository.findByName("Adminn")).thenReturn(null);

            assertThrows(IllegalArgumentException.class, () -> service.registerUser(dto));
            verify(userRepository, never()).save(any());
        }

        @Test
        void login_success_returnsToken() {
            User u = new User();
            u.setEmail("ziad.albalwi@gmail.com");
            u.setPassword("12345678");
            u.setIsDeleted(false);

            when(userRepository.findByEmail("ziad.albalwi@gmail.com")).thenReturn(Optional.of(u));
            when(passwordEncoder.matches("secret1234", "12345678")).thenReturn(true);
            when(jwtService.generateToken(u)).thenReturn("jwt-xyz");

            ResponseDTO out = service.login("ziad.albalwi@gmail.com", "secret1234");


            assertEquals("jwt-xyz", out.getToken());
        }

        @Test
        void login_wrongPassword_throwsAuthException() {
            User u = new User();
            u.setEmail("ziad@example.com");
            u.setPassword("s1234568");
            u.setIsDeleted(false);

            when(userRepository.findByEmail("ziad@example.com")).thenReturn(Optional.of(u));
            when(passwordEncoder.matches("12345678", "s1234568")).thenReturn(false);

            assertThrows(AuthenticationException.class, () -> service.login("ziad@example.com", "12345678"));
        }

        @Test
        void login_deletedAccount_throwsAuthException() {
            User u = new User();
            u.setEmail("ziad.albalwi@gmail.com");
            u.setPassword("12345678");
            u.setIsDeleted(true);

            when(userRepository.findByEmail("ziad.albalwi@gmail.com")).thenReturn(Optional.of(u));

            assertThrows(AuthenticationException.class, () -> service.login("ziad.albalwi@gmail.com", "1234568"));
        }



}
