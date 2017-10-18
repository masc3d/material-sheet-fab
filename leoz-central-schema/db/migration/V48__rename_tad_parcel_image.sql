use dekuclient;

ALTER TABLE tad_parcel_image RENAME TO tad_images;
ALTER TABLE tad_images ADD context VARCHAR(20) NOT NULL;
ALTER TABLE tad_images ADD order_id INT(11) NULL;
ALTER TABLE tad_images MODIFY parcel_id INT(11);
ALTER TABLE tad_images
  MODIFY COLUMN order_id INT(11) AFTER parcel_id,
  MODIFY COLUMN context VARCHAR(20) NOT NULL AFTER order_id;