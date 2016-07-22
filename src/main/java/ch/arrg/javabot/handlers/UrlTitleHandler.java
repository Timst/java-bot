package ch.arrg.javabot.handlers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.util.HtmlReaderHelper;

public class UrlTitleHandler implements CommandHandler {
	
	private final static String URL_REGEX = "(https?://\\S+)\\s?";
	private final static Pattern URL_PAT = Pattern.compile(URL_REGEX);
	
	@Override
	public void handle(BotContext ctx) {
		String m = ctx.message;
		
		String url = findUrl(m);
		if(url != null) {
			String title = HtmlReaderHelper.readTitle(url);
			if(title != null) {
				ctx.reply("Link title: \"" + title + "\"");
			}
		}
	}
	
	private static String findUrl(String m) {
		if(m.contains("http") && m.contains("://") && m.contains("+t")) {
			Matcher matcher = URL_PAT.matcher(m);
			if(matcher.find()) {
				return matcher.group(1);
			}
		}
		
		return null;
	}
	
	@Override
	public String getName() {
		return "linktitle";
	}
	
	@Override
	public void help(BotContext ctx) {
		ctx.reply("Automatically reads the title of links with +t.");
	}
	
}
