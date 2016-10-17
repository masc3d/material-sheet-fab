CREATE TABLE mst_bundle_version (
  id      INTEGER NOT NULL IDENTITY PRIMARY KEY,
  bundle  VARCHAR DEFAULT NULL,
  alias   VARCHAR DEFAULT NULL,
  version VARCHAR DEFAULT NULL,
  UNIQUE (alias, bundle)
)