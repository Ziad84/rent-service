package com.RentalApplication.RT.Entity;


import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "property_owner")
public class PropertyOwner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private UUID id;

    @Column(name = "Owner_name")
    private String ownerName;

    @Column(name = "Owner_email", unique = true)
    private String ownerEmail;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    /*
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Apartment> apartments;
*/
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;

    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

    }

public PropertyOwner(){

}

public PropertyOwner(String ownerName,String ownerEmail,String phoneNumber,LocalDateTime createdAt , LocalDateTime updatedAt,String createdBy , String updatedBy ){

        this.ownerName=ownerName;
        this.ownerEmail=ownerEmail;
        this.phoneNumber=phoneNumber;
        this.createdAt=createdAt;
        this.updatedAt=updatedAt;
        this.createdBy=createdBy;
        this.updatedBy=updatedBy;

}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
