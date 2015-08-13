package ch.arrg.javabot.handlers;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.data.UserDb;
import ch.arrg.javabot.util.CommandMatcher;

public class UserInfoHandler implements CommandHandler {

	@Override
	public void handle(BotContext ctx) {

		String message = ctx.message;
		CommandMatcher matcher;

		matcher = CommandMatcher.make("+userinfo canon");
		if (matcher.matches(message)) {
			ctx.reply("Your canonical username is " + UserDb.canonize(ctx.sender));
		}

		matcher = CommandMatcher.make("+userinfo records");
		if (matcher.matches(message)) {
			int keys = ctx.getUserData(ctx.sender).countKeys();
			ctx.reply("I have " + keys + " records about you.");
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
