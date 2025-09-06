package com.RentalApplication.rent.service.Repository;

import com.RentalApplication.rent.service.Entity.Apartments;
import com.RentalApplication.rent.service.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppartmentsRepository  extends JpaRepository<Apartments, Integer> {

    List<Apartments> findByOwner_Id(Integer ownerId);

    List<Apartments> findByClient_Id(Integer clientId);

    List<Apartments> findByIsDeleted(Boolean isDeleted);




}
