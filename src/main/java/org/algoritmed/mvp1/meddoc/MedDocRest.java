package org.algoritmed.mvp1.meddoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MedDocRest {
	private static final Logger logger = LoggerFactory.getLogger(MedDocRest.class);
	@Autowired
	protected NamedParameterJdbcTemplate db1ParamJdbcTemplate;

	private @Value("${sql.meddoc.openIcPc2SubGroup.en}") String sqlMeddocOpenIcPc2SubGroupEn;
	private @Value("${sql.meddoc.openIcPc2SubGroup}") String sqlMeddocOpenIcPc2SubGroup;
	@GetMapping(value = "/r/meddoc/openIcPc2SubGroup/{code}")

	public @ResponseBody Map<String, Object> openIcPc2SubGroup(@PathVariable String code) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", code);
		logger.info("---------------\n"
				+ "/r/meddoc/openIcPc2SubGroup/{code}"
				+ "\n" + map);
		Map<String, Object> openIcPc2SubGroup = db1ParamJdbcTemplate.queryForMap(sqlMeddocOpenIcPc2SubGroup, map);
		map.put("openIcPc2SubGroup", openIcPc2SubGroup);
		Map<String, Object> openIcPc2SubGroupEn = db1ParamJdbcTemplate.queryForMap(sqlMeddocOpenIcPc2SubGroupEn, map);
		map.put("openIcPc2SubGroupEn", openIcPc2SubGroupEn);
		return map;
	}

	String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	int ID_LENGTH = 4;

	@GetMapping(value = "/r/meddoc/amGenerateID")
	public @ResponseBody List<String> amGenerateID() {
		ArrayList<String> list = new ArrayList<>();
		for (int j = 0; j < 10; j++) {
			String amGenerateID = "";
			for (int i = 0; i < ID_LENGTH; i++) {
				int round = (int) Math.round(Math.floor(Math.random() * ALPHABET.length()));
				amGenerateID += ALPHABET.charAt(round);
			}
			if(!list.contains(amGenerateID))
				list.add(amGenerateID);
		}
		return list;
	}
}
