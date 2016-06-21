package ch.arrg.javabot.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HtmlReaderHelper {
	
	public static BufferedReader openUrlForRead(String urlS) {
		HttpURLConnection conn;
		try {
			URL url = new URL(urlS);
			conn = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		try (BufferedReader in = new BufferedReader(new InputStreamReader(
				conn.getInputStream()))) {
			return in;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
