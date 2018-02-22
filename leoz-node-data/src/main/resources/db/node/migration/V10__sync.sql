--- Database sync support table for notify presets, storing central sync ids
CREATE TABLE lcl_sync (
  id         IDENTITY,
  table_name VARCHAR,
  sync_id    BIGINT
);

CREATE UNIQUE INDEX ix_lcl_sync_table_name
  ON lcl_sync(table_name);
