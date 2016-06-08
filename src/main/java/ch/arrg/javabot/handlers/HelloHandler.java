package ch.arrg.javabot.handlers;

import java.util.List;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.Const;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.util.HandlerUtils;

import com.google.common.collect.Lists;

public class HelloHandler implements CommandHandler {
	
	private static final String KEY_HELLO_COUNT = "hellos";
	
	private final List<String> helloWords;
	
	public HelloHandler() {
		helloWords = Lists.newArrayList("Hello", "Hi", "Yo", "Lo", "Salut", "Bonjour",
				"Guten Tag");
	}
	
	@Override
	public void handle(BotContext ctx) {
		
		for(String helloWord : helloWords) {
			if(ctx.message.equalsIgnoreCase(helloWord + " " + Const.BOT_NAME)) {
				int cntPlusOne = updateCount(ctx);
				String replyWord = HandlerUtils.random(helloWords);
				
				ctx.reply(replyWord + " " + ctx.sender + " ! You've greeted me " + cntPlusOne
						+ " times.");
			}
		}
	}
	
	private static int updateCount(BotContext ctx) {
		String cnt = ctx.getRecordStr(KEY_HELLO_COUNT);
		int cntPlusOne = 1;
		try {
			int i = Integer.parseInt(cnt);
			cntPlusOne = i + 1;
		} catch (NumberFormatException e) {
			// Ignore
		}
		ctx.setRecord(KEY_HELLO_COUNT, "" + cntPlusOne);
		return cntPlusOne;
	}
	
	@Override
	public String getName() {
		return "+hello";
	}
	
	@Override
	public void help(BotContext ctx) {
		ctx.reply("Say hello to me");
	}
}
