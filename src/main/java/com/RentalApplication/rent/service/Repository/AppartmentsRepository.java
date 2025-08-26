package com.RentalApplication.rent.service.Repository;

import com.RentalApplication.rent.service.Entity.Appartments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AppartmentsRepository  extends JpaRepository<Appartments, Integer> {

    List<Appartments> findByOwner_Id(Integer ownerId);

}
