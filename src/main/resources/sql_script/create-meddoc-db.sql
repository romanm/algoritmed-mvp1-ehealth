CREATE SEQUENCE dbid;

DROP TABLE IF EXISTS uuid;
CREATE TABLE uuid (
	uuid_dbid INTEGER NOT NULL DEFAULT NEXTVAL('dbid')  PRIMARY KEY,
	uuid_uuid VARCHAR(36) NOT NULL
) ;

CREATE TABLE "icd" (
	"icd_id" INTEGER PRIMARY KEY AUTO_INCREMENT,
	"icd_root" INTEGER NOT NULL,
	"icd_left_key" INTEGER NOT NULL,
	"icd_right_key" INTEGER NOT NULL,
	"icd_level" INTEGER NOT NULL,
	"icd_start" INTEGER NOT NULL,
	"icd_end" INTEGER,
	"icd_code" VARCHAR(7) NOT NULL UNIQUE,
	"icd_name" VARCHAR(255) NOT NULL,
) ;
CREATE UNIQUE INDEX PRIMARY_KEY_1 ON PUBLIC."icd" (icd_id) ;

CREATE TABLE "icd10uatree" (
	"icd10uatree_id" INTEGER PRIMARY KEY,
	"icd10uatree_parent_id" INTEGER NOT NULL,
	FOREIGN KEY ("icd10uatree_id") REFERENCES "icd"("icd_id")
) ;
--------doc----------------------------

DROP TABLE IF EXISTS "docchecked";
DROP TABLE IF EXISTS "doctimestamp";
DROP TABLE IF EXISTS "docbody";
DROP TABLE IF EXISTS "doc";
DROP TABLE IF EXISTS "doctype";
CREATE TABLE doctype (
	doctype_id INTEGER PRIMARY KEY AUTO_INCREMENT,
	doctype VARCHAR(20),
	parent_id INTEGER,
	FOREIGN KEY (parent_id) REFERENCES doctype(doctype_id)
) ;
CREATE TABLE "docbody" (
	"docbody_id" INTEGER  PRIMARY KEY,
	"docbody" VARCHAR(100000)
	--,FOREIGN KEY ("docbody_id") REFERENCES "doc"("doc_id")
) ;
CREATE TABLE "doc" (
	"doc_id" INTEGER DEFAULT NEXTVAL('dbid') PRIMARY KEY ,
	"doctype" INTEGER,
	"docbody" INTEGER,
	"parent_id" INTEGER,
	"reference" INTEGER,
	"removed" BOOLEAN DEFAULT FALSE
	,FOREIGN KEY ("doctype") REFERENCES "doctype"("doctype_id"),
	FOREIGN KEY ("docbody") REFERENCES "docbody"("docbody_id"),
	FOREIGN KEY ("parent_id") REFERENCES "doc"("doc_id"),
	FOREIGN KEY ("reference") REFERENCES "doc"("doc_id")
) ;
ALTER TABLE docbody ADD FOREIGN KEY (docbody_id) REFERENCES doc(doc_id);
INSERT INTO "doc" ("doc_id") SELECT UUID_dbid FROM "uuid";
ALTER TABLE uuid ADD FOREIGN KEY ("uuid_dbid") REFERENCES "doc"("doc_id");
--ALTER TABLE doc ADD FOREIGN KEY (docbody) REFERENCES docbody (docbody_id);

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
--------doc----------------------------END
--------protocol----------------------------
DROP TABLE IF EXISTS protocol;
CREATE TABLE protocol (
	protocol_id INT DEFAULT NEXTVAL('dbid') PRIMARY KEY
	,protocol_name VARCHAR(100)
	,protocol_doc VARCHAR(100000)
	,removed BOOLEAN DEFAULT FALSE
);
ALTER TABLE "protocol" ADD COLUMN "doctype" integer DEFAULT 2;
ALTER TABLE "protocol" ADD FOREIGN KEY ("doctype") REFERENCES "doctype"("doctype_id");

DROP TABLE IF EXISTS protocoldd2icd10;
CREATE TABLE protocoldd2icd10 (
	protocoldd INTEGER NOT NULL,
	icd10 INTEGER NOT NULL,
	doctype INTEGER NOT NULL DEFAULT 7,
	FOREIGN KEY (protocoldd) REFERENCES doc(doc_id) ON DELETE CASCADE,
	FOREIGN KEY (doctype) REFERENCES doctype(doctype_id),
	FOREIGN KEY (icd10) REFERENCES icd(icd_id)
);

--------protocol----------------------------END
--------icpc2----------------------------
DROP TABLE IF EXISTS icpc2consider;
CREATE TABLE icpc2consider(
	icpc2_code VARCHAR(3),
	icpc2_code_consider VARCHAR(3),
	consider VARCHAR(200),
	FOREIGN KEY (icpc2_code) REFERENCES demo_icpc2_ua(code),
	FOREIGN KEY (icpc2_code_consider) REFERENCES demo_icpc2_ua(code)
);
DROP TABLE IF EXISTS icpc2inclusion;
CREATE TABLE icpc2inclusion (
	icpc2_code VARCHAR(3),
	inclusion VARCHAR(400),
	FOREIGN KEY (icpc2_code) REFERENCES demo_icpc2_ua(code),
);
--------icpc2----------------------------END


