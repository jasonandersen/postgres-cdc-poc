----------------------------------------------------------
-- Schema to experiment with trigger-based change data
-- capture patterns to throw domain events from data
-- mutation events.
--
-- Jason Andersen
-- January 19, 2023
----------------------------------------------------------

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
    patient_id          VARCHAR(255)               NOT NULL,
    encounter_status_id SMALLINT,
    notes               VARCHAR(255) DEFAULT 0     NOT NULL,
    created_on          TIMESTAMP    DEFAULT NOW() NOT NULL,
    updated_on          TIMESTAMP    DEFAULT NOW() NOT NULL,
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

CREATE TABLE IF NOT EXISTS encounters_outbox
(
    event_id     UUID      DEFAULT uuid_generate_v1() PRIMARY KEY,
    encounter_id INTEGER                 NOT NULL,
    created_on   TIMESTAMP DEFAULT NOW() NOT NULL
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

