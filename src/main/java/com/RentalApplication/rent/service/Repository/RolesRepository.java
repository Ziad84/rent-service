package com.RentalApplication.rent.service.Repository;

import com.RentalApplication.rent.service.Entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RolesRepository extends JpaRepository<Roles, Integer> {

    Roles findByName(String name);
}
