package ch.arrg.javabot.util;

import ch.arrg.javabot.Bot;

public class Replyer {
	private Bot bot;
	private String channel;

	public Replyer(Bot bot, String channel) {
		this.bot = bot;
		this.channel = channel;
	}

	public void send(String message) {
		if (message != null) {
			bot.sendMsg(channel, message);
		}
	}
}
