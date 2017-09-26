package org.algoritmed.mvp1;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CommonDbRest extends DbAlgoritmed{
	private static final Logger logger = LoggerFactory.getLogger(CommonDbRest.class);
	
	@PostMapping("/r/update_sql_with_param")
	public @ResponseBody Map<String, Object> update_sql_with_param(
			@RequestBody Map<String, Object> data
			,HttpServletRequest request
			,Principal principal
			) {
		String sql = (String) data.get("sql");
		String sql_from_env = env.getProperty(sql);
		data.put(sql, sql_from_env);
		logger.info("\n-------------\n"
				+ "/r/update_sql_with_param"
				+ "\n" + data
				);
		int update = db1ParamJdbcTemplate.update(sql_from_env, data);
		data.put("update", update);
		return data;
	}

	@GetMapping("/r/read_sql_with_param")
	public @ResponseBody Map<String, Object> read_sql_with_param(
			@RequestParam(value = "sql", required = true) String sql
			,HttpServletRequest request
			) {
		Map<String, Object> map = sqlParamToMap(sql, request);
		List<Map<String, Object>> list = db1ParamJdbcTemplate.queryForList(env.getProperty(sql), map);
		map.put("list", list);
		return map;
	}

	private Map<String, Object> sqlParamToMap(String sql, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		String sql_from_env = env.getProperty(sql);
		map.put(sql, sql_from_env);
		Map<String, String[]> parameterMap = request.getParameterMap();
		map.put("parameterMap", parameterMap);
		for (String key : parameterMap.keySet()) {
			String[] v = parameterMap.get(key);
			map.put(key, v[0]);
		}
		return map;
	}

}
