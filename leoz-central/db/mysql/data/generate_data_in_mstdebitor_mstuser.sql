delete from mst_debitor;

insert into mst_debitor (debitor_nr) select distinct DebitorNr from tbldepotliste where debitornr >1000 ;
UPDATE (mst_station INNER JOIN tbldepotliste ON mst_station.station_nr = tbldepotliste.DepotNr)
  INNER JOIN mst_debitor ON tbldepotliste.DebitorNr = mst_debitor.debitor_nr
SET mst_station.debitor_id = mst_debitor.debitor_id;

delete from mst_user where email='user@deku.org';
INSERT INTO mst_user (debitor_id, email, password,alias,role,salt,active,firstname,lastname) VALUES ('4117', 'user@deku.org', 'password','testuser','POWERUSER','x',-1,'Hans','Mustermann');

/**

Debitor 4117 / Station 20

{
  "user": {
    "email": "user@deku.org",
    "password": "password"
  },
  "mobile": {
    "model": "CT-50",
    "serial": "ABCDEFGH",
    "imei": "990000862471854"
  }
}


**/