package org.algoritmed.mvp1.util;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class MapUtil {
	public String getString(Map map, String key1) {
		return ""+map.get(key1);
	}
	public String getString(Map map1, String key1, String key2) {
		Map map2 = getMap(map1, key1);
		String string = getString(map2, key2);
		return string;
	}
	public String getString(Map map1, String key1, String key2, String key3) {
		Map map3 = getMap(map1, key1, key2);
		String string = getString(map3, key3);
		return string;
	}
	public Map getMap(Map map1, String key1) {
		Map map;
		String[] splitKey = key1.split("\\[");
		if(splitKey.length>1) {
			String key = splitKey[1].split("]")[0];
			int k = Integer.parseInt(key);
			List l = (List) map1.get(splitKey[0]);
			map = (Map) l.get(k);
		}else {
			map = (Map) map1.get(key1);
		}
		return map;
	}
	public Map getMap(Map map1, String key1, String key2) {
		Map map2 = getMap(map1, key1);
		Map map3 = getMap(map2, key2);
		return map3;
	}
}
