package ch.arrg.javabot.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/** Key-value map for a single user.
 * 
 * @author tgi */
public class UserData implements Serializable {
	private Map<String, Object> data = new HashMap<>();
	
	public String getRecordStr(String key) {
		return (String) getRecord(key);
	}
	
	private Object getRecord(String key) {
		return data.get(key);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Serializable> T getOrInit(String key, T defaultValue) {
		if(getRecord(key) == null) {
			setRecord(key, defaultValue);
		}
		
		return (T) getRecord(key);
	}
	
	public void setRecord(String key, Object value) {
		data.put(key, value);
	}
	
	public int countKeys() {
		return data.size();
	}
}
