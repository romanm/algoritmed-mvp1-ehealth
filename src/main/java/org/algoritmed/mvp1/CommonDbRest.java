package org.algoritmed.mvp1;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "${config.security_prefix}")
public class CommonDbRest extends DbAlgoritmed{
	private static final Logger logger = LoggerFactory.getLogger(CommonDbRest.class);

	@PostMapping("/update_sql_with_param")
	public @ResponseBody Map<String, Object> update_sql_with_param(
			@RequestBody Map<String, Object> data
			,HttpServletRequest request
			,Principal principal
		) {
		logger.info("\n-------29-----\n"
				+ "/r/update_sql_with_param"
				);
		update_sql_script(data);
		return data;
	}

	@GetMapping("/read_sql_with_param")
	public @ResponseBody Map<String, Object> read_sql_with_param(
			@RequestParam(value = "sql", required = true) String sql
			,HttpServletRequest request
			) {
		Map<String, Object> map = sqlParamToMap(sql, request);
		logger.info("\n------46-------\n"
				+ "/r/read_sql_with_param"
				+ "\n" + map
				);
		read_select(map, env.getProperty(sql), null);
//		List<Map<String, Object>> list = db1ParamJdbcTemplate.queryForList(env.getProperty(sql_command), map);
//		map.put("list", list);
		return map;
	}

	private Map<String, Object> sqlParamToMap(String sql, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		String sql_from_env = env.getProperty(sql);
		map.put(sql, sql_from_env);
		Map<String, String[]> parameterMap = request.getParameterMap();
		System.err.println("---93--------");
		System.err.println(parameterMap.keySet());
		map.put("parameterMap", parameterMap);
		for (String key : parameterMap.keySet()) {
			String[] v = parameterMap.get(key);
			String val = v[0];
			System.err.print(" & "+key+"/"+val);
			map.put(key, val);
		}
		System.err.println();
		return map;
	}

}
