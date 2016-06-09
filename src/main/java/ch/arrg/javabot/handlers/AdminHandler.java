package ch.arrg.javabot.handlers;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.Const;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.data.UserDb;
import ch.arrg.javabot.util.CommandMatcher;

public class AdminHandler implements CommandHandler {
	
	@Override
	public void handle(BotContext ctx) {
		
		if(!isAdmin(ctx)) {
			return;
		}
		
		CommandMatcher matcher = CommandMatcher.make("+admin");
		if(matcher.matches(ctx.message)) {
			String action = matcher.nextWord();
			String botName = matcher.nextWord();
			
			if(!botName.equals(Const.BOT_NAME))
				return;
			
			if("pause".equals(action)) {
				ctx.reply("zzz");
				ctx.adminPause();
			}
			
			if("unpause".equals(action)) {
				ctx.reply("I'm back bitches");
				ctx.adminUnpause();
			}
			
			if("quit".equals(action)) {
				ctx.reply("Thanks mr skeltal");
				ctx.quit();
			}
		}
	}
	
	private static boolean isAdmin(BotContext ctx) {
		return UserDb.canonize(ctx.sender).equals("arrg");
	}
	
	@Override
	public String getName() {
		return "+admin";
	}
	
	@Override
	public void help(BotContext ctx) {
		ctx.reply("Administration commands: quit");
	}
}
