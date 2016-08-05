package ch.arrg.javabot.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.util.HtmlReaderHelper;
import ch.arrg.javabot.util.Logging;
import ch.arrg.javabot.util.UrlHandlerHelper;
import ch.arrg.javabot.util.UrlHandlerHelper.UrlMatcher;

public class SteamUrlHandler implements CommandHandler {
	
	private final static UrlMatcher URL_MATCHER = UrlHandlerHelper
			.makeMatcher("(https?://store\\.steampowered\\.com/app/(\\d+))");
	
	private final static String TITLE_REGEX = "<div class=\"apphub_AppName\">(.*?)</div>";
	private final static Pattern TITLE_PAT = Pattern.compile(TITLE_REGEX);
	
	@Override
	public void handle(BotContext ctx) {
		String m = ctx.message;
		String ytUrl = getUrl(m);
		if(ytUrl != null) {
			String title = readTitle(ytUrl);
			if(title != null) {
				ctx.reply("Steam page: " + title);
			}
		}
	}
	
	private static String getUrl(String str) {
		Matcher m = URL_MATCHER.tryMatch(str);
		if(m != null) {
			return m.group(1);
		}
		
		return null;
	}
	
	private static String readTitle(String urlS) {
		
		try (BufferedReader in = HtmlReaderHelper.openUrlForRead(urlS)) {
			String inputLine;
			
			while((inputLine = in.readLine()) != null) {
				Matcher m = TITLE_PAT.matcher(inputLine);
				if(m.find()) {
					return m.group(1);
				}
			}
		} catch (IOException e) {
			Logging.logException(e);
		}
		
		return null;
	}
	
	@Override
	public String getName() {
		return "steam";
	}
	
	@Override
	public void help(BotContext ctx) {
		ctx.reply("Automatically reads the name of games on steam.");
	}
	
}
