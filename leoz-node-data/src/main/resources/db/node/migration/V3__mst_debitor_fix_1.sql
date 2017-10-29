--- Fixes synchronization issue, as `ts_updated` is nulltable in central db
ALTER TABLE mst_debitor ALTER COLUMN ts_updated SET NULL;