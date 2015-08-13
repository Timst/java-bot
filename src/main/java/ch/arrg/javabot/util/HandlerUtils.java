package ch.arrg.javabot.util;


public class HandlerUtils {
	public static String withKeyword(String keyword, String message) {
		String tag = "+" + keyword;
		if (message.startsWith(tag)) {
			String notag = message.replace(tag, "").trim();
			return notag;
		}

		return null;
	}

	public static String getWord(String message, int index) {
		String[] words = message.split("\\s+");
		if (words.length > index) {
			return words[index];
		} else {
			return "";
		}
	}

	public static int random(int i, int j) {
		return (int) (i + j * Math.random());
	}
}
