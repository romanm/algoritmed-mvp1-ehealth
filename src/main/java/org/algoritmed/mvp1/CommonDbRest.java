package org.algoritmed.mvp1;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
				+ "\n" + sql_from_env
				);
		if(sql_from_env.contains(";")) {
			if(data.containsKey("docbodyMap")) {
				Map<String,Object> docbodyMap = (Map<String, Object>) data.get("docbodyMap");
				String docbody = objectToString(docbodyMap);
				data.put("docbody", docbody);
			}
			if(data.containsKey("lotOfNewIds")) {
				int lotOfNewIds = (int) data.get("lotOfNewIds");
				for (int i = 1; i <= lotOfNewIds; i++) {
					Integer nextDbId = nextDbId();
					data.put("nextDbId"+i, nextDbId);
				}
				System.err.println(data);
			}
			String[] sqls_from_env = sql_from_env.split(";");
			for (int i = 0; i < sqls_from_env.length; i++) {
				String sql_command = sqls_from_env[i].trim();
				System.err.print(sql_command);
				if(sql_command.length()==0)
					continue;
				System.err.print("-"+i+"->");
				String first_word = sql_command.split(" ")[0];
				System.err.println(first_word);
				if("INSERT".equals(first_word)
				|| "UPDATE".equals(first_word)
				|| "DELETE".equals(first_word)
				){
					int update = db1ParamJdbcTemplate.update(sql_command, data);
					data.put("update"+i, update);
				}else if("SELECT".equals(first_word)) {
					List<Map<String, Object>> list = db1ParamJdbcTemplate.queryForList(sql_command, data);
					data.put("list"+i, list);
				}
			}
		}else {
			int update = db1ParamJdbcTemplate.update(sql_from_env, data);
			data.put("update", update);
		}
		return data;
	}

	@GetMapping("/r/read_sql_with_param")
	public @ResponseBody Map<String, Object> read_sql_with_param(
			@RequestParam(value = "sql", required = true) String sql
			,HttpServletRequest request
			) {
		Map<String, Object> map = sqlParamToMap(sql, request);
		logger.info("\n------46-------\n"
				+ "/r/read_sql_with_param"
				+ "\n" + map
				);
		List<Map<String, Object>> list = db1ParamJdbcTemplate.queryForList(env.getProperty(sql), map);
		map.put("list", list);
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
