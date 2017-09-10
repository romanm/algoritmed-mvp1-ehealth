package org.algoritmed.mvp1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@PropertySource("classpath:sql.properties")
public class CommonDbRest {
	private static final Logger logger = LoggerFactory.getLogger(CommonDbRest.class);
	
	@GetMapping("/r/read_sql_with_param")
	public @ResponseBody Map<String, Object> read_sql_with_param(
			@RequestParam(value = "sql", required = true) String sql
			,HttpServletRequest request
			) {
		//user for sql.roles.select
		Map<String, Object> map = new HashMap<String, Object>();
		String sql_select = env.getProperty(sql);
		map.put(sql, sql_select);
		Map<String, String[]> parameterMap = request.getParameterMap();
		map.put("parameterMap", parameterMap);
		for (String key : parameterMap.keySet()) {
			String[] v = parameterMap.get(key);
			map.put(key, v[0]);
		}
		List<Map<String, Object>> list = db1JdbcTemplate.queryForList(sql_select);
		map.put("list", list);
		return map;
		
	}

	@GetMapping("/r/read_sql/{sql_env_name}")
	public @ResponseBody Map<String, Object> read_sql(
			@PathVariable String sql_env_name
			) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sql_env_name", sql_env_name);
		String sql_select = env.getProperty("sql.roles.select");
		System.err.println(sql_select);
		System.err.println(env.getRequiredProperty("sql.roles.select"));
		System.err.println(env.getProperty("sql.db1.users.checkUsername"));
		logger.info(" --------- \n"
				+ "/r/read_sql/{sql_env_name}"
				+ "\n" + map
				);
		List<Map<String, Object>> list = db1JdbcTemplate.queryForList(sql_select);
		map.put("list", list);
		return map;
	}

	@Autowired private Environment env;
	@Autowired JdbcTemplate db1JdbcTemplate;
	@Autowired NamedParameterJdbcTemplate db1ParamJdbcTemplate;

}
