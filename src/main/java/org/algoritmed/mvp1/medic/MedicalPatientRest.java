package org.algoritmed.mvp1.medic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * REST доступ для роботи медиків з пацієнтами.
 * @author roman
 *
 */
@Controller
public class MedicalPatientRest {
	/**
	 * JDBC доступ до БД через простий SQL
	 */
	@Autowired
	protected JdbcTemplate dataSourceDb1;
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
		List<Map<String, Object>> medicPatients = dataSourceDb1.queryForList(sqlMedicalSelectPatients);
		map.put("medicPatients", medicPatients);
		return map;
	}
}
