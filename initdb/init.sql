CREATE ROLE beadando WITH
	LOGIN
	SUPERUSER
	CREATEDB
	CREATEROLE
	INHERIT
	REPLICATION
	CONNECTION LIMIT -1
	PASSWORD '12345678';

CREATE TABLE department (
id serial PRIMARY KEY,
firstname VARCHAR(100) NOT NULL,
lastname VARCHAR(100) NOT NULL,
country VARCHAR(100) NOT NULL
);

INSERT INTO department (firstname, lastname, country) values ('Teszt', 'Elek', 'HU');
INSERT INTO department (firstname, lastname, country) values ('Szuper', 'Áron', 'AU');