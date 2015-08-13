package ch.arrg.javabot.handlers;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.data.UserDb;

public class QuitHandler implements CommandHandler {

	@Override
	public void handle(BotContext ctx) {

		if ("+quit".equals(ctx.message) && UserDb.canonize(ctx.sender).equals("arrg")) {
			ctx.reply("Thanks mr skeltal");
			ctx.quit();
		}
	}

	@Override
	public String getName() {
		return "+quit";
	}

	@Override
	public void help(BotContext ctx) {
		ctx.reply("Kill me");
	}
}
