package ch.arrg.javabot.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Key-value map for a single user.
 * 
 * @author tgi
 */
public class UserData implements Serializable {
	private Map<String, String> data = new HashMap<>();

	public String getRecord(String key) {
		return data.get(key);
	}

	public String getOrInit(String key, String defaultValue) {
		if (getRecord(key) == null) {
			setRecord(key, defaultValue);
		}

		return getRecord(key);
	}

	public void setRecord(String key, String value) {
		data.put(key, value);
	}

	public int countKeys() {
		return data.size();
	}
}
