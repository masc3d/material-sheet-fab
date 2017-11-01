USE dekuclient;

CREATE OR REPLACE
  ALGORITHM = UNDEFINED
VIEW tad_v_order_parcel AS
  SELECT
    ((tblauftragcollies.OrderID * 100) + tblauftragcollies.OrderPos) AS id,
    tblauftragcollies.OrderID                                        AS order_id,
    tblauftragcollies.CollieBelegNr                                  AS scan_id,
    tblauftragcollies.RollkartennummerD                              AS last_delivery_list_id,
    tblauftragcollies.VerpackungsArt                                 AS parcel_type,
    tblauftragcollies.Laenge                                         AS dimension_length,
    tblauftragcollies.Hoehe                                          AS dimension_height,
    tblauftragcollies.Breite                                         AS dimension_width,
    tblauftragcollies.GewichtReal                                    AS dimention_weight,
    tblauftragcollies.bmpFileName                                    AS signature_path,
    tblauftragcollies.erstlieferstatus                               AS delivered_status,
    tblauftragcollies.lieferfehler                                   AS last_delivered_event_reason,
    tblauftragcollies.mydepotabd                                     AS pickup_station,
    tblauftragcollies.mydepotid2                                     AS delivery_station
  FROM
    tblauftragcollies
  WHERE
    ((tblauftragcollies.CollieBelegNr > 0)
     AND (tblauftragcollies.OrderPos > 0))