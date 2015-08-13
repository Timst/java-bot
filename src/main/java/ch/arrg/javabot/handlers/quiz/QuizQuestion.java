package ch.arrg.javabot.handlers.quiz;

import ch.arrg.javabot.util.Replyer;

public interface QuizQuestion {

	public abstract void success(Replyer rep, String sender, int score);

	public abstract void ask(Replyer rep);

	public abstract boolean tryGuess(String sender, String message);

	public abstract void cancel(Replyer rep);

	public abstract void timeout(Replyer rep);

}