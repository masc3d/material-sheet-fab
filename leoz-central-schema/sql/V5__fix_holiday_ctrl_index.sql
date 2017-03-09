ALTER TABLE `mst_holidayctrl`
  DROP INDEX `key` ,
  ADD UNIQUE INDEX `key` (`holiday` ASC, `country` ASC);