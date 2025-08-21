package com.RentalApplication.rent.service.Repository;

import com.RentalApplication.rent.service.Entity.propertyowner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyOwnerRepository extends JpaRepository<propertyowner,Integer> {
}
