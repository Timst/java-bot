package ch.arrg.javabot.util;

import java.util.Date;
import java.util.List;

public class HandlerUtils {
	public static int random(int i, int j) {
		return (int) (i + j * Math.random());
	}
	
	public static <T> T random(List<T> items) {
		int idx = random(0, items.size());
		return items.get(idx);
	}
	
	public static String prettyDate(Date lastSeen) {
		return lastSeen.toString(); // TODO generated HandlerUtils.prettyDate
	}
}
