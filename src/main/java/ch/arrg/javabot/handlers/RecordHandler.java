package ch.arrg.javabot.handlers;

import java.util.ArrayList;
import java.util.List;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.data.UserDb;
import ch.arrg.javabot.util.CommandMatcher;

import com.google.common.base.Joiner;

// TODO : only allow +record in private messages

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
			
			if("set".equals(action)) {
				String key = matcher.nextWord();
				if(allowedRecords.contains(key)) {
					String value = matcher.nextWord();
					ctx.setRecord(key, value);
					ctx.reply("Okay I will remember.");
				} else {
					ctx.reply("This is not a known key, doot");
				}
			} else if("get".equals(action)) {
				String key = matcher.nextWord();
				String value = ctx.getRecordStr(key);
				ctx.reply("Your value for <" + key + "> is <" + value + ">.");
				
			} else if("count".equals(action)) {
				int keys = ctx.getUserData(UserDb.canonize(ctx.sender)).countKeys();
				ctx.reply("I have " + keys + " records about you.");
			}
		}
	}
	
	@Override
	public String getName() {
		return "+record";
	}
	
	@Override
	public void help(BotContext ctx) {
		ctx.reply("Set/get info about yourself.");
		ctx.reply("Use +record set <key> <value> to set a record.");
		ctx.reply("Use +record get <key> to read a record.");
		String allowed = Joiner.on(", ").join(allowedRecords);
		ctx.reply("Allowed records are: [" + allowed + "]");
	}
}
