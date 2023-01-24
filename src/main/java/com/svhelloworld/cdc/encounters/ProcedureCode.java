package com.svhelloworld.cdc.encounters;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cpt_codes")
public class ProcedureCode {
    
    public static ProcedureCode from(String cptCode, String description) {
        ProcedureCode code = new ProcedureCode();
        code.setCptCode(cptCode);
        code.setDescription(description);
        return code;
    }
    
    @Id
    @Column(name = "cpt_code")
    private String cptCode;
    
    @Column(name = "description")
    private String description;
    
    public String getCptCode() {
        return cptCode;
    }
    
    public void setCptCode(String cptCode) {
        this.cptCode = cptCode;
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
        if (!(o instanceof ProcedureCode)) return false;
        ProcedureCode that = (ProcedureCode) o;
        return getCptCode().equals(that.getCptCode());
    }
    
    @Override
    public int hashCode() {
        return getCptCode().hashCode();
    }
}
