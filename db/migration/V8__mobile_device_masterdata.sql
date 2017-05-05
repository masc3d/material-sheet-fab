use dekuclient;

CREATE TABLE mst_mobile_device
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    serial VARCHAR(50) NOT NULL,
    imei VARCHAR(15),
    manufacturer VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20),
    company_device BOOLEAN DEFAULT FALSE  NOT NULL,
    chargeable BOOLEAN DEFAULT FALSE  NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE UNIQUE INDEX mst_mobile_device_serial_uindex ON mst_mobile_device (serial);

create table mst_mobile_deviceconfiguration
(
	id int not null auto_increment
		primary key,
	device_id int not null,
	schema_version int not null,
	configuration blob null,
	constraint mst_mobile_deviceconfiguration__schema unique (schema_version, device_id)
);