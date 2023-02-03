package com.svhelloworld.cdc.encounters.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProcedureCode that)) return false;
        return getCptCode().equals(that.getCptCode());
    }
    
    @Override
    public int hashCode() {
        return getCptCode().hashCode();
    }
}
