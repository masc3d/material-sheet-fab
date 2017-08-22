USE `dekuclient`;
ALTER TABLE `dekuclient`.`mst_user`
  ADD UNIQUE INDEX `mst_key_uindex` (`key_id` ASC);
