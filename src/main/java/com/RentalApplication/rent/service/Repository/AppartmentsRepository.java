package com.RentalApplication.rent.service.Repository;

import com.RentalApplication.rent.service.Entity.appartments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppartmentsRepository  extends JpaRepository<appartments,Integer> {

}
