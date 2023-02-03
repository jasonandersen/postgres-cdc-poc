package com.svhelloworld.cdc.encounters.dao;

import com.svhelloworld.cdc.encounters.model.EncounterStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converter class used by Hibernate to convert integer values to and from {@link EncounterStatus} enum values.
 */
@Converter(autoApply = true)
public class EncounterStatusConverter implements AttributeConverter<EncounterStatus, Integer> {
    
    @Override
    public Integer convertToDatabaseColumn(EncounterStatus encounterStatus) {
        if (encounterStatus == null) {
            return null;
        }
        return encounterStatus.getStatusId();
    }
    
    @Override
    public EncounterStatus convertToEntityAttribute(Integer statusId) {
        if (statusId == null) {
            return null;
        }
        return EncounterStatus.fromId(statusId);
    }
}
