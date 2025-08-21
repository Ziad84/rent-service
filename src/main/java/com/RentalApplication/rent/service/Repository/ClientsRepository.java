package com.RentalApplication.rent.service.Repository;

import com.RentalApplication.rent.service.Entity.clients;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientsRepository extends JpaRepository<clients,Integer> {
}
