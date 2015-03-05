create table patient(
	sin		char(9),
	lastname	varchar(10),
	firstname	varchar(10),
	healthno	char(9),
	dob		date,
	gender	char(1),
	primary key (sin),
	
)

create table session(
	ecgpeak	float,
	interval	float,
	sample	float,
	systolic	float,
	dystolic	float,
	map		float,
	o2		float,
	bloodo2	float,
	timestamp	time,
	datestamp	date,
	primary key (timestamp)
)
create table account(
	username	varchar(10),
	password	varchar(20),
	primary key (password)
)	