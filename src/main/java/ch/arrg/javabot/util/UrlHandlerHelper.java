package ch.arrg.javabot.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlHandlerHelper {
	
	public static UrlMatcher makeMatcher(String... patterns) {
		return new UrlMatcher(patterns);
	}
	
	public static class UrlMatcher {
		
		private final Pattern[] pats;
		
		public UrlMatcher(String[] patterns) {
			pats = new Pattern[patterns.length];
			for(int i = 0; i < patterns.length; i++) {
				pats[i] = Pattern.compile(patterns[i]);
			}
		}
		
		public Matcher tryMatch(String str) {
			for(Pattern p : pats) {
				Matcher matcher = p.matcher(str);
				if(matcher.find()) {
					return matcher;
				}
			}
			
			return null;
		}
		
	}
}
