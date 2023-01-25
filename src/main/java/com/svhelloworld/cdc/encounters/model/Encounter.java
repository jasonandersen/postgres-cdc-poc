package com.svhelloworld.cdc.encounters.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

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
    
    @Column(name = "patient_id")
    private String patientId;
    
    @Column(name = "encounter_status_id")
    private EncounterStatus status;
    
    @Column(name = "notes")
    private String notes;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "encounter_procedures",
            joinColumns = @JoinColumn(name = "encounter_id"),
            inverseJoinColumns = @JoinColumn(name = "cpt_code"))
    private Set<ProcedureCode> procedureCodes;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "encounter_dx",
            joinColumns = @JoinColumn(name = "encounter_id"),
            inverseJoinColumns = @JoinColumn(name = "icd_code"))
    private Set<DiagnosisCode> diagnosisCodes;
    
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
    
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public EncounterStatus getStatus() {
        return status;
    }
    
    public void setStatus(EncounterStatus status) {
        this.status = status;
    }
    
    public void setStatusName(String statusName) {
        this.status = EncounterStatus.valueOf(statusName);
    }
    
    /**
     * Allows tests to pass in a status ID that we'll resolve into a {@link EncounterStatus} instance.
     */
    public void setStatusId(int statusId) {
        setStatus(EncounterStatus.fromId(statusId));
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Set<ProcedureCode> getProcedureCodes() {
        return procedureCodes;
    }
    
    public void setProcedureCodes(Set<ProcedureCode> procedureCodes) {
        this.procedureCodes = procedureCodes;
    }
    
    public Set<DiagnosisCode> getDiagnosisCodes() {
        return diagnosisCodes;
    }
    
    public void setDiagnosisCodes(Set<DiagnosisCode> diagnosisCodes) {
        this.diagnosisCodes = diagnosisCodes;
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
    
    public void addProcedure(ProcedureCode procedure) {
        if (procedureCodes == null) {
            procedureCodes = new HashSet<>();
        }
        procedureCodes.add(procedure);
    }
    
    public void addDiagnosis(DiagnosisCode diagnosis) {
        if (diagnosisCodes == null) {
            diagnosisCodes = new HashSet<>();
        }
        diagnosisCodes.add(diagnosis);
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Encounter)) return false;
        Encounter encounter = (Encounter) o;
        return getId() == encounter.getId();
    }
    
    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }
}
