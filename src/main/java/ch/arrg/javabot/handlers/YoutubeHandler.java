package ch.arrg.javabot.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.data.BotContext;

public class YoutubeHandler implements CommandHandler {
	
	private final static String[] URL_REGEXES = {
			"(https?://(.*?)youtube\\.com/watch\\?v=([a-zA-Z0-9_-]+))",
			"(https?://(.*?)youtu\\.be/([a-zA-Z0-9_-]+))" };
	
	private final static Pattern[] URL_PAT;
	
	private final static String TITLE_REGEX = "<title>(.*?) - YouTube</title>";
	private final static Pattern TITLE_PAT = Pattern.compile(TITLE_REGEX);
	
	static {
		URL_PAT = new Pattern[URL_REGEXES.length];
		for(int i = 0; i < URL_REGEXES.length; i++) {
			URL_PAT[i] = Pattern.compile(URL_REGEXES[i]);
		}
	}
	
	@Override
	public void handle(BotContext ctx) {
		String m = ctx.message;
		String ytUrl = getYoutubeUrl(m);
		if(ytUrl != null) {
			String title = readTitle(ytUrl);
			if(title != null) {
				ctx.reply("YouTube title: " + title);
			}
		}
	}
	
	private static String getYoutubeUrl(String m) {
		for(Pattern p : URL_PAT) {
			Matcher matcher = p.matcher(m);
			if(matcher.find()) {
				return matcher.group(1);
			}
		}
		
		return null;
	}
	
	private static String readTitle(String urlS) {
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
	
	@Override
	public String getName() {
		return "youtube";
	}
	
	@Override
	public void help(BotContext ctx) {
		ctx.reply("Automatically reads the title of youtube videos.");
	}
	
}
