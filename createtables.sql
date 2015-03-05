create table patient(
	sin		char(9),
	lastname	varchar(10),
	firstname	varchar(10),
	healthno	char(9),
	primary key (sin)
)

create table session(
	ecg		float,
	o2		float,
	bloodo2	float,
	sestime		time,
	sesdate		date,
	primary key (time)
)
create table account(
	username	varchar(10),
	password	varchar(20),
	primary key (password)
)	