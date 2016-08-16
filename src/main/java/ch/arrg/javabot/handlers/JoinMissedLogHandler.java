package ch.arrg.javabot.handlers;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.Const;
import ch.arrg.javabot.IrcEventHandler;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.data.UserDb;
import ch.arrg.javabot.log.DatabaseLogService;
import ch.arrg.javabot.log.LogLine;

// TODO make opt-in ?

public class JoinMissedLogHandler implements CommandHandler, IrcEventHandler {
	
	final static String OPT_OUT_RECORD = "no_missed_log";
	
	@Override
	public void onJoin(String user, BotContext ctx) {
		String optOutRec = ctx.getRecordStr(OPT_OUT_RECORD);
		if("true".equals(optOutRec)) {
			return;
		}
		
		String userCanon = UserDb.canonize(user);
		LogLine lastMsg = DatabaseLogService
				.lastMessageByUser(DatabaseLogService.escapeChannel(ctx.channel), userCanon);
		if(lastMsg != null) {
			sendResponse(user, ctx, lastMsg);
		}
	}
	
	private void sendResponse(String user, BotContext ctx, LogLine lastMsg) {
		int lastId = lastMsg.id;
		String urlPattern = Const.str("JoinMissedLogHandler.logurl");
		String url = urlPattern.replaceAll("%s", "" + lastId);
		int missedLines = DatabaseLogService.getNumberOfMessagesSince(DatabaseLogService.escapeChannel(ctx.channel),
				lastId);
		
		if(missedLines > 0) {
			ctx.sendMsg(user, "Heya. You've missed " + missedLines + " lines.");
			ctx.sendMsg(user, "See log here : " + url);
		}
	}
	
	public String getName() {
		return "nomissedlog";
	}
	
	public void help(BotContext ctx) {
		ctx.reply("Tells you how much log you've missed when you join.");
		ctx.reply("Disable with +record no_missed_log true");
	}
	
	@Override
	public void handle(BotContext ctx) {
		// Nothing
	}
}
