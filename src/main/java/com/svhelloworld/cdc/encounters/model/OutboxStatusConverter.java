package com.svhelloworld.cdc.encounters.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OutboxStatusConverter implements AttributeConverter<OutboxStatus, Integer> {
    
    @Override
    public Integer convertToDatabaseColumn(OutboxStatus outboxStatus) {
        if (outboxStatus == null) {
            return null;
        }
        return outboxStatus.getStatusId();
    }
    
    @Override
    public OutboxStatus convertToEntityAttribute(Integer statusId) {
        if (statusId == null) {
            return null;
        }
        return OutboxStatus.fromId(statusId);
    }
}

