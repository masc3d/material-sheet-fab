CREATE INDEX idx_tad_node_geoposition_position_datetime
  ON tad_node_geoposition (position_datetime);

create table tad_order (
  id                                    identity,
  uid                                   uuid DEFAULT RANDOM_UUID() NOT NULL,
  orderid                               bigint default 0           not null,
  customer_reference                    varchar,
  service                               bigint default 0           not null,
  order_type                            varchar(1),
  order_status                          varchar(1),
  reference_id_to_exchange_id           bigint default 0           not null,
  customer_station                      int,
  pickup_station                        int,
  delivery_station                      int,
  cash_amount                           double default 0.0         not null,
  number_of_parcels                     int,
  weight_of_order                       double,
  pickup_information1                   varchar(80),
  pickup_information2                   varchar(40),
  delivery_information                  longtext,
  content                               varchar(50),
  pickup_address_line1                  varchar(50),
  pickup_address_line2                  varchar(50),
  pickup_address_line3                  varchar(50),
  pickup_address_street                 varchar(50),
  pickup_address_street_no              varchar(10),
  pickup_address_country_code           varchar(3),
  pickup_address_zip_code               varchar(10),
  pickup_address_city                   varchar(50),
  pickup_address_phoneNumber            varchar(20),
  delivery_address_line1                varchar(50),
  delivery_address_line2                varchar(50),
  delivery_address_line3                varchar(50),
  delivery_address_street               varchar(50),
  delivery_address_street_no            varchar(10),
  delivery_address_country_code         varchar(3),
  delivery_address_zip_code             varchar(10),
  delivery_address_city                 varchar(50),
  delivery_address_phoneNumber          varchar(20),
  appointment_pickup_start              datetime,
  appointment_pickup_end                datetime,
  appointment_pickup_not_before_start   boolean,
  appointment_delivery_start            datetime,
  appointment_delivery_end              datetime,
  appointment_delivery_not_before_start boolean,
  created                               datetime,
  is_cancelled                          boolean default false      not null

);

CREATE UNIQUE INDEX idx_tad_order_uid
  ON tad_order (uid);
create index idx_tad_order_orderid
  on tad_order (orderid);
create index idx_tad_order_customer_reference
  on tad_order (customer_reference);
create index idx_tad_order_customer_station
  on tad_order (customer_station);
create index idx_tad_order_pickup_station
  on tad_order (pickup_station);
create index idx_tad_order_delivery_station
  on tad_order (delivery_station);
create index idx_tad_order_pickup_address_line1
  on tad_order (pickup_address_line1);
create index idx_tad_order_delivery_address_zip_code
  on tad_order (delivery_address_zip_code);
create index idx_tad_order_delivery_address_city
  on tad_order (delivery_address_city);
create index idx_tad_order_cash_amount
  on tad_order (cash_amount);
create index idx_tad_order_appointment_pickup_start
  on tad_order (appointment_pickup_start);
create index idx_tad_order_service
  on tad_order (service);
create index idx_tad_order_order_status
  on tad_order (order_status);
create index idx_tad_order_reference_id_to_exchange_id
  on tad_order (reference_id_to_exchange_id);
create index idx_tad_order_is_cancelled
  on tad_order (is_cancelled);
create index idx_tad_order_created
  on tad_order (created);

create table tad_order_parcel (
  id                          identity,
  uid                         uuid DEFAULT RANDOM_UUID() NOT NULL,
  parcel_id                   bigint default 0           not null,
  orderid                     bigint default 0           not null,
  orderuid                    uuid,
  scan_id                     bigint default 0           not null,
  last_delivery_list_id       bigint,
  parcel_type                 bigint default 0           not null,
  dimension_length            int default 0              not null,
  dimension_height            int default 0              not null,
  dimension_width             int default 0              not null,
  dimension_weight_real       double default 0.0         not null,
  signature_path              varchar(100),
  delivered_status            int default 0              not null,
  last_delivered_event_reason int default 0              not null,
  pickup_station              int,
  delivery_station            int,
  is_damaged                  boolean default false      not null,
  is_cancelled                boolean default false      not null,
  initial                     int default 0              not null

);

create unique index idx_tad_order_parcel_uid
  on tad_order_parcel (uid);
create index idx_tad_order_parcel_parcel_id
  on tad_order_parcel (parcel_id);
create index idx_tad_order_parcel_orderid
  on tad_order_parcel (orderid);
create index idx_tad_order_parcel_orderuid
  on tad_order_parcel (orderuid);
create index idx_tad_order_parcel_scan_id
  on tad_order_parcel (scan_id);
create index idx_tad_order_parcel_parcel_type
  on tad_order_parcel (parcel_type);
create index idx_tad_order_parcel_pickup_station
  on tad_order_parcel (pickup_station);
create index idx_tad_order_parcel_delivery_station
  on tad_order_parcel (delivery_station);
create index idx_tad_order_parcel_is_cancelled
  on tad_order_parcel (is_cancelled);
create index idx_tad_order_parcel_delivered_status
  on tad_order_parcel (delivered_status);
create index idx_tad_order_parcel_last_delivered_event_reason
  on tad_order_parcel (last_delivered_event_reason);
