USE `dekuclient`;
ALTER TABLE tad_parcel_messages
    Add COLUMN `create_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;