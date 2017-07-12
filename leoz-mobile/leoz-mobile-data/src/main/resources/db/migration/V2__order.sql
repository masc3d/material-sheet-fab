CREATE TABLE address
(
    id INTEGER PRIMARY KEY,
	classification VARCHAR NULL,
	order_ref VARCHAR NULL,
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

CREATE TABLE appointment
(
	id INTEGER PRIMARY KEY,
	order_ref VARCHAR NULL,
	classification VARCHAR NULL,
	date_from timestamp NULL,
	date_to timestamp NULL
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