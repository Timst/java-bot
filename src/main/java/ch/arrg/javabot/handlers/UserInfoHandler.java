package ch.arrg.javabot.handlers;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.data.UserDb;
import ch.arrg.javabot.util.HandlerUtils;

public class UserInfoHandler implements CommandHandler {

	@Override
	public void handle(BotContext ctx) {

		String message = ctx.message;
		if ((message = HandlerUtils.withKeyword("userinfo", message)) != null) {
			String[] words = message.split("\\s+");
			if (words == null || words.length == 0) {
				return;
			}

			String action = words[0];
			String reply = null;
			if (action.equals("canon")) {
				reply = "Your canonical username is " + UserDb.canonize(ctx.sender);
			}

			if (action.equals("records")) {
				int keys = ctx.getUserData(ctx.sender).countKeys();
				reply = "I have " + keys + " records about you.";
			}

			ctx.reply(reply);
		}

	}

	@Override
	public String getName() {
		return "+userinfo";
	}

	@Override
	public void help(BotContext ctx) {
		ctx.reply("Ask what I know about you");
		ctx.reply("Known subcommands: records, canon");
	}

}
