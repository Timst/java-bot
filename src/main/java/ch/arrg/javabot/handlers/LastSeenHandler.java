package ch.arrg.javabot.handlers;

import java.util.Date;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.log.DatabaseLogService;
import ch.arrg.javabot.log.LogLine;
import ch.arrg.javabot.util.CommandMatcher;
import ch.arrg.javabot.util.HandlerUtils;

public class LastSeenHandler implements CommandHandler {
	
	@Override
	public void handle(BotContext ctx) {
		
		CommandMatcher matcher = CommandMatcher.make("+lastseen");
		if(matcher.matches(ctx.message)) {
			String user = matcher.nextWord();
			
			findLastSeen(ctx, user);
		}
	}
	
	private void findLastSeen(BotContext ctx, String user) {
		Date lastSeen = getLastSeenTime(ctx.channel, user);
		if(lastSeen == null) {
			ctx.reply("I never heard of that \"nujabes\" you're asking about ?");
		} else {
			ctx.reply(user + " was last seen " + HandlerUtils.prettyDate(lastSeen));
		}
	}
	
	private Date getLastSeenTime(String channel, String user) {
		String channelEscaped = DatabaseLogService.escapeChannel(channel);
		LogLine msg = DatabaseLogService.lastMessageByUser(channelEscaped, user);
		if(msg != null) {
			return msg.date;
		}
		
		return null;
	}
	
	@Override
	public String getName() {
		return "+lastseen";
	}
	
	@Override
	public void help(BotContext ctx) {
		ctx.reply("Ask when a user was last chatting: +lastseen <user>");
	}
}
