USE dekuclient;

ALTER TABLE `tbldepotliste`
  ADD COLUMN `software` INT(11) NOT NULL DEFAULT 0;

ALTER TABLE `tblhublinien`
  ADD COLUMN `tracking_opt_out` INT(11) NOT NULL DEFAULT 0;

ALTER TABLE `hubcounttmp`
  ADD COLUMN `parcel_id` INT(11) NOT NULL DEFAULT 0
  FIRST,
  ADD PRIMARY KEY (`parcel_id`);
