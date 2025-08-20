package com.RentalApplication.rent.service.Entity;


import jakarta.persistence.*;

@Entity
@Table(name = "appartments")
public class Appartments {

@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")

}
