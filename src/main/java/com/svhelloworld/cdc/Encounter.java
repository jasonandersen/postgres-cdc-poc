package com.svhelloworld.cdc;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name="encounters")
public class Encounter {
    
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "EncounterSequenceId")
    @SequenceGenerator(
            name = "EncounterSequenceId",
            sequenceName = "encounters_encounter_id_seq",
            allocationSize = 1)
    @Column(name = "encounter_id")
    private long id;
    
    @Column(name = "encounter_status_id")
    private int statusId;
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "created_on")
    private Instant createdOn = Instant.now();
    
    @Column(name = "updated_on")
    private Instant updatedOn = Instant.now();
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public int getStatusId() {
        return statusId;
    }
    
    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Instant getCreatedOn() {
        return createdOn;
    }
    
    public void setCreatedOn(Instant createdOn) {
        this.createdOn = createdOn;
    }
    
    public Instant getUpdatedOn() {
        return updatedOn;
    }
    
    public void setUpdatedOn(Instant updatedOn) {
        this.updatedOn = updatedOn;
    }
}
