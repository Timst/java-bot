package ch.arrg.javabot.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/** Key-value map for a single user.
 * 
 * @author tgi */
public class UserData implements Serializable {
	private Map<String, Serializable> data = new HashMap<>();
	
	public String getRecordStr(String key) {
		return (String) getRecord(key);
	}
	
	private Serializable getRecord(String key) {
		return data.get(key);
	}
	
	public String getOrInit(String key, Serializable defaultValue) {
		if(getRecordStr(key) == null) {
			setRecord(key, defaultValue);
		}
		
		return getRecordStr(key);
	}
	
	public void setRecord(String key, Serializable value) {
		data.put(key, value);
	}
	
	public int countKeys() {
		return data.size();
	}
}
