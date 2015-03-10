create table patient(
	name		varchar(10),
	ahcn		char(9),
	dob		date,
	primary key (healthno)
)
create table ecg(
	ahcn		char(9),
	mv		int,
	pulse		int,
	o2		int,
	diabp		int,
	sysbp		int,
	map		int,
	timestamp	time,
	ses_no	int,
	devicetype	char(10),
	primary key (ahcn)
)
create table oximiter(
	ahcn		char(9),
	pulse		int,
	o2		int,
	timestamp	time,
	ses_no	int,
	devicetype	char(10),
	primary key (ahcn)
)
create table bpressure(
	ahcn		char(9),
	diabp		int,
	sysbp		int,
	map		int,
	timestamp	time,
	ses_no	int,
	devicetype	char(10),
	primary key (ahcn)
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