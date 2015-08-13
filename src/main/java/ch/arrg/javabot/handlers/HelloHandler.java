package ch.arrg.javabot.handlers;

import ch.arrg.javabot.Bot;
import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.Const;
import ch.arrg.javabot.util.Replyer;

public class HelloHandler implements CommandHandler {

	private static final String KEY_HELLO_COUNT = "hellos";

	@Override
	public void handle(Bot bot, String channel, String sender, String login,
			String hostname, String message) {

		if (message.equalsIgnoreCase("Hello " + Const.BOT_NAME)) {
			String cnt = bot.getUserData(sender).getRecord(KEY_HELLO_COUNT);
			int cntPlusOne = 1;
			try {
				int i = Integer.parseInt(cnt);
				cntPlusOne = i + 1;
			} catch (NumberFormatException e) {

			}
			bot.getUserData(sender).setRecord(KEY_HELLO_COUNT, "" + cntPlusOne);

			bot.sendMsg(channel, "Hello " + sender + " ! You've said hello "
					+ cntPlusOne + " times.");

		}
	}

	@Override
	public String getName() {
		return "+hello";
	}

	@Override
	public void help(Replyer rep, String message) {
		rep.send("Say hello to me");
	}
}
