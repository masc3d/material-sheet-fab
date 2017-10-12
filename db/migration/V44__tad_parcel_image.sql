use dekuclient;

CREATE TABLE tad_parcel_image
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    parcel_id INT NOT NULL,
    image_type VARCHAR(20) NOT NULL,
    image_uid VARCHAR(60) NOT NULL,
    image_name VARCHAR(60) NOT NULL,
    image_timestamp TIMESTAMP NOT NULL,
    ts_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);