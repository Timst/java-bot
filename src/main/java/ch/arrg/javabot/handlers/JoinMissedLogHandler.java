package ch.arrg.javabot.handlers;

import java.util.List;

import ch.arrg.javabot.Const;
import ch.arrg.javabot.IrcEventHandler;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.data.UserDb;
import ch.arrg.javabot.log.DatabaseLogServiceProvider;
import ch.arrg.javabot.log.LogLine;

// TODO make private messages opt-in

public class JoinMissedLogHandler implements IrcEventHandler {
	
	final static String OPT_OUT_RECORD = "no_missed_log";
	final static int VERBATIM_THRESHOLD = 10;
	
	@Override
	public void onJoin(String user, BotContext ctx) {
		String optOutRec = ctx.getRecordStr(OPT_OUT_RECORD);
		if("true".equals(optOutRec)) {
			return;
		}
		
		String userCanon = UserDb.canonize(user);
		LogLine lastMsg = DatabaseLogServiceProvider.get().lastMessageByUser(ctx.channel, userCanon);
		if(lastMsg != null) {
			sendResponse(user, ctx, lastMsg);
		}
	}
	
	private void sendResponse(String user, BotContext ctx, LogLine lastMsg) {
		int lastId = lastMsg.id;
		String urlPattern = Const.str("JoinMissedLogHandler.logurl");
		String url = urlPattern.replaceAll("%s", "" + lastId);
		int missedLines = DatabaseLogServiceProvider.get().getNumberOfMessagesSince(ctx.channel, lastId);
		
		if(missedLines > 0) {
			ctx.sendMsg(user, "Heya. You've missed " + missedLines + " lines.");
			
			if(missedLines <= VERBATIM_THRESHOLD) {
				List<LogLine> lines = DatabaseLogServiceProvider.get().getMessagesSinceId(ctx.channel, lastId);
				for(LogLine line : lines) {
					ctx.sendMsg(user, "> " + line.user + ": " + line.message);
				}
			}
			
			ctx.sendMsg(user, "See log here : " + url);
		}
	}
	
	public String getName() {
		return "nomissedlog";
	}
	
	public void help(BotContext ctx) {
		ctx.reply("Tells you how much log you've missed when you join.");
		ctx.reply("Disable with +record set no_missed_log true");
	}
	
	@Override
	public void handle(BotContext ctx) {
		// Nothing
	}
}
