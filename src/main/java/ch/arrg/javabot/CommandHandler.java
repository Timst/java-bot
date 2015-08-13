package ch.arrg.javabot;

import ch.arrg.javabot.util.Replyer;

public interface CommandHandler {
	public void handle(Bot bot, String channel, String sender, String login,
			String hostname, String message);

	public String getName();

	public void help(Replyer rep, String message);
}
