create table patient(
	name		varchar(10),
	ahcn		char(9),
	dob		date,
	livestatus	boolean,
	doctor	varchar(15),
	pid		char(10),
	primary key (pid),
	unique key (ahcn)
)
create table ecg(
	pid			char(10),
	mv			int,
	pulse			int,
	o2			int,
	diastolicbp	int,
	systolicbp		int,
	map			int,
	timestamp		time,
	session_id		int,
	devicetype		char(10),
	primary key (pid),
	foreign key (pid) references patient
)
create table oximiter(
	pid			char(10),
	pulse			int,
	oxygen		int,
	timestamp		time,
	session_id		int,
	devicetype		char(10),
	primary key (pid),
	foreign key (pid) references patient
)
create table bpressure(
	pid			char(10),
	diastolicbp	int,
	systolicbp		int,
	map			int,
	timestamp		time,
	session_id		int,
	devicetype		char(10),
	primary key (pid),
	foreign key (pid) references patient
)
create table account(
	username	varchar(10),
	password	varchar(20),
	primary key (username)
)	


(Unsure if session is relevant or not.  Feel free to ignore or delete it if necessary.)
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