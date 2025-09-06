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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



    @ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
    class UserServiceImplTest {

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
            service = new UserServiceImpl(userRepository, roleRepository, apartmentsRepository, passwordEncoder, jwtService, validator);
        }

        @Test
        void registerUser_success_returnsToken() {
            RegisterUserDTO dto = new RegisterUserDTO();
            dto.setName("Ziad");
            dto.setEmail("ziad.albalwi1@gmail.com");
            dto.setPhoneNumber("0559746495");
            dto.setPassword("secret");
            dto.setRoleName("Client");

            Roles clientRole = new Roles();
            clientRole.setId(3);
            clientRole.setName("Client");

            when(roleRepository.findByName("Client")).thenReturn(clientRole);
            when(passwordEncoder.encode("secret")).thenReturn("ENC");
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
            dto.setEmail("Ziad@hotmail.com");
            dto.setPhoneNumber("0559746493");
            dto.setPassword("p");
            dto.setRoleName("Client");

            Roles clientRole = new Roles();
            clientRole.setId(3);
            clientRole.setName("Client");
            when(roleRepository.findByName("Client")).thenReturn(clientRole);

            when(passwordEncoder.encode(anyString())).thenReturn("ENC");
            // simulate unique constraint violation on save
            when(userRepository.save(any(User.class)))
                    .thenThrow(new DataIntegrityViolationException("... users.email ..."));

            assertThrows(EmailAlreadyExistsException.class, () -> service.registerUser(dto));
        }

        @Test
        void registerUser_invalidRole_throwsIllegalArgument() {
            RegisterUserDTO dto = new RegisterUserDTO();
            dto.setName("Ziad");
            dto.setEmail("Ziad1@gmail.com");
            dto.setPhoneNumber("0559746492");
            dto.setPassword("p");
            dto.setRoleName("Admin");

            when(roleRepository.findByName("Admin")).thenReturn(null);

            assertThrows(IllegalArgumentException.class, () -> service.registerUser(dto));
            verify(userRepository, never()).save(any());
        }

        @Test
        void login_success_returnsToken() {
            User u = new User();
            u.setId(11);
            u.setEmail("ziad@example.com");
            u.setPassword("ENC");
            u.setIsDeleted(false);

            when(userRepository.findByEmail("ziad.albalwi1@gmail.com")).thenReturn(Optional.of(u));
            when(passwordEncoder.matches("secret", "ENC")).thenReturn(true);
            when(jwtService.generateToken(u)).thenReturn("jwt-xyz");

            ResponseDTO out = service.login("ziad.albalwi1@gmail.com", "secret");

            assertEquals(11, out.getId());
            assertEquals("jwt-xyz", out.getToken());
        }

        @Test
        void login_wrongPassword_throwsAuthException() {
            User u = new User();
            u.setEmail("ziad@example.com");
            u.setPassword("ENC");
            u.setIsDeleted(false);

            when(userRepository.findByEmail("ziad@example.com")).thenReturn(Optional.of(u));
            when(passwordEncoder.matches("bad", "ENC")).thenReturn(false);

            assertThrows(AuthenticationException.class, () -> service.login("ziad@example.com", "bad"));
        }

        @Test
        void login_deletedAccount_throwsAuthException() {
            User u = new User();
            u.setEmail("ziad.albalwi1@gmail.com");
            u.setPassword("ENC");
            u.setIsDeleted(true);

            when(userRepository.findByEmail("ziad.albalwi1@gmail.com")).thenReturn(Optional.of(u));

            assertThrows(AuthenticationException.class, () -> service.login("ziad.albalwi1@gmail.com", "secret"));
        }
}
