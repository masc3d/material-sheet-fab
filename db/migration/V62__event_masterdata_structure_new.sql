use dekuclient;

create table mst_event
(
	eventcode varchar(15) not null
		primary key,
	event_name varchar(50) not null,
	event_text_id int null,
	constraint mst_event_event_name_uindex
		unique (event_name)
)
;

create table mst_event_reason
(
	id int auto_increment
		primary key,
	eventcode varchar(15) not null,
	reasoncode varchar(15) not null,
	publish_level varchar(10) default 'PRIVATE' not null,
	text_id int not null,
	public_text_id int null,
	valid_in_hub tinyint(1) default '0' not null,
	valid_in_linehaul tinyint(1) default '0' not null,
	valid_in_importstation tinyint(1) default '0' not null,
	valid_in_exportstation tinyint(1) default '0' not null,
	status_old int null,
	reason_old int null,
	gls_event varchar(10) null,
	gls_reason varchar(20) null,
	gls_trace_id int null
)
;

create table mst_reason
(
	reasoncode varchar(15) not null
		primary key,
	reason_name varchar(50) not null,
	reason_text_id int null,
	constraint mst_reason_reason_name_uindex
		unique (reason_name)
)
;

