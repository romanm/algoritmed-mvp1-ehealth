-- Зчитування структури таблиці
SELECT d1.doc_id, s1.value tablename ,s2.value fieldname ,rs2.value fieldtype 
FROM doc d1, doc d2, doc r2, string rs2, string s1, string s2
WHERE d1.doc_id=d2.parent AND d1.doctype=1
AND s1.string_id=d1.doc_id
AND s2.string_id=d2.doc_id
AND d2.reference=r2.doc_id
AND rs2.string_id=r2.doc_id
-- 

-- 


