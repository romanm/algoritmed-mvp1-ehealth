package org.algoritmed.mvp1.medic;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.algoritmed.mvp1.DbAlgoritmed;
import org.algoritmed.mvp1.util.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
	
	@Autowired
	protected NamedParameterJdbcTemplate db1ParamJdbcTemplate;
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
	
	private @Value("${sql.medical.selectPatientById}") String sqlMedicalSelectPatientById;
	@GetMapping(value = "/r/medical/patient/{patient_id}")
	public @ResponseBody Map<String, Object>  patient(@PathVariable Integer patient_id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("patient_id", patient_id);
		map.put("parent_id", patient_id);
		logger.info("---------------\n"
				+ "/r/medical/patient/{patient_id}"
				+ "\n" + map);
		Map<String, Object> patientById = db1ParamJdbcTemplate.queryForMap(sqlMedicalSelectPatientById, map);
		List<Map<String, Object>> children = db1ParamJdbcTemplate.queryForList(sqlDocChildren, map);
		patientById.put("children", children);
		map.put("patientById", patientById);
		return map;
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
		int numberOfDeletedRows = db1ParamJdbcTemplate.update(sqlDocDelete, dbSaveObj);
		dbSaveObj.put("numberOfDeletedRows", numberOfDeletedRows);
		return dbSaveObj;
	}
	private @Value("${sql.doc.delete}")				String sqlDocDelete;
	private @Value("${sql.doc.insert}")				String sqlDocInsert;
	private @Value("${sql.docbody.insertEmpty}")	String sqlDocbodyInsertEmpty;
	private @Value("${sql.doctimestamp.insert}")	String sqlDoctimestampInsert;
	private @Value("${sql.doc.children}")			String sqlDocChildren;
	private @Value("${sql.doc.byId}")				String sqlDocById;
	@PostMapping("/r/newPatientSateRecord")
	public @ResponseBody Map<String, Object> newPatientSateRecord(
			@RequestBody Map<String, Object> dbSaveObj
			, Principal userPrincipal) {
		dbSaveObj.put("hello", "/r/newPatientSateRecord");
		logger.info("---------------\n"
				+ "/r/newPatientSateRecord"
				+ "\n" + dbSaveObj);
		//insert doc parent patientId doctype:thing
		//insert doctimestamp
		//insert docbody empty
		Integer nextDbId = nextDbId();
		dbSaveObj.put("doc_id", nextDbId);
		dbSaveObj.put("doctype", 5);//patient.thing
		dbSaveObj.put("parent_id", dbSaveObj.get("patientId"));
		Timestamp doctimestamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
		dbSaveObj.put("doctimestamp", doctimestamp);
		int update = db1ParamJdbcTemplate.update(sqlDocInsert, dbSaveObj);
		int update2 = db1ParamJdbcTemplate.update(sqlDoctimestampInsert, dbSaveObj);
		int update3 = db1ParamJdbcTemplate.update(sqlDocbodyInsertEmpty, dbSaveObj);
		//return result
		Map<String, Object> newPatientSateRecord = db1ParamJdbcTemplate.queryForMap(sqlDocById, dbSaveObj);
		dbSaveObj.put("newPatientSateRecord", newPatientSateRecord);
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
		/*
		 * */
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
