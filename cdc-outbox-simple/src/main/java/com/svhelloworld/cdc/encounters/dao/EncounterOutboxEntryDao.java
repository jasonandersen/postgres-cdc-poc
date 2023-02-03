package com.svhelloworld.cdc.encounters.dao;

import com.svhelloworld.cdc.encounters.model.EncounterOutboxEntry;
import com.svhelloworld.cdc.encounters.model.OutboxStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EncounterOutboxEntryDao extends CrudRepository<EncounterOutboxEntry, UUID> {
    
    /*
     * FIXME - need to restrict this DAO to one read method
     * Did a little research into it and it looked to be a pain in the tookus so we'll deal with it later
     */
    
    List<EncounterOutboxEntry> findByStatus(OutboxStatus status);
}
