package com.RentalApplication.rent.service.Repository;

import com.RentalApplication.rent.service.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmailAndIdNot(String email, Integer id);
    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Integer id);

}
