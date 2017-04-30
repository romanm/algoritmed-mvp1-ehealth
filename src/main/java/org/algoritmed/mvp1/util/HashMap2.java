package org.algoritmed.mvp1.util;

import java.util.HashMap;
import java.util.Map;

public class HashMap2 extends HashMap<String, Object> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HashMap2(Map<String, Object> dbSaveObj) {
		this.putAll(dbSaveObj);
	}

	public boolean notContainsOrStringIsEmpty(String key) {
		if(!this.containsKey(key))
			return false;
		String str = this.getString(key);
		return null == str || str.equals("");
	}
	
	public String getString(String key) {
		return (String) this.get(key);
	}

	String pointSplit = "\\.";
	public boolean contains(String keysString) {
		Map<String, Object> map = this;
		String[] split = keysString.split(pointSplit);
		for (int i = 0; i < split.length; i++) {
			if(!map.containsKey(split[i]))
				return false;
			if(i < split.length - 1)
				map = (Map<String, Object>) map.get(split[i]);
		}
		return map.containsKey(split[split.length - 1]);
	}

	private Object getObject(String keysString) {
		Map<String, Object> map = this;
		String[] split = keysString.split(pointSplit);
		for (int i = 0; i < split.length; i++) {
			if(!map.containsKey(split[i]))
				return null;
			if(i < split.length - 1)
				map = (Map<String, Object>) map.get(split[i]);
		}
		Object object = map.get(split[split.length - 1]);
		return object;
	}

	public Integer getInteger(String keysString) {
		return (Integer) getObject(keysString);
	}
}
