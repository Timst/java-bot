package ch.arrg.javabot.handlers;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.util.CommandMatcher;

public class SayHandler implements CommandHandler {
	
	@Override
	public void handle(BotContext ctx) {
		
		CommandMatcher cm = CommandMatcher.make("+say");
		if(cm.matches(ctx.message)) {
			String channel = cm.nextWord();
			String message = cm.remaining();
			ctx.sendMsg(channel, message);
		}
	}
	
	@Override
	public String getName() {
		return "+say";
	}
	
	@Override
	public void help(BotContext ctx) {
		ctx.reply("Ask me to say something with +say <channel> <msg>");
	}
}
