CREATE TABLE doctype (
doctype_id int auto_increment primary key
,doctype_name varchar(10)
);
ALTER TABLE doctype ALTER COLUMN doctype_name  RENAME TO doctype;
ALTER TABLE doctype ADD COLUMN parent_id INT;
ALTER TABLE doctype ADD FOREIGN KEY (parent_id) REFERENCES (doctype_id);
DROP TABLE IF EXISTS docbody;
CREATE TABLE docbody (
	docbody_id INTEGER PRIMARY KEY
	,docbody VARCHAR(100000)
	,FOREIGN KEY (docbody_id) REFERENCES doc(doc_id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE doc (
doc_id int DEFAULT NEXTVAL('dbid') primary key
,doctype  int 
,docbody  int 
, FOREIGN KEY (doctype) REFERENCES doctype (doctype_id)
, FOREIGN KEY (docbody) REFERENCES docbody (docbody_id)
);
-- ALTER TABLE doc ADD FOREIGN KEY (docbody) REFERENCES docbody (docbody_id);

CREATE TABLE doctimestamp (
	doctimestamp_id INTEGER PRIMARY KEY AUTO_INCREMENT
	,created TIMESTAMP
	,updated TIMESTAMP
	,FOREIGN KEY (doctimestamp_id) REFERENCES doc(doc_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE docchecked (
	docchecked_id INTEGER PRIMARY KEY AUTO_INCREMENT
	,checked TIMESTAMP
	,FOREIGN KEY (docchecked_id) REFERENCES doctimestamp(doctimestamp_id) ON DELETE CASCADE ON UPDATE CASCADE
);
-- ALTER TABLE "doctimestamp" ALTER COLUMN doctimestamp RENAME TO created;
-- ALTER TABLE "doctimestamp" add COLUMN updated TIMESTAMP;
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

CREATE TABLE "icd" (
	"icd_id" INTEGER PRIMARY KEY  AUTO_INCREMENT,
	"icd_root" INTEGER NOT NULL,
	"icd_left_key" INTEGER NOT NULL,
	"icd_right_key" INTEGER NOT NULL,
	"icd_level" INTEGER NOT NULL,
	"icd_start" INTEGER NOT NULL,
	"icd_end" INTEGER,
	"icd_code" VARCHAR(7) NOT NULL UNIQUE,
	"icd_name" VARCHAR(255) NOT NULL
) ;
DROP TABLE IF EXISTS icd10uatree;
CREATE TABLE "icd10uatree" (
	"icd10uatree_id" INTEGER PRIMARY KEY,
	"icd10uatree_parent_id" INTEGER NOT NULL
	, FOREIGN KEY ("icd10uatree_id") REFERENCES "icd"("icd_id")
	, FOREIGN KEY (icd10uatree_parent_id) REFERENCES icd10uatree(icd10uatree_id)
) ;

