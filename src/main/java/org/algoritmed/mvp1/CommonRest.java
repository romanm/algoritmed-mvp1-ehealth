package org.algoritmed.mvp1;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.algoritmed.mvp1.util.ReadJsonFromFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CommonRest  extends DbAlgoritmed{
	private static final Logger logger = LoggerFactory.getLogger(CommonRest.class);
	@Autowired	ReadJsonFromFile readJsonFromFile;

	@GetMapping("/v/{page1}")
	public String viewPage1(@PathVariable String page1, Model model) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("page1", page1);
		setModelAtribute(model, page1, "ng_template");
		String th_template = (String) 
				setModelAtribute(model, page1, "th_template");
		logger.info(" ----30---viewPage1-- /v/{page1} "+page1);
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

	@GetMapping(value = "/r/msp_list")
	public @ResponseBody Map<String, Object>  msp_list() {
		Map<String, Object> map = super.msp_list();
		return map;
	}

	@GetMapping("/r/principal")
	public @ResponseBody Map<String, Object> principal(Principal principal) {
		Map<String, Object> map = 
				super.principal(principal);
		return map;
	}

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
