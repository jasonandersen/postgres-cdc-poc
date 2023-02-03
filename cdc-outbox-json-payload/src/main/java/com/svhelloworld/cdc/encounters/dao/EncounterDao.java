package com.svhelloworld.cdc.encounters.dao;

import com.svhelloworld.cdc.encounters.model.Encounter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncounterDao extends CrudRepository<Encounter, Long> {
}
