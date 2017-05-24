package org.algoritmed.mvp1.medic;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.algoritmed.mvp1.DbAlgoritmed;
import org.algoritmed.mvp1.util.HashMap2;
import org.algoritmed.mvp1.util.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * REST доступ для роботи медиків з пацієнтами.
 * @author roman
 *
 */
@Controller
public class MedicalPatientRest  extends DbAlgoritmed{
	private static final Logger logger = LoggerFactory.getLogger(MedicalPatientRest.class);
	
	/**
	 * SQL select для зчитування всіх пацієнтів медіка
	 */
	private @Value("${sql.medical.selectPatients}") String sqlMedicalSelectPatients;
	/**
	 * Зчитування всіх пацієнтів медичної установи
	 * @return Map об'єкт що містить всіх пацієнтів медіка
	 */
	@GetMapping(value = "/r/medical/patients")
	public @ResponseBody Map<String, Object>  patients() {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> medicPatients = db1JdbcTemplate.queryForList(sqlMedicalSelectPatients);
		map.put("medicPatients", medicPatients);
		return map;
	}
	
	private @Value("${sql.medical.selectPatientById}")	String sql_medical_selectPatientById;
	private @Value("${sql.doc.children}")				String sql_doc_children;
	private @Value("${sql.doc.children.children}")		String sql_doc_children_children;
	@GetMapping(value = "/r/medical/patient/{patient_id}")
	public @ResponseBody Map<String, Object>  patient(@PathVariable Integer patient_id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("patient_id", patient_id);
		map.put("doc_id", patient_id);
		logger.info("---------------\n"
				+ "/r/medical/patient/{patient_id}"
				+ "\n" + sql_medical_selectPatientById.replace(":patient_id", ""+patient_id)
				+ "\n" + sql_doc_children.replace(":parent_id", ""+patient_id)
				+ "\n" + map
				);
		Map<String, Object> patientById = getMap(sql_medical_selectPatientById, map);
		HashMap<Integer, Object> docIdMap = new HashMap<>();
		HashMap<Integer, Object> docIdMapPath = new HashMap<>();
		patientById.put("docIdMap", docIdMapPath);
		docIdMap.put(patient_id, patientById);
		docIdMapPath.put(patient_id, "patient");
		List<Map<String, Object>> children = getList(sql_doc_children, map);
		int index=0;
		for (Map<String, Object> map2 : children) {
			docbodyToMap(map2);
			Integer doc_id = (Integer) map2.get("doc_id");
			docIdMap.put(doc_id, map2);
			docIdMapPath.put(doc_id, "children[" + index++ + "]");
		}
		List<Map<String, Object>> childrenChildren = getList(sql_doc_children_children, map);
		for (Map<String, Object> map2 : childrenChildren) {
			docbodyToMap(map2);
			Integer parent_id = (Integer) map2.get("parent_id");
			Map parentElement = (Map) docIdMap.get(parent_id);
			List<Map<String, Object>> childrenList = (List) parentElement.get("children");
			if(childrenList == null){
				childrenList = new ArrayList<>();
				parentElement.put("children", childrenList);
			}
			childrenList.add(map2);
		}
		patientById.put("children", children);
		map.put("patientById", patientById);
		System.err.println(map);
		System.err.println(map.get("docIdMap"));
		return map;
	}

	@PostMapping("/r/autoSaveHistory")
	public @ResponseBody Map<String, Object> autoSaveHistory(
			@RequestBody Map<String, Object> dbSaveObj
			, Principal userPrincipal) {
		HashMap2 dbSaveObj2 = new HashMap2(dbSaveObj).put("url", "/r/addHistoryRecord");
		logger.info("\n---------------\n"
				+ "/r/autoSaveHistory"
				+ "\n" + dbSaveObj2
				);
		//update docbody
		Map docbodyMap = dbSaveObj2.getMap("docbody");
		updateDocbody(dbSaveObj2 , docbodyMap);
		return dbSaveObj2;
	}
	@PostMapping("/r/addHistoryRecord")
	public @ResponseBody Map<String, Object> addHistoryRecord(
			@RequestBody Map<String, Object> dbSaveObj
			, Principal userPrincipal) {
		dbSaveObj.put("url", "/r/addHistoryRecord");
		logger.info("\n---------------\n"
				+ "/r/addHistoryRecord"
				+ "\n" + dbSaveObj
				);
		HashMap2 dbSaveObj2 = new HashMap2(dbSaveObj);
		/*
		Map toSaveMap = dbSaveObj2.getMap("toSave");
		System.err.println(toSaveMap);
		String toSave_doctype = dbSaveObj2.getString("toSave.doctype");
		 * */
		Integer parentId = dbSaveObj2.getInteger("parent_id");
		Integer nextDbId = nextDbId();
		dbSaveObj2.put("doc_id", nextDbId);
		System.err.println(dbSaveObj2);
		Map docbodyMap = dbSaveObj2.getMap("docbody");
		//insert new record in this history record
		insertDocElementWithDocbody(dbSaveObj2, parentId , docbodyMap);
		Map<String, Object> patientHistoryElement = readDocElement(dbSaveObj2);
		return dbSaveObj2;
	}

	@PostMapping("/r/savePatientHistoryRecord")
	public @ResponseBody Map<String, Object> savePatientHistoryRecord(
			@RequestBody Map<String, Object> dbSaveObj1
			, Principal userPrincipal) {
		HashMap2 dbSaveObj2 = new HashMap2(dbSaveObj1);
		dbSaveObj2.put("hello", "/r/savePatientHistoryRecord");
		logger.info("\n---------------\n"
				+ "/r/savePatientHistoryRecord"
				+ "\n" + sql_docbody_update
				+ "\n" + dbSaveObj2
				);
//		Object docbodyObj = dbSaveObj2.get("docbody");
		Map docbodyObj = dbSaveObj2.getMap("docbody");
//		String docbody = objectToString(docbodyObj);
//		System.err.println(docbody);
		updateDocbody(
			dbSaveObj2
				.put("docbody_id", dbSaveObj2.get("doc_id"))
//				.put("docbody", docbody)
				, docbodyObj
		);
		return dbSaveObj2;
	}

	@PostMapping("/r/removePatientHistoryRecord")
	public @ResponseBody Map<String, Object> removePatientHistoryRecord(
			@RequestBody Map<String, Object> dbSaveObj
			, Principal userPrincipal) {
		dbSaveObj.put("hello", "/r/removePatientHistoryRecord");
		logger.info("---------------\n"
				+ "/r/removePatientHistoryRecord"
				+ "\n" + dbSaveObj);
		//delete doc - first level child by patient
		int numberOfDeletedChildrenRows = db1ParamJdbcTemplate.update(sql_doc_delete_children, dbSaveObj);
		dbSaveObj.put("numberOfDeletedChildrenRows", numberOfDeletedChildrenRows);
		removeDocElement(dbSaveObj);
		return dbSaveObj;
	}
	@Value("${sql.doc.delete.children}") protected				String sql_doc_delete_children;

	@PostMapping("/r/newPatientHistoryRecord")
	public @ResponseBody Map<String, Object> newPatientHistoryRecord(
			@RequestBody Map<String, Object> dbSaveObj
			, Principal userPrincipal) {
		dbSaveObj.put("hello", "/r/newPatientHistoryRecord");
		logger.info("\n---------------\n"
				+ "/r/newPatientHistoryRecord"
				+ "\n" + dbSaveObj);
		//insert doc parent patientId doctype:thing
		//insert doctimestamp
		//insert docbody empty
		Integer nextDbId = nextDbId();
		dbSaveObj.put("doc_id", nextDbId);
		dbSaveObj.put("doctype", 5);//patient.thing
		
		Integer parentId = Integer.parseInt((String) dbSaveObj.get("patientId")) ;
//		insertDocElement(dbSaveObj, parentId);
		insertDocElementWithDocbody(dbSaveObj, parentId);
		
		//return result
		Map<String, Object> newPatientHistoryRecord = readDocElement(dbSaveObj);
		dbSaveObj.put("newPatientHistoryRecord", newPatientHistoryRecord);
		return dbSaveObj;
	}

	@GetMapping(value = "/r/medical/patient2/{patient_id}")
	public @ResponseBody Map<String, Object>  patient2(@PathVariable Integer patient_id) {
		Map<String, Object> map = null;
		return map;
	}
	@GetMapping(value = "/r/central/dbProtocol/{cdbId}")
	public @ResponseBody Map<String, Object> cdbProtocol(@PathVariable Integer cdbId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cdbId", cdbId);
		String url = configMeddocServer + 
			"/r/meddoc/dbProtocol/" + cdbId
			;
		map.put("url", url);
		Map<String, Object> protocol = webClient.getFromUrl(url);
		map.put("protocol", protocol);
		return map;
	}
	@GetMapping(value = "/r/seekProtocolFromMeddoc/{seekProtocol}")
	public @ResponseBody Map<String, Object>  protocolFromMeddocPatients(@PathVariable String seekProtocol) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("seekProtocol", seekProtocol);
		String url = configMeddocServer + "/r/meddoc/dbProtocolListe";
		map.put("url", url);
		Map<String, Object> protocolBourse = webClient.getFromUrl(url);
		map.put("protocolBourse", protocolBourse);
		return map;
	}
	/**
	 * Пошук пацієнтів в БД загальної медичної страховки
	 * @return
	 */
	@GetMapping(value = "/r/seekPatientFromInsurance/{seekPatient}")
	public @ResponseBody Map<String, Object>  medicalFromInsurancePatients(@PathVariable String seekPatient) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("seekPatient", seekPatient);
		logger.info("---------------\n"
				+ "/r/medicalFromInsurance/patients/{seekPatient} " + map);
		Map<String, Object> insuranceSeekPatient = webClient.getFromUrl(configInsuranceServer + "/r/insurance/seekPatient/"
				+ seekPatient);
		logger.info(" ---------------\n " + insuranceSeekPatient);
		map.put("insurancePatients", insuranceSeekPatient.get("insurancePatients"));
		return map;
	}

	private @Autowired WebClient webClient;
	private @Value("${config.insurance.server}") String configInsuranceServer;
	private @Value("${config.meddoc.server}") String configMeddocServer;

}
