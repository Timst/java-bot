package ch.arrg.javabot.data;

import ch.arrg.javabot.Bot;

public class BotContext implements Bot {

	private final Bot bot;
	public final String channel;
	public final String sender;
	public final String login;
	public final String hostname;
	public final String message;

	public BotContext(Bot bot, String channel, String sender, String login, String hostname, String message) {
		this.bot = bot;
		this.channel = channel;
		this.sender = sender;
		this.login = login;
		this.hostname = hostname;
		this.message = message;
	}

	public void reply(String message) {
		if (message != null) {
			bot.sendMsg(channel, message);
		}
	}

	@Override
	public void sendMsg(String target, String message) {
		bot.sendMsg(target, message);
	}

	@Override
	public UserData getUserData(String user) {
		return bot.getUserData(user);
	}

	@Override
	public void quit() {
		bot.quit();
	}

	public String getRecord(String recordName) {
		return bot.getUserData(sender).getRecord(recordName);
	}

	public void setRecord(String key, String value) {
		bot.getUserData(sender).setRecord(key, value);
	}
}
