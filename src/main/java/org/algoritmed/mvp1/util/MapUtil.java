package org.algoritmed.mvp1.util;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class MapUtil {
	public String getString(Map map, String key1) {
		return (String) map.get(key1);
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
		return (Map) map1.get(key1);
	}
	public Map getMap(Map map1, String key1, String key2) {
		Map map2 = getMap(map1, key1);
		Map map3 = getMap(map2, key2);
		return map3;
	}
}
