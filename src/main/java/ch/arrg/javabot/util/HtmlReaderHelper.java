package ch.arrg.javabot.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlReaderHelper {
	
	private final static String TITLE_REGEX = "<title>(.*?)</title>";
	private final static Pattern TITLE_PAT = Pattern.compile(TITLE_REGEX);
	
	public static BufferedReader openUrlForRead(String urlS) {
		HttpURLConnection conn;
		try {
			URL url = new URL(urlS);
			conn = (HttpURLConnection) url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			return in;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static String readTitle(String urlS) {
		
		try (BufferedReader in = HtmlReaderHelper.openUrlForRead(urlS)) {
			String inputLine;
			
			while((inputLine = in.readLine()) != null) {
				Matcher m = TITLE_PAT.matcher(inputLine);
				if(m.find()) {
					return m.group(1);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
