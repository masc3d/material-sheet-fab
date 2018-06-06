use dekuclient;
ALTER TABLE mst_user ADD password_expires_on DATE DEFAULT '2099-12-31' NOT NULL;
ALTER TABLE mst_user
  MODIFY COLUMN password_expires_on DATE NOT NULL DEFAULT '2099-12-31' AFTER expires_on;