use dekuclient;

CREATE TABLE tad_parcel_image
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    parcel_id INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    uid VARCHAR(60) NOT NULL,
    name VARCHAR(60) NOT NULL,
    ts_taken TIMESTAMP NOT NULL, /* When has the image been created/taken? */
    ts_processed TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL /* When has the image been processed from WorkDirectory to target directory and matched to an parcel. (Creation of the datarow) */
);