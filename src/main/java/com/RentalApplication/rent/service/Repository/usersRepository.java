package com.RentalApplication.rent.service.Repository;

import com.RentalApplication.rent.service.Entity.users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface usersRepository extends JpaRepository<users, UUID> {

    Optional<users> findByEmail(String email);
}
