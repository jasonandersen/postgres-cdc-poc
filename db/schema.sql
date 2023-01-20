CREATE TABLE IF NOT EXISTS cpt_codes
(
    cpt_code    VARCHAR(16),
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS icd_codes
(
    icd_code    VARCHAR(16),
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
    encounter_status_id SMALLINT,
    notes               VARCHAR(255) DEFAULT 0     NOT NULL,
    created_on          TIMESTAMP    DEFAULT NOW() NOT NULL,
    updated_on          TIMESTAMP    DEFAULT NOW() NOT NULL,
    CONSTRAINT fk_encounter_status FOREIGN KEY (encounter_status_id) REFERENCES encounter_status (encounter_status_id)
);

CREATE TABLE IF NOT EXISTS encounter_procedures
(
    encounter_id INTEGER,
    icd_code     VARCHAR(16),
    created_on   TIMESTAMP DEFAULT NOW() NOT NULL,
    PRIMARY KEY (encounter_id, icd_code),
    CONSTRAINT fk_encounter_procedures_encounter_id FOREIGN KEY (encounter_id) REFERENCES encounters (encounter_id),
    CONSTRAINT fk_encounter_procedures_icd_code FOREIGN KEY (icd_code) REFERENCES icd_codes (icd_code)
);

CREATE TABLE IF NOT EXISTS encounter_dx
(
    encounter_id INTEGER,
    cpt_code     VARCHAR(16),
    created_on   TIMESTAMP DEFAULT NOW() NOT NULL,
    PRIMARY KEY (encounter_id, cpt_code),
    CONSTRAINT fk_encounter_dx_encounter_id FOREIGN KEY (encounter_id) REFERENCES encounters (encounter_id),
    CONSTRAINT fk_encounter_dx_cpt_code FOREIGN KEY (cpt_code) REFERENCES cpt_codes (cpt_code)
);

CREATE TABLE IF NOT EXISTS encounters_outbox
(
    event_id     UUID      DEFAULT uuid_generate_v1() PRIMARY KEY,
    encounter_id INTEGER                 NOT NULL,
    created_on   TIMESTAMP DEFAULT NOW() NOT NULL
);

-- Lookup data

INSERT INTO encounter_status
VALUES (100, 'NEW'),
       (200, 'IN PROGRESS'),
       (300, 'COMPLETE')
ON CONFLICT DO NOTHING;

INSERT INTO cpt_codes VALUES('86152', 'Cell enumeration &id');
INSERT INTO cpt_codes VALUES('86153', 'Cell enumeration phys interp');
INSERT INTO cpt_codes VALUES('86890', 'Autologous blood process');
INSERT INTO cpt_codes VALUES('86891', 'Autologous blood op salvage');
INSERT INTO cpt_codes VALUES('86927', 'Plasma fresh frozen');
INSERT INTO cpt_codes VALUES('86930', 'Frozen blood prep');
INSERT INTO cpt_codes VALUES('86931', 'Frozen blood thaw');
INSERT INTO cpt_codes VALUES('86932', 'Frozen blood freeze/thaw');
INSERT INTO cpt_codes VALUES('86945', 'Blood product/irradiation');
INSERT INTO cpt_codes VALUES('86950', 'Leukacyte transfusion');
INSERT INTO cpt_codes VALUES('86960', 'Vol reduction of blood/prod');
INSERT INTO cpt_codes VALUES('86965', 'Pooling blood platelets');
INSERT INTO cpt_codes VALUES('86985', 'Split blood or products');

