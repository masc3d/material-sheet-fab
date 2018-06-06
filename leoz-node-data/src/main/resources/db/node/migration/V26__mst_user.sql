-- Drop `key_id` as it's superseded by `key_uid`
ALTER TABLE mst_user
    DROP COLUMN key_id;
