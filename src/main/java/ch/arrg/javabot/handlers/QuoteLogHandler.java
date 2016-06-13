package ch.arrg.javabot.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.log.LogLine;
import ch.arrg.javabot.util.CommandMatcher;
import ch.arrg.javabot.util.LogLines;

public class QuoteLogHandler implements CommandHandler {
	
	// TODO do not store a private map
	private Map<Integer, LogLine> lines = null;
	
	Pattern LOG_REGEX = Pattern
			.compile("http://braisn.sarcasme.org/braisnchat-log/#id-(\\d+)");
	
	@Override
	public void handle(BotContext ctx) {
		
		String message = ctx.message;
		
		CommandMatcher matcher = CommandMatcher.make("+log");
		if(matcher.matches(message)) {
			try {
				Integer lineId = Integer.parseInt(matcher.nextWord());
				quote(ctx, lineId);
			} catch (NumberFormatException e) {
				ctx.reply("You must provide a line number.");
			}
		}
		
		autoQuote(ctx, message);
	}
	
	private void autoQuote(BotContext ctx, String message) {
		Matcher m = LOG_REGEX.matcher(message);
		if(m.find()) {
			Integer lineId = Integer.parseInt(m.group(1));
			quote(ctx, lineId);
		}
	}
	
	private void quote(BotContext ctx, Integer lineId) {
		if(lines == null) {
			initMap();
		}
		
		LogLine line = lines.get(lineId);
		if(line == null) {
			ctx.reply("Quote not found");
		} else {
			// TODO Date formatting
			
			ctx.reply(line.user + " on " + line.date + ": " + line.message);
		}
	}
	
	private void initMap() {
		lines = new HashMap<>();
		for(LogLine line : LogLines.getLogLines()) {
			lines.put(line.id, line);
		}
	}
	
	@Override
	public String getName() {
		return "+log";
	}
	
	@Override
	public void help(BotContext ctx) {
		ctx.reply("Quote a line from the log");
		ctx.reply("+log <lineId>");
	}
	
}
