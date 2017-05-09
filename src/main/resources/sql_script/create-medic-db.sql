CREATE TABLE doctype (
doctype_id int auto_increment primary key
,doctype_name varchar(10)
);
ALTER TABLE doctype ALTER COLUMN doctype_name  RENAME TO doctype;
CREATE TABLE docbody (
docbody_id int DEFAULT NEXTVAL('dbid') primary key
,docbody  VARCHAR(100000)
);
CREATE TABLE doc (
doc_id int DEFAULT NEXTVAL('dbid') primary key
,doctype  int 
,docbody  int 
, FOREIGN KEY (doctype) REFERENCES doctype (doctype_id)
, FOREIGN KEY (docbody) REFERENCES docbody (docbody_id)
);
CREATE TABLE doctymestamp (
doctymestamp_id int auto_increment primary key
,doctymestamp TIMESTAMP
, FOREIGN KEY (doctymestamp_id) REFERENCES doc(doc_id)
);
ALTER TABLE patient ADD COLUMN doctype int NOT null DEFAULT 1;
ALTER TABLE patient ADD FOREIGN KEY (doctype) REFERENCES doctype(doctype_id);
ALTER TABLE patient ADD FOREIGN KEY (patient_id,doctype) REFERENCES doc(doc_id,doctype);
ALTER TABLE address ADD FOREIGN KEY (address_id,doctype) REFERENCES doc(doc_id,doctype);
ALTER TABLE uuid ADD FOREIGN KEY (uuid_dbid) REFERENCES doc(doc_id);
ALTER TABLE patient DROP COLUMN patient_address_id;
ALTER TABLE doc ADD COLUMN parent_id INT;
ALTER TABLE doc ADD FOREIGN KEY (parent_id) REFERENCES (doc_id);
ALTER TABLE doc ADD COLUMN reference INT;
ALTER TABLE doc ADD COLUMN removed BOOLEAN DEFAULT FALSE;
ALTER TABLE doc ADD FOREIGN KEY (reference,doctype) REFERENCES (doc_id,doctype);
ALTER TABLE address ADD COLUMN doctype INT NOT null DEFAULT 4;
ALTER TABLE address ADD FOREIGN KEY (doctype) REFERENCES doctype(doctype_id);
ALTER TABLE docbody ADD FOREIGN KEY (docbody_id) REFERENCES doc(doc_id);

