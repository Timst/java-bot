package ch.arrg.javabot.util;

public class ApiHelper {
	public static String addParam(String req, String key, String value) {
		return req + "&" + key + "=" + value;
	}
}
