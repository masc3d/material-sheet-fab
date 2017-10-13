use dekuclient;

ALTER TABLE tbldepotliste
  ADD COLUMN debitor_id INT NOT NULL DEFAULT 0,
  ADD INDEX `debitorid` (`debitor_id` ASC);

CREATE OR REPLACE
  ALGORITHM = UNDEFINED
VIEW tad_v_deliverylist AS
  SELECT
    rkkopf.rollkartennummer              AS id,
    rkkopf.rollkartendatum               AS delivery_list_date,
    rkkopf.lieferdepot                   AS delivery_station,
    (SELECT debitor_id
     FROM tbldepotliste
     WHERE depotnr = rkkopf.lieferdepot) AS debitor_id,
    rkkopf.druckzeit                     AS create_date
  FROM rkkopf;
