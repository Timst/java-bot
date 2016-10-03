package ch.arrg.javabot.handlers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.Const;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.log.DatabaseLogServiceProvider;
import ch.arrg.javabot.log.LogLine;
import ch.arrg.javabot.util.CommandMatcher;
import ch.arrg.javabot.util.HandlerUtils;

public class QuoteLogHandler implements CommandHandler {
	
	/** Log URL pattern where the first match group is the log line id. */
	Pattern LOG_REGEX = Pattern.compile(Const.str("QuoteLogHandler.logUrlPattern"));
	
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
		LogLine line = DatabaseLogServiceProvider.get().getById(ctx.channel, lineId);
		if(line == null) {
			ctx.reply("Quote not found");
		} else {
			ctx.reply(line.user + " on " + HandlerUtils.prettyDate(line.date) + ": " + line.message);
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
