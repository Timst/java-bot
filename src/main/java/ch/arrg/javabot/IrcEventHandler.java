package ch.arrg.javabot;

import ch.arrg.javabot.data.BotContext;

/** A handler that is able to react to IRC events such as join, part, etc.
 * 
 * @author tgi */
public interface IrcEventHandler extends CommandHandler {
	
	public void onJoin(String user, BotContext ctx);
}
