package ch.arrg.javabot.handlers;

import ch.arrg.javabot.Bot;
import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.data.UserDb;
import ch.arrg.javabot.util.Replyer;

public class QuitHandler implements CommandHandler {

	@Override
	public void handle(Bot bot, String channel, String sender, String login, String hostname, String message) {

		if ("+quit".equals(message) && UserDb.canonize(sender).equals("arrg")) {
			bot.sendMsg(channel, "Thanks mr skeltal");
			bot.quit();
		}
	}

	@Override
	public String getName() {
		return "+quit";
	}

	@Override
	public void help(Replyer rep, String message) {
		rep.send("Kill me");
	}
}
