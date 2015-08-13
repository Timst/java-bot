package ch.arrg.javabot.handlers;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.Const;
import ch.arrg.javabot.data.BotContext;

public class HelloHandler implements CommandHandler {

	private static final String KEY_HELLO_COUNT = "hellos";

	@Override
	public void handle(BotContext ctx) {

		if (ctx.message.equalsIgnoreCase("Hello " + Const.BOT_NAME)) {
			int cntPlusOne = updateCount(ctx);
			ctx.reply("Hello " + ctx.sender + " ! You've said hello " + cntPlusOne + " times.");
		}
	}

	private static int updateCount(BotContext ctx) {
		String cnt = ctx.getRecord(KEY_HELLO_COUNT);
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
