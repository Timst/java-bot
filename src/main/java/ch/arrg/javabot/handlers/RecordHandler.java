package ch.arrg.javabot.handlers;

import java.util.ArrayList;
import java.util.List;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.util.HandlerUtils;

public class RecordHandler implements CommandHandler {

	private final List<String> allowedRecords = new ArrayList<>();

	public RecordHandler() {
		allowedRecords.add(TimeHandler.TZ_RECORD);
	}

	@Override
	public void handle(BotContext ctx) {

		String message = ctx.message;
		if ((message = HandlerUtils.withKeyword("record", message)) != null) {
			String[] words = message.split("\\s+");
			if (words == null || words.length == 0) {
				return;
			}

			String action = words[0];
			if (allowedRecords.contains(action)) {
				String value = words[1];
				ctx.getUserData(ctx.sender).setRecord(action, value);
				ctx.reply("Okay I will remember.");
			} else {
				ctx.reply("This is now a known key, doot");
			}
		}

	}

	@Override
	public String getName() {
		return "+record";
	}

	@Override
	public void help(BotContext ctx) {
		ctx.reply("Set info about yourself.");
		ctx.reply("Use +record <key> <value> to set a record.");
		String allowed = String.join(", ", allowedRecords);
		ctx.reply("Allowed records are: [" + allowed + "]");
	}
}
