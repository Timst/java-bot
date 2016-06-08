package ch.arrg.javabot.handlers.quiz;

import ch.arrg.javabot.data.BotContext;

public interface QuizQuestion {
	
	public abstract void success(BotContext ctx, int score);
	
	public abstract void ask(BotContext ctx);
	
	public abstract Boolean tryGuess(String sender, String message);
	
	public abstract void cancel(BotContext ctx);
	
	public abstract void timeout(BotContext ctx);
	
}