USE dekuclient;
CREATE OR REPLACE
  ALGORITHM = UNDEFINED
VIEW tad_v_deliverylistinfo AS
  SELECT
    rkkopf.rollkartennummer AS id,
    rkkopf.rollkartendatum  AS delivery_list_date
  FROM rkkopf;

CREATE OR REPLACE
  ALGORITHM = UNDEFINED
VIEW tad_v_deliverylist AS
  SELECT
    rkkopf.rollkartennummer AS id,
    rkkopf.rollkartendatum  AS delivery_list_date
  FROM rkkopf;

CREATE OR REPLACE
  ALGORITHM = UNDEFINED
VIEW tad_v_deliverylist_details AS
  SELECT
    rkdetails.rollkartennummer AS id,
    rkdetails.reihenfolge      AS order_position,
    rkdetails.OrderID          AS order_id,
    rkdetails.packstuecknummer AS number
  FROM rkdetails
  ORDER BY rkdetails.reihenfolge;
