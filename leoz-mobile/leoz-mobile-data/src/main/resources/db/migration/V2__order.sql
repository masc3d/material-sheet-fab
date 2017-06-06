create table address
(
	id int
		primary key,
	classification varchar(30) null,
	order_ref varchar(50) null,
	addressLine1 varchar(50) null,
	addressLine2 varchar(50) null,
	street varchar(50) null,
	streetNo varchar(50) null,
	zipCode varchar(50) null,
	city varchar(50) null,
	latitude double null,
	longitude double null
)
;

create table appointment
(
	id int 
		primary key,
	order_ref varchar(50) null,
	classification varchar(30) null,
	dateFrom timestamp null,
	dateTo timestamp null
)
;

create table information
(
	id int 
		primary key,
	order_ref varchar(50) null,
	classification varchar(30) null,
	type varchar(20) null,
	value varchar(100) null
)
;

create table orders
(
	id varchar(50) not null
		primary key,
	state varchar(10) null,
	classification varchar(20) null,
	carrier varchar(20) null,
	service double null,
	sort int null
)
;

create table parcel
(
	id varchar(50) not null
		primary key,
	labelReference varchar(30) null,
	length double null,
	height double null,
	width double null,
	weight double null
)
;

create table status
(
	id int 
		primary key,
	parcel varchar(50) null,
	event integer null,
	reason integer null,
	date timestamp null,
	latitude double null,
	longitude double null,
	recipient varchar(50) null,
	information varchar(50) null
)
;

create table position
(
	id int 
		primary key,
	latitude double not null,
	longitude double not null,
	time timestamp default CURRENT_TIMESTAMP not null,
	speed double null,
	bearing double null,
	altitude double null,
	accuracy double null
)
;