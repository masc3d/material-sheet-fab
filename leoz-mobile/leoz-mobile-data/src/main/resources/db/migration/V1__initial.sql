CREATE TABLE station (
    station_nr INTEGER NOT NULL PRIMARY KEY,
    address1 VARCHAR,
    address2 VARCHAR,
    billing_address1 VARCHAR,
    billing_address2 VARCHAR,
    billing_city VARCHAR,
    billing_country VARCHAR,
    billing_house_nr VARCHAR,
    billing_street VARCHAR,
    billing_zip VARCHAR,
    city VARCHAR,
    contact_person1 VARCHAR,
    contact_person2 VARCHAR,
    country VARCHAR,
    email VARCHAR,
    house_nr VARCHAR,
    mobile VARCHAR,
    phone1 VARCHAR,
    phone2 VARCHAR,
    pos_lat DOUBLE,
    pos_long DOUBLE,
    sector VARCHAR,
    service_phone1 VARCHAR,
    service_phone2 VARCHAR,
    strang INTEGER,
    street VARCHAR,
    telefax VARCHAR,
    ust_id VARCHAR,
    web_address VARCHAR,
    zip VARCHAR,
    `timestamp` TIMESTAMP NOT NULL,
    sync_id BIGINT NOT NULL
);

CREATE TABLE user (
    id INTEGER NOT NULL PRIMARY KEY,
    email VARCHAR NOT NULL,
    password VARCHAR NOT NULL,
    api_key VARCHAR NOT NULL
);

CREATE TABLE address
(
    id INTEGER PRIMARY KEY,
	line1 VARCHAR NULL,
	line2 VARCHAR NULL,
	line3 VARCHAR NULL,
	street VARCHAR NULL,
	street_no VARCHAR NULL,
	zip_code VARCHAR NULL,
	city VARCHAR NULL,
	latitude DOUBLE NULL,
	longitude DOUBLE NULL,
	phone VARCHAR NULL
);

CREATE TABLE information
(
	id INTEGER PRIMARY KEY,
	order_ref VARCHAR NULL,
	classification VARCHAR NULL,
	type VARCHAR NULL,
	value VARCHAR NULL
);

CREATE TABLE order_entity
(
	id VARCHAR NOT NULL PRIMARY KEY,
	state VARCHAR NULL,
	classification VARCHAR NULL,
	delivery_address_id INTEGER,
	delviery_date_from TIMESTAMP NULL,
    delivery_date_to TIMESTAMP NULL,
    pickup_address_id INTEGER,
    pickup_date_from TIMESTAMP NULL,
    pickup_date_to TIMESTAMP NULL,
	carrier VARCHAR NULL,
	service DOUBLE NULL,
	sort int NULL
);

CREATE TABLE parcel
(
	id VARCHAR NOT NULL PRIMARY KEY,
	label_ref VARCHAR NULL,
	`length` DOUBLE NULL,
	height DOUBLE NULL,
	width DOUBLE NULL,
	weight DOUBLE NULL
);

CREATE TABLE status
(
	id INTEGER PRIMARY KEY,
	parcel VARCHAR NULL,
	event INTEGER NULL,
	reason INTEGER NULL,
	`timestamp` TIMESTAMP NULL,
	latitude DOUBLE NULL,
	longitude DOUBLE NULL,
	recipient VARCHAR NULL,
	information VARCHAR NULL
);

CREATE TABLE gps
(
	id INTEGER PRIMARY KEY,
	latitude DOUBLE NOT NULL,
	longitude DOUBLE NOT NULL,
	`timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	speed DOUBLE NULL,
	bearing DOUBLE NULL,
	altitude DOUBLE NULL,
	accuracy DOUBLE NULL
);