use dekuclient;

CREATE TABLE his_user_activity
(
    id INT PRIMARY KEY NOT NULL,
    user_id INT NOT NULL,
    activity VARCHAR(100) NOT NULL,
    ts_activity TIMESTAMP NOT NULL,
    ts_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);