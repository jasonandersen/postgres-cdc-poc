CREATE TABLE IF NOT EXISTS encounter_status
(
    encounter_status_id SMALLINT PRIMARY KEY,
    status              VARCHAR(255) NOT NULL
);

INSERT INTO encounter_status
VALUES (100, 'NEW'),
       (200, 'IN PROGRESS'),
       (300, 'COMPLETE')
       ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS encounters
(
    encounter_id        SERIAL PRIMARY KEY,
    encounter_status_id SMALLINT,
    notes               VARCHAR(255)  DEFAULT 0     NOT NULL,
    created_on          TIMESTAMP DEFAULT NOW() NOT NULL,
    updated_on          TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT fk_encounter_status FOREIGN KEY (encounter_status_id) REFERENCES encounter_status (encounter_status_id)
);

CREATE TABLE IF NOT EXISTS encounters_outbox
(
    event_id      UUID DEFAULT uuid_generate_v1() PRIMARY KEY,
    encounter_id  INTEGER NOT NULL,
    created_on    TIMESTAMP DEFAULT NOW() NOT NULL
);
