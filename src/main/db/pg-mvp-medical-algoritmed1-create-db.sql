/* БД pg_mvp_medical_algoritmed1 */

CREATE SEQUENCE IF NOT EXISTS dbid;
ALTER SEQUENCE dbid RESTART WITH 1;
DROP TABLE IF EXISTS medical.m_facility_patient;
DROP TABLE IF EXISTS medical.m_facility;
DROP TABLE IF EXISTS insurance.i_facility_patient;
DROP TABLE IF EXISTS insurance.i_facility;
DROP TABLE IF EXISTS patient;
DROP TABLE IF EXISTS uuid;

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

/* МЕДИК (ЧАСНА ПРАКТИКА / ЛІКУВАЛЬНИЙ ЗАКЛАД) */
CREATE SCHEMA IF NOT EXISTS medical;

/*  лікувальні заклади */
CREATE TABLE medical.m_facility (
	m_facility_id INT PRIMARY KEY,
	m_facility_name VARCHAR(200),
	m_facility_shortname VARCHAR(20),
	m_facility_address_id INT,
	FOREIGN KEY (m_facility_address_id) REFERENCES address(address_id),
	FOREIGN KEY (m_facility_id) REFERENCES uuid(uuid_dbid)
);

/* паціент в лікувальному закладі */
CREATE TABLE medical.m_facility_patient (
	mfp_facility_id INT NOT NULL,
	mfp_patient_id INT NOT NULL ,
	FOREIGN KEY (mfp_patient_id) REFERENCES patient(patient_id),
	FOREIGN KEY (mfp_facility_id) REFERENCES m_facility(m_facility_id)
);

/* МЕДИЧНЕ СТРАХУВАННЯ */
CREATE SCHEMA IF NOT EXISTS insurance;
/* Страхові компанії або види страховки */
CREATE TABLE insurance.i_facility (
	i_facility_id INT PRIMARY KEY,
	i_facility_name VARCHAR(200),
	i_facility_shortname VARCHAR(20),
	i_facility_address_id INT,
	FOREIGN KEY (i_facility_address_id) REFERENCES address(address_id),
	FOREIGN KEY (i_facility_id) REFERENCES uuid(uuid_dbid)
);
/* страховка паціента */
CREATE TABLE insurance.i_facility_patient (
	ifp_facility_id INT NOT NULL,
	ifp_patient_id INT NOT NULL ,
	FOREIGN KEY (ifp_patient_id) REFERENCES patient(patient_id),
	FOREIGN KEY (ifp_facility_id) REFERENCES i_facility(i_facility_id)
);

