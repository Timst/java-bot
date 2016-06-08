package ch.arrg.javabot.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/** Keeps data about users. Usernames are canonized so that the bot considers
 * that for instance "james" and "james_away" are the same person.
 * 
 * @author tgi */
public class UserDb implements Serializable {
	
	private Map<String, UserData> users = new HashMap<>();
	
	public UserData getOrCreateUserData(String user) {
		String canon = canonize(user);
		if(!users.containsKey(canon)) {
			users.put(canon, new UserData());
		}
		
		return users.get(canon);
	}
	
	public static String canonize(String user) {
		if(user == null) {
			return "";
		}
		
		user = user.toLowerCase();
		user = user.replaceAll("[-_](.*?)$", ""); // arrg_manger -> arrg
		return user.replaceAll("[^a-zA-Z]", ""); // djidiouf1 -> djidiouf
	}
	
	public int countKeys(String sender) {
		UserData data = getOrCreateUserData(sender);
		return data.countKeys();
	}
}
