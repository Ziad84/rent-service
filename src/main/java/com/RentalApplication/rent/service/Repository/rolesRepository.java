package com.RentalApplication.rent.service.Repository;

import com.RentalApplication.rent.service.Entity.roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface rolesRepository extends JpaRepository<roles, Long> {

    roles findByName(String name);
}
