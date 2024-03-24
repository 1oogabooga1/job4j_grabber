create table post(
id serial primary key, 
	name TEXT, 
	text TEXT, 
	link TEXT UNIQUE, 
	created TIMESTAMP
);