package com.RentalApplication.rent.service.Repository;

import com.RentalApplication.rent.service.Entity.appartments;
import com.RentalApplication.rent.service.Entity.users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AppartmentsRepository  extends JpaRepository<appartments, UUID> {

    List<appartments> findByOwner_Id(UUID ownerId);

}
