USE `dekuclient`;

ALTER TABLE mst_user MODIFY password VARCHAR(255) NOT NULL;
ALTER TABLE mst_user MODIFY expires_on DATE DEFAULT '2099-12-31';

ALTER TABLE mst_user ADD email VARCHAR(100) NOT NULL;
CREATE UNIQUE INDEX mst_user_email_uindex ON mst_user (email);
DROP INDEX user_name_UNIQUE ON dekuclient.mst_user;
ALTER TABLE mst_user ADD debitor_id INT NOT NULL;
ALTER TABLE mst_user
  MODIFY COLUMN email VARCHAR(100) NOT NULL AFTER user_name,
  MODIFY COLUMN debitor_id INT NOT NULL AFTER email;
  
CREATE UNIQUE INDEX mst_username_index ON mst_user (user_name, debitor_id);
ALTER TABLE mst_user ADD role VARCHAR(20) DEFAULT 'USER' NOT NULL;
ALTER TABLE mst_user
  MODIFY COLUMN debitor_id INT(11) NOT NULL AFTER user_id,
  MODIFY COLUMN email VARCHAR(100) NOT NULL AFTER user_name,
  MODIFY COLUMN role VARCHAR(20) NOT NULL AFTER email,
  MODIFY COLUMN expires_on DATE DEFAULT '2099-12-31' AFTER active;
ALTER TABLE mst_user CHANGE user_id id INT(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE mst_user CHANGE user_name alias VARCHAR(30) NOT NULL;
  
  
# Table mst_user_configuration

CREATE TABLE mst_user_configuration
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    schema_version INT NOT NULL,
    user_id INT NOT NULL,
    configuration BLOB
);
CREATE UNIQUE INDEX mst_user_configuration_user_id_schema_version_uindex ON mst_user_configuration (user_id, schema_version);

# Table sys_value

DROP TABLE sys_values;

CREATE TABLE mst_user_scope
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    station_id INT COMMENT 'Refering the station by it`s unique id, not the operative identifier'
);