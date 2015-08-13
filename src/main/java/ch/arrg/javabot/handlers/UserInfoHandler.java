package ch.arrg.javabot.handlers;

import ch.arrg.javabot.Bot;
import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.data.UserDb;
import ch.arrg.javabot.util.HandlerUtils;
import ch.arrg.javabot.util.Replyer;

public class UserInfoHandler implements CommandHandler {

	@Override
	public void handle(Bot bot, String channel, String sender, String login,
			String hostname, String message) {

		if ((message = HandlerUtils.withKeyword("userinfo", message)) != null) {
			handle(bot, channel, sender, message);
		}

	}

	private void handle(Bot bot, String channel, String sender, String message) {
		String[] words = message.split("\\s+");
		if (words == null || words.length == 0) {
			return;
		}

		String action = words[0];
		String reply = null;
		if (action.equals("canon")) {
			reply = "Your canonical username is " + UserDb.canonize(sender);
		}

		if (action.equals("records")) {
			int keys = bot.getUserData(sender).countKeys();
			reply = "I have " + keys + " records about you.";
		}

		if (reply != null) {
			bot.sendMsg(channel, reply);
		}
	}

	@Override
	public String getName() {
		return "+userinfo";
	}

	@Override
	public void help(Replyer rep, String message) {
		rep.send("Ask what I know about you");
		rep.send("Known subcommands: records, canon");
	}

}
