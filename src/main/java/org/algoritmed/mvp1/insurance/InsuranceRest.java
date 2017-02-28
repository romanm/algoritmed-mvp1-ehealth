package org.algoritmed.mvp1.insurance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * REST медична страховака
 * @author roman
 *
 */
@Controller
public class InsuranceRest {
	private static final Logger logger = LoggerFactory.getLogger(InsuranceRest.class);
	/**
	 * JDBC доступ до БД медичної страховки через простий SQL
	 */
	@Autowired protected NamedParameterJdbcTemplate db1ParamJdbcTemplate;
	
	/**
	 * JDBC доступ до БД медичної страховки через простий SQL
	 */
	@Autowired protected JdbcTemplate db1JdbcTemplate;
	
	/**
	 * SQL select для зчитування всіх пацієнтів страховки
	 */
	private @Value("${sql.insurance.selectPatients}") String sqlSelectPatients;
	
	/**
	 * Зчитування всіх пацієнтів медичної страховки
	 * @return Map об'єкт що містить всіх пацієнтів медіка
	 */
	@GetMapping(value = "/r/insurance/patients")
	public @ResponseBody Map<String, Object>  patients() {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> insurancePatients = db1JdbcTemplate.queryForList(sqlSelectPatients);
		map.put("insurancePatients", insurancePatients);
		return map;
	}
	
	/**
	 * SQL select для пошуку пацієнтів страховки
	 */
	private @Value("${sql.insurance.seekPatient}") String sqlSeekPatients;
	/**
	 * Пошук пацієнтів в БД загальної медичної страховки
	 * @return
	 */
	@GetMapping(value = "/r/insurance/seekPatient/{seekPatient}")
	public @ResponseBody Map<String, Object> insuranceSeekPatient(@PathVariable String seekPatient) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("seekPatient", "%" + seekPatient +"%");
		logger.info("---------------\n"
				+ "/r/insurance/seekPatient/{seekPatient}" + map);
		List<Map<String, Object>> insurancePatients = db1ParamJdbcTemplate.queryForList(sqlSeekPatients, map);
		map.put("insurancePatients", insurancePatients);
		return map;
	}
}
