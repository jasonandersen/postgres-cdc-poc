package com.svhelloworld.cdc;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncounterDao extends CrudRepository<Encounter, Long> {
}