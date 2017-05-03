package org.algoritmed.mvp1.medic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.algoritmed.mvp1.util.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * REST доступ для роботи медиків з пацієнтами.
 * @author roman
 *
 */
@Controller
public class MedicalPatientRest {
	private static final Logger logger = LoggerFactory.getLogger(MedicalPatientRest.class);
	/**
	 * JDBC доступ до БД через простий SQL
	 */
	@Autowired
	protected JdbcTemplate db1JdbcTemplate;
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
		logger.info("---------------\n"
				+ "/r/medical/patient/{patient_id}\n" + map);
		Map<String, Object> patientById = db1ParamJdbcTemplate.queryForMap(sqlMedicalSelectPatientById, map);
		map.put("patientById", patientById);
		return map;
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
