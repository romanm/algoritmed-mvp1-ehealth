package org.algoritmed.mvp1;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.UUID;

import org.algoritmed.mvp1.util.ReadJsonFromFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CommonRest {
	private static final Logger logger = LoggerFactory.getLogger(CommonRest.class);
	@Autowired	ReadJsonFromFile readJsonFromFile;

	@GetMapping("/v/{page1}")
	public String viewPage1(@PathVariable String page1, Model model) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("page1", page1);
		logger.info(" --------- \n"
				+ "/v/{page1}"
				+ "\n" + map
				+ "\n" + model
				);
		setModelAtribute(model, page1, "ng_template");
		String th_template = (String) 
				setModelAtribute(model, page1, "th_template");
		logger.info(" --------- \n"
				+ "/v/{page1}"
				+ "\n" + map
				+ "\n" + model
				);
		return th_template;
	}

	private Object setModelAtribute(Model model, String page1, String attribute) {
		Object att = getModelAttribute(page1, attribute);
		model.addAttribute(attribute, att);
		return att;
	}

	private Object getModelAttribute(String page1, String attribute) {
		Map<String, Object> configWebSite = readJsonFromFile.readConfigWebSite();
		Map<String, Object> pageConfig = (Map<String, Object>) configWebSite.get(page1);
		Object ngController = null;
		if(pageConfig != null){
			if(pageConfig.containsKey(attribute))
				ngController = pageConfig.get(attribute);
			else
				ngController = configWebSite.get(attribute);
		}
		return ngController;
	}


	private @Value("${sql.msp.list}")				String sql_msp_list;
	@GetMapping(value = "/r/msp_list")
	public @ResponseBody Map<String, Object>  msp_list() {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> msp_list = db1JdbcTemplate.queryForList(sql_msp_list);
		map.put("msp_list", msp_list);
		return map;
	}

	
	@GetMapping("/r/principal")
	public @ResponseBody Map<String, Object> principal(Principal principal) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("principal", principal);
		logger.info(" --------- \n"
				+ "/v/principal \n" + map);
		if(null!=principal) {
			String name = principal.getName();
			System.err.println("name = "+name);
			map.put("username", name);
			Map<String, Object> queryForMap = db1ParamJdbcTemplate.queryForMap(sqlDb1UsersFromUsername, map);
			map.put("user", queryForMap);
			Integer user_id = (Integer) queryForMap.get("user_id");
			map.put("user_id", user_id);
			List l = db1ParamJdbcTemplate.queryForList(sql_user_msp, map);
			map.put("user_msp", l);
			if("admin".equals(name)) {
				Map<String, Object> msp_list = msp_list();
				map.put("user_msp", msp_list.get("msp_list"));
			}
		}
		return map;
	}

	private @Value("${sql.db1.users.fromUsername}") String sqlDb1UsersFromUsername;
	private @Value("${sql.db1.user.msp}") String sql_user_msp;

	@Autowired JdbcTemplate db1JdbcTemplate;
	@Autowired NamedParameterJdbcTemplate db1ParamJdbcTemplate;
	
	@GetMapping("/r/testUUID")
	public @ResponseBody Map<String, Object> testUUI(Principal principal) {
		Map<String, Object> map = new HashMap<String, Object>();
		UUID uuid = addUuid(map);
		map.put("version", uuid.version());
		map.put("variant", uuid.variant());
		map.put("length", uuid.toString().length());
		map.put("principal", principal);
		logger.info(" --------- \n"
				+ "/v/testUUI \n" + map);
		return map;
	}

	/**
	 * Генерує випадковий universally unique identifier (UUID), 
	 * додає його до map
	 * @param map об'єкт для довавання новоствореного UUID
	 * @return новостворений UUID
	 */
	protected UUID addUuid(Map<String, Object> map) {
		UUID uuid = UUID.randomUUID();
		map.put("uuid", uuid);
		return uuid;
	}

}
