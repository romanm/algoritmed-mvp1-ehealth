/* БД h2_mvp_medical_algoritmed1 */

CREATE SEQUENCE IF NOT EXISTS dbid;
/* init script < 10 */
ALTER SEQUENCE dbid RESTART WITH 10;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS patient;
DROP TABLE IF EXISTS address;
DROP TABLE IF EXISTS uuid;

/* аутентифікація */
CREATE TABLE users (
  username VARCHAR(45) NOT NULL,
  password VARCHAR(45) NOT NULL,
  enabled boolean NOT NULL DEFAULT true,
  PRIMARY KEY (username)
);

CREATE TABLE user_roles (
  user_role_id int DEFAULT NEXTVAL('dbid') PRIMARY KEY,
  username VARCHAR(45) NOT NULL,
  role VARCHAR(45) NOT NULL,
  UNIQUE (role,username),
  CONSTRAINT fk_username FOREIGN KEY (username) REFERENCES users (username)
);

/* uuid для розпреділеної БД */
CREATE TABLE uuid (
	uuid_dbid INT DEFAULT NEXTVAL('dbid') PRIMARY KEY,
	uuid_uuid VARCHAR(36) NOT NULL,
	UNIQUE (uuid_uuid)
);

CREATE TABLE address(
	address_id INT PRIMARY KEY,
	address_address VARCHAR(200),
	FOREIGN KEY (address_id) REFERENCES uuid(uuid_dbid)
);

/* паціент на лікуванні */
CREATE TABLE patient (
	patient_id INT PRIMARY KEY,
	patient_pib VARCHAR(200),
	patient_address_id INT,
	patient_dob DATE,
	FOREIGN KEY (patient_address_id) REFERENCES address(address_id),
	FOREIGN KEY (patient_id) REFERENCES uuid(uuid_dbid)
);

