package ch.arrg.javabot.handlers;

import java.util.ArrayList;
import java.util.List;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.util.CommandMatcher;

import com.google.common.base.Joiner;

public class RecordHandler implements CommandHandler {
	
	private final List<String> allowedRecords = new ArrayList<>();
	
	public RecordHandler() {
		allowedRecords.add(TimeHandler.TZ_RECORD);
		allowedRecords.add(JoinMissedLogHandler.OPT_OUT_RECORD);
	}
	
	@Override
	public void handle(BotContext ctx) {
		
		CommandMatcher matcher = CommandMatcher.make("+record");
		if(matcher.matches(ctx.message)) {
			String action = matcher.nextWord();
			
			if(allowedRecords.contains(action)) {
				String value = matcher.nextWord();
				ctx.setRecord(action, value);
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
		String allowed = Joiner.on(", ").join(allowedRecords);
		ctx.reply("Allowed records are: [" + allowed + "]");
	}
}
