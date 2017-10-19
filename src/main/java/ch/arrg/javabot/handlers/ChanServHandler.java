package ch.arrg.javabot.handlers;

import java.util.HashMap;
import java.util.Map;

import ch.arrg.javabot.IrcEventHandler;
import ch.arrg.javabot.data.BotContext;

public class ChanServHandler implements IrcEventHandler {
	// TODO make runtime or conf configurable
	private final static Map<String, String> STATUSES = new HashMap<String, String>();

	public ChanServHandler() {
		STATUSES.put("Bretton_Woods", "voice");
		STATUSES.put("Cardinal2Richeli", "voice");
	}

	@Override
	public void handle(BotContext ctx) {
		// Noop
	}

	public void onJoin() {
		// List channel users and try to voice them
	}

	@Override
	public void onJoin(String user, BotContext ctx) {
		trySetStatus(user, ctx);
	}

	private void trySetStatus(String user, BotContext ctx) {
		if(STATUSES.containsKey(user)) {
			String status = STATUSES.get(user);
			ctx.reply("/msg ChanServ " + status + " " + ctx.channel + " " + user);
		}
	}

	@Override
	public String getName() {
		return "chanserv";
	}

	@Override
	public void help(BotContext ctx) {
		ctx.reply("Automatically sets status for some users.");
	}
}
