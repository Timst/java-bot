package ch.arrg.javabot.handlers;

import java.util.ArrayList;
import java.util.List;

import ch.arrg.javabot.Bot;
import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.util.HandlerUtils;
import ch.arrg.javabot.util.Replyer;

public class RecordHandler implements CommandHandler {

	private final List<String> allowedRecords = new ArrayList<>();

	public RecordHandler() {
		allowedRecords.add(TimeHandler.TZ_RECORD);
	}

	@Override
	public void handle(Bot bot, String channel, String sender, String login,
			String hostname, String message) {

		if ((message = HandlerUtils.withKeyword("record", message)) != null) {
			handle(HandlerUtils.makeReplyer(bot, channel), bot, sender, message);
		}

	}

	private void handle(Replyer replyer, Bot bot, String sender, String message) {
		String[] words = message.split("\\s+");
		if (words == null || words.length == 0) {
			return;
		}

		String action = words[0];
		if (allowedRecords.contains(action)) {
			String value = words[1];
			bot.getUserData(sender).setRecord(action, value);
			replyer.send("Okay I will remember.");
		} else {
			replyer.send("This is now a known key, doot");
		}
	}

	@Override
	public String getName() {
		return "+record";
	}

	@Override
	public void help(Replyer rep, String message) {
		rep.send("Set info about yourself.");
		rep.send("Use +record <key> <value> to set a record.");
		String allowed = String.join(", ", allowedRecords);
		rep.send("Allowed records are: [" + allowed + "]");
	}
}
