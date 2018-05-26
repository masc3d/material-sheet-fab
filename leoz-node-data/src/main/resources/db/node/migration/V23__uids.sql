ALTER TABLE mst_user
    ADD COLUMN uid UUID DEFAULT RANDOM_UUID() AFTER id;
CREATE UNIQUE INDEX ix_mst_user_uid ON mst_user (uid);

ALTER TABLE mst_user
    ADD COLUMN sync_id BIGINT NULL DEFAULT NULL;
CREATE UNIQUE INDEX ix_mst_user_sync_id ON mst_user(sync_id);

ALTER TABLE mst_user
    ADD COLUMN key_uid UUID AFTER key_id;
CREATE UNIQUE INDEX ix_mst_user_key_uid ON mst_user(key_uid);

ALTER TABLE mst_key
    ADD COLUMN uid UUID DEFAULT RANDOM_UUID() AFTER id;
CREATE UNIQUE INDEX ix_mst_key_uid ON mst_key (uid);

ALTER TABLE mst_key
    ADD COLUMN sync_id BIGINT NULL DEFAULT NULL;
CREATE UNIQUE INDEX ix_mst_key_sync_id ON mst_key (sync_Id);

ALTER TABLE mst_node
    ADD COLUMN sync_id BIGINT NULL DEFAULT NULL;
CREATE UNIQUE INDEX ix_mst_node_sync_id ON mst_node (sync_Id);

