package com.RentalApplication.rent.service.Controllers;

import com.RentalApplication.rent.service.DTO.ApartmentsDTO;
import com.RentalApplication.rent.service.DTO.RentRequestDTO;

import com.RentalApplication.rent.service.Services.Interfaces.ApartmentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
    @RequestMapping("/api/v1/apartments")
    @RequiredArgsConstructor
    public class ApartmentsController {

        private final ApartmentsService apartmentsService;


        @GetMapping("/Allappartments")
        @PreAuthorize("hasAnyRole('Admin', 'Owner', 'Client')")
        public ResponseEntity<List<ApartmentsDTO>> getAll() {
            return ResponseEntity.ok(apartmentsService.getAllApartments());
        }


       @GetMapping("/{id}")
       @PreAuthorize("hasAnyRole('Admin','Owner','Client')")
       public ResponseEntity<ApartmentsDTO> getApartmentById(@PathVariable Integer id) {
           return ResponseEntity.ok(apartmentsService.getApartmentById(id));
       }


      @PostMapping("/CreateApartment")
      @PreAuthorize("hasAnyRole('Owner')")
      public ResponseEntity<ApartmentsDTO> createApartment(@RequestBody ApartmentsDTO dto) {
          return ResponseEntity.ok(apartmentsService.createApartment(dto));
      }

        @PutMapping("/updateApartment/{id}")
        @PreAuthorize("hasRole('Owner')")
        public ResponseEntity<ApartmentsDTO> updateApartment(@PathVariable Integer id, @RequestBody ApartmentsDTO dto) {
            return ResponseEntity.ok(apartmentsService.updateApartment(id, dto));
        }



        @DeleteMapping("/deleteApartment/{id}")
        @PreAuthorize("hasAnyRole('Admin','Owner')")
        public ResponseEntity<Map<String, String>> deleteApartment(@PathVariable Integer id) {
            apartmentsService.deleteApartment(id);
            return ResponseEntity.ok(Map.of("message", "Apartment has been deleted"));
        }



        @GetMapping("/available")
        @PreAuthorize("hasAnyRole('Client','Admin','Owner')")
        public ResponseEntity<List<ApartmentsDTO>> viewAvailableApartments() {
            return ResponseEntity.ok(apartmentsService.viewAvailableApartments());

        }

        @PostMapping("/rent")
        @PreAuthorize("hasRole('Client')")
        public ResponseEntity<ApartmentsDTO> rentApartment(@RequestBody RentRequestDTO request) {

            return ResponseEntity.ok(apartmentsService.rentApartment(request.getApartmentId()));
        }

    }


