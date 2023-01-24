package com.svhelloworld.cdc.encounters.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "icd_codes")
public class DiagnosisCode {
    
    public static DiagnosisCode from(String icdCode, String description) {
        DiagnosisCode out = new DiagnosisCode();
        out.setIcdCode(icdCode);
        out.setDescription(description);
        return out;
    }
    
    @Id
    @Column(name = "icd_code")
    private String icdCode;
    
    @Column(name = "description")
    private String description;
    
    public String getIcdCode() {
        return icdCode;
    }
    
    public void setIcdCode(String icdCode) {
        this.icdCode = icdCode;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiagnosisCode)) return false;
        
        DiagnosisCode that = (DiagnosisCode) o;
    
        return getIcdCode().equals(that.getIcdCode());
    }
    
    @Override
    public int hashCode() {
        return getIcdCode().hashCode();
    }
}
