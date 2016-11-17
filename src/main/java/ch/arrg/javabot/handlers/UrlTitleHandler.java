package ch.arrg.javabot.handlers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.util.HtmlReaderHelper;

public class UrlTitleHandler implements CommandHandler {

	private final static Pattern URL_PAT = Pattern.compile("(https?://\\S+)\\s?");
	private final static Pattern SHORT_CMD = Pattern.compile("\\+t\\b");

	private String lastUrl = null;

	private boolean alwaysReadTitles = true;
	
	@Override
	public void handle(BotContext ctx) {
		String m = ctx.message;

		boolean printUrlNow = false;
		String url = findUrlInMessage(m);
		if(url != null) {
			lastUrl = url;
			
			if(alwaysReadTitles) {
				printUrlNow = true;
			}
			
			Matcher matcher = SHORT_CMD.matcher(m);
			if(matcher.find()) {
				printUrlNow = true;
			}
		}
		
		if(lastUrl != null && (printUrlNow || m.startsWith("+linktitle"))) {
			handleUrl(ctx, lastUrl);
		}
	}

	private static void handleUrl(BotContext ctx, String url) {
		if(url != null) {
			String title = HtmlReaderHelper.readTitle(url);
			if(title != null) {
				ctx.reply("Link: \"" + title + "\"");
			}
		}
	}

	private static String findUrlInMessage(String m) {
		if(m.contains("http") && m.contains("://")) {
			Matcher matcher = URL_PAT.matcher(m);
			if(matcher.find()) {
				return matcher.group(1);
			}
		}

		return null;
	}

	@Override
	public String getName() {
		return "+linktitle";
	}

	@Override
	public void help(BotContext ctx) {
		ctx.reply("Automatically reads the title of links with +t or +linktitle to read the latest link.");
	}

}
