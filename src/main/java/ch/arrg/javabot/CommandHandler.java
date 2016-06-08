package ch.arrg.javabot;

import ch.arrg.javabot.data.BotContext;

/** A command handler is able to receive commands and react to them.
 * 
 * @author tgi */
public interface CommandHandler {
	public void handle(BotContext ctx);
	
	public String getName();
	
	public void help(BotContext ctx);
}
