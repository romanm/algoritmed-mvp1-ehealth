-- Універсальна мета-контент модель бази даних.
-- Адаптація до парадігми 1С
DROP SEQUENCE dbid IF EXISTS;
CREATE SEQUENCE dbid;

DROP TABLE IF EXISTS doctype;
CREATE TABLE doctype (
	doctype_id INTEGER PRIMARY KEY AUTO_INCREMENT,
	doctype VARCHAR(20),
	parent_id INTEGER,
	FOREIGN KEY (parent_id) REFERENCES doctype(doctype_id)
);
INSERT INTO doctype (doctype) VALUES('table');
INSERT INTO doctype (doctype) VALUES('column');
INSERT INTO doctype (doctype) VALUES('form');
INSERT INTO doctype (doctype) VALUES('row');
INSERT INTO doctype (doctype) VALUES('cell');

DROP TABLE IF EXISTS docbody;
CREATE TABLE docbody (
	docbody_id INTEGER PRIMARY KEY
	,docbody VARCHAR(100000)
	--,FOREIGN KEY (docbody_id) REFERENCES doc(doc_id) ON DELETE CASCADE ON UPDATE CASCADE
);
DROP TABLE IF EXISTS doc;
CREATE TABLE doc (
doc_id int DEFAULT NEXTVAL('dbid') primary key
,doctype  int 
,docbody  int 
, FOREIGN KEY (doctype) REFERENCES doctype (doctype_id)
, FOREIGN KEY (docbody) REFERENCES docbody (docbody_id) ON DELETE CASCADE ON UPDATE CASCADE
);
ALTER TABLE docbody ADD FOREIGN KEY (docbody_id) REFERENCES doc(doc_id);

DROP TABLE IF EXISTS doctimestamp;
CREATE TABLE doctimestamp (
	doctimestamp_id INTEGER PRIMARY KEY AUTO_INCREMENT
	,created TIMESTAMP
	,updated TIMESTAMP
	,FOREIGN KEY (doctimestamp_id) REFERENCES doc(doc_id) ON DELETE CASCADE ON UPDATE CASCADE
);

