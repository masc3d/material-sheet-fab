CREATE TABLE mst_node
(
  id              IDENTITY,
  sys_info        VARCHAR                             NULL,
  key             VARCHAR                             NULL,
  authorized      INT                                 NULL,
  config          VARCHAR                             NULL,
  version_alias   VARCHAR DEFAULT 'release'           NOT NULL,
  bundle          VARCHAR                             NOT NULL,
  serial          VARCHAR                             NULL,
  current_version VARCHAR                             NULL,
  ts_modified     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  ts_lastlogin    TIMESTAMP                           NULL
);