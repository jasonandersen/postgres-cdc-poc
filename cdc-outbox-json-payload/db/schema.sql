----------------------------------------------------------
-- Schema to experiment with trigger-based change data
-- capture patterns to throw domain events from data
-- mutation events.
--
-- Jason Andersen
-- January 19, 2023
----------------------------------------------------------

----------------------------------
-- uuid-ossp extension required to
-- provide UUID functions to generate
-- UUID as primary keys.
----------------------------------

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

----------------------------------
-- Encounter schema
----------------------------------

CREATE TABLE IF NOT EXISTS cpt_codes
(
    cpt_code    VARCHAR(16) PRIMARY KEY,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS icd_codes
(
    icd_code    VARCHAR(16) PRIMARY KEY,
    description VARCHAR(255),
    icd_version SMALLINT
);

CREATE TABLE IF NOT EXISTS encounter_status
(
    encounter_status_id SMALLINT PRIMARY KEY,
    status              VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS encounters
(
    encounter_id        SERIAL PRIMARY KEY,
    patient_id          VARCHAR(255)            NOT NULL,
    encounter_status_id SMALLINT                NOT NULL,
    notes               VARCHAR(255)            NOT NULL,
    created_on          TIMESTAMP DEFAULT NOW() NOT NULL,
    updated_on          TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT fk_encounters_encounter_status FOREIGN KEY (encounter_status_id) REFERENCES encounter_status (encounter_status_id)
);

CREATE TABLE IF NOT EXISTS encounter_procedures
(
    encounter_id INTEGER,
    cpt_code     VARCHAR(16),
    created_on   TIMESTAMP DEFAULT NOW() NOT NULL,
    PRIMARY KEY (encounter_id, cpt_code),
    CONSTRAINT fk_encounter_procedures_encounter_id FOREIGN KEY (encounter_id) REFERENCES encounters (encounter_id),
    CONSTRAINT fk_encounter_procedures_icd_code FOREIGN KEY (cpt_code) REFERENCES cpt_codes (cpt_code)
);

CREATE TABLE IF NOT EXISTS encounter_dx
(
    encounter_id INTEGER,
    icd_code     VARCHAR(16),
    created_on   TIMESTAMP DEFAULT NOW() NOT NULL,
    PRIMARY KEY (encounter_id, icd_code),
    CONSTRAINT fk_encounter_dx_encounter_id FOREIGN KEY (encounter_id) REFERENCES encounters (encounter_id),
    CONSTRAINT fk_encounter_dx_cpt_code FOREIGN KEY (icd_code) REFERENCES icd_codes (icd_code)
);

CREATE TABLE IF NOT EXISTS outbox_status
(
    outbox_status_id INTEGER PRIMARY KEY,
    description      VARCHAR(255) NOT NULL
);

-- Note: a constraint on encounter_id ensures that we cannot write to the encounters_outbox when we delete
-- a row from the encounter table. This schema design is predicated on soft deletes only.
CREATE TABLE IF NOT EXISTS encounters_outbox
(
    event_id         UUID      DEFAULT uuid_generate_v1() PRIMARY KEY,
    encounter_id     INTEGER                 NOT NULL,
    outbox_status_id INTEGER   DEFAULT 100   NOT NULL,
    created_on       TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT fk_encounters_outbox_encounter_id FOREIGN KEY (encounter_id) REFERENCES encounters (encounter_id),
    CONSTRAINT fk_encounters_outbox_outbox_status_id FOREIGN KEY (outbox_status_id) REFERENCES outbox_status (outbox_status_id)
);


----------------------------------
-- Encounter statuses
----------------------------------

INSERT INTO encounter_status
VALUES (100, 'NEW'),
       (200, 'IN PROGRESS'),
       (300, 'COMPLETE')
ON CONFLICT DO NOTHING;

----------------------------------
-- Outbox statuses
----------------------------------

INSERT INTO outbox_status
VALUES (100, 'UNRESOLVED'),
       (200, 'RESOLVED'),
       (300, 'ERROR')
ON CONFLICT DO NOTHING;

----------------------------------
-- ICD10 codes
----------------------------------

INSERT INTO icd_codes (icd_code, description, icd_version)
VALUES ('A36.0', 'Pharyngeal diphtheria', 10)
ON CONFLICT DO NOTHING;
INSERT INTO icd_codes (icd_code, description, icd_version)
VALUES ('A36.1', 'Nasopharyngeal diphtheria', 10)
ON CONFLICT DO NOTHING;
INSERT INTO icd_codes (icd_code, description, icd_version)
VALUES ('A36.2', 'Laryngeal diphtheria', 10)
ON CONFLICT DO NOTHING;
INSERT INTO icd_codes (icd_code, description, icd_version)
VALUES ('A36.3', 'Cutaneous diphtheria', 10)
ON CONFLICT DO NOTHING;
INSERT INTO icd_codes (icd_code, description, icd_version)
VALUES ('A36.8', 'Other diphtheria', 10)
ON CONFLICT DO NOTHING;
INSERT INTO icd_codes (icd_code, description, icd_version)
VALUES ('A36.9', 'Diphtheria, unspecified', 10)
ON CONFLICT DO NOTHING;

----------------------------------
-- CPT codes
----------------------------------

INSERT INTO cpt_codes
VALUES ('86152', 'Cell enumeration &id')
ON CONFLICT DO NOTHING;
INSERT INTO cpt_codes
VALUES ('86153', 'Cell enumeration phys interp')
ON CONFLICT DO NOTHING;
INSERT INTO cpt_codes
VALUES ('86890', 'Autologous blood process')
ON CONFLICT DO NOTHING;
INSERT INTO cpt_codes
VALUES ('86891', 'Autologous blood op salvage')
ON CONFLICT DO NOTHING;
INSERT INTO cpt_codes
VALUES ('86927', 'Plasma fresh frozen')
ON CONFLICT DO NOTHING;
INSERT INTO cpt_codes
VALUES ('86930', 'Frozen blood prep')
ON CONFLICT DO NOTHING;
INSERT INTO cpt_codes
VALUES ('86931', 'Frozen blood thaw')
ON CONFLICT DO NOTHING;
INSERT INTO cpt_codes
VALUES ('86932', 'Frozen blood freeze/thaw')
ON CONFLICT DO NOTHING;
INSERT INTO cpt_codes
VALUES ('86945', 'Blood product/irradiation')
ON CONFLICT DO NOTHING;
INSERT INTO cpt_codes
VALUES ('86950', 'Leukacyte transfusion')
ON CONFLICT DO NOTHING;
INSERT INTO cpt_codes
VALUES ('86960', 'Vol reduction of blood/prod')
ON CONFLICT DO NOTHING;
INSERT INTO cpt_codes
VALUES ('86965', 'Pooling blood platelets')
ON CONFLICT DO NOTHING;
INSERT INTO cpt_codes
VALUES ('86985', 'Split blood or products')
ON CONFLICT DO NOTHING;


----------------------------------
-- created_on triggers
----------------------------------
CREATE OR REPLACE FUNCTION fn_encounters_insert_trigger()
    RETURNS trigger AS
$$
BEGIN
    NEW."created_on" = NOW();
    NEW."updated_on" = NOW();
    RETURN NEW;
END;
$$
    LANGUAGE 'plpgsql';

CREATE TRIGGER encounters_insert_trigger
    BEFORE INSERT
    ON "encounters"
    FOR EACH ROW
EXECUTE PROCEDURE fn_encounters_insert_trigger();

----------------------------------
-- updated_on triggers
----------------------------------
CREATE OR REPLACE FUNCTION fn_encounters_updated_on_trigger()
    RETURNS trigger AS
$$
BEGIN
    NEW."updated_on" = NOW();
    RETURN NEW;
END;
$$
    LANGUAGE 'plpgsql';

CREATE TRIGGER encounters_update_trigger
    BEFORE UPDATE
    ON "encounters"
    FOR EACH ROW
EXECUTE PROCEDURE fn_encounters_updated_on_trigger();

----------------------------------
-- Outbox triggers
----------------------------------

CREATE OR REPLACE FUNCTION fn_encounters_outbox_upsert_trigger()
    RETURNS trigger AS
$$
BEGIN
    INSERT INTO encounters_outbox (encounter_id) VALUES (NEW."encounter_id");
    RETURN NEW;
END;
$$
    LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION fn_encounters_outbox_delete_trigger()
    RETURNS trigger AS
$$
BEGIN
    INSERT INTO encounters_outbox (encounter_id) VALUES (OLD."encounter_id");
    RETURN NEW;
END;
$$
    LANGUAGE 'plpgsql';

CREATE TRIGGER encounters_trigger
    AFTER INSERT OR UPDATE
    ON "encounters"
    FOR EACH ROW
EXECUTE PROCEDURE fn_encounters_outbox_upsert_trigger();

CREATE TRIGGER encounter_dx_upsert_trigger
    AFTER INSERT OR UPDATE
    ON "encounter_dx"
    FOR EACH ROW
EXECUTE PROCEDURE fn_encounters_outbox_upsert_trigger();

CREATE TRIGGER encounter_dx_delete_trigger
    AFTER DELETE
    ON "encounter_dx"
    FOR EACH ROW
EXECUTE PROCEDURE fn_encounters_outbox_delete_trigger();

CREATE TRIGGER encounter_procedures_upsert_trigger
    AFTER INSERT OR UPDATE
    ON "encounter_procedures"
    FOR EACH ROW
EXECUTE PROCEDURE fn_encounters_outbox_upsert_trigger();

CREATE TRIGGER encounter_procedures_delete_trigger
    AFTER DELETE
    ON "encounter_procedures"
    FOR EACH ROW
EXECUTE PROCEDURE fn_encounters_outbox_delete_trigger();
