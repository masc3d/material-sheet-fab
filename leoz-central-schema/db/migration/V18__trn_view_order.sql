USE dekuclient;
CREATE OR REPLACE ALGORITHM = UNDEFINED
VIEW trn_v_order AS
  SELECT
    OrderID AS id,
    Service,
    'DEKU' as Carrier,
    ROrderID as referenceIDToExchangeOrderID,
    DepotNrAD as delivery_station,
    dtAuslieferung as appointment_delivery_day
  FROM
    tblauftrag;
