package ch.arrg.javabot.handlers.quiz;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.util.CommandMatcher;

public abstract class AbstractQuizHandler implements CommandHandler {

	protected int QUESTION_TIMEOUT = 20;

	protected int QUESTION_DELAY = 4;

	protected int SCORE_NO = -1;

	protected int SCORE_YES = 2;

	protected int SCORE_LIMIT = 7;

	private Map<String, Integer> scores = new HashMap<>();

	private QuizQuestion currentQuestion;

	@Override
	public void handle(BotContext ctx) {
		String message = ctx.message;

		if (currentQuestion != null) {
			if (message.startsWith("+")) {
				onReply(ctx);
			}
		}

		if (CommandMatcher.make("+" + getQuizName()).matches(message)) {
			onCommand(ctx);
		}
	}

	private void onReply(final BotContext ctx) {
		String sender = ctx.sender;

		boolean success = currentQuestion.tryGuess(sender, ctx.message);

		int score = changeScore(sender, success);

		if (score >= SCORE_LIMIT) {
			endgame(ctx);
			return;
		}

		if (success) {
			currentQuestion.success(ctx, score);

			Executors.newScheduledThreadPool(1).schedule(new Runnable() {
				@Override
				public void run() {
					askNewQuestion(ctx);
				}
			}, QUESTION_DELAY, TimeUnit.SECONDS);
		}
	}

	private void onCommand(final BotContext ctx) {
		if (currentQuestion == null) {
			ctx.reply("Starting the game !");
			askNewQuestion(ctx);
		} else {
			currentQuestion.cancel(ctx);
			currentQuestion = null;
			ctx.reply("Stopping the game !");
			endgame(ctx);
		}
	}

	private void endgame(BotContext ctx) {
		Map<Integer, Set<String>> scoreBoard = new TreeMap<>(Collections.reverseOrder());
		for (Entry<String, Integer> e : scores.entrySet()) {
			Integer score = e.getValue();
			if (!scoreBoard.containsKey(score)) {
				scoreBoard.put(score, new TreeSet<String>());
			}

			scoreBoard.get(score).add(e.getKey());
		}

		int rank = 1;
		int eq = 0;
		for (Entry<Integer, Set<String>> e : scoreBoard.entrySet()) {
			if (rank == 1 && eq == 0) {
				ctx.reply("Game is over, " + e.getValue().iterator().next() + " won !");
				ctx.reply("Scoreboard: ");
			}

			int score = e.getKey();
			for (String user : e.getValue()) {
				ctx.reply("#" + rank + " " + user + " (" + score + ")");
				eq++;
			}

			rank += eq;
			eq = 0;
		}

		currentQuestion = null;
		scores.clear();
	}

	private int changeScore(String sender, boolean success) {
		if (!scores.containsKey(sender)) {
			scores.put(sender, 0);
		}

		int curr = scores.get(sender);
		curr += success ? SCORE_YES : SCORE_NO;

		scores.put(sender, curr);
		return scores.get(sender);
	}

	private void timeout(BotContext rep) {
		currentQuestion.timeout(rep);
		currentQuestion = null;
		askNewQuestion(rep);
	}

	private void askNewQuestion(final BotContext ctx) {
		final QuizQuestion newQuestion = getNewQuestion();
		scheduleTimeout(ctx, newQuestion);
		currentQuestion = newQuestion;
		currentQuestion.ask(ctx);
	}

	private void scheduleTimeout(final BotContext ctx, final QuizQuestion newQuestion) {
		Runnable timeout = new Runnable() {
			@Override
			public void run() {
				if (currentQuestion == newQuestion) {
					timeout(ctx);
				}
			}
		};
		Executors.newScheduledThreadPool(1).schedule(timeout, QUESTION_TIMEOUT, TimeUnit.SECONDS);
	}

	@Override
	public String getName() {
		return "+" + getQuizName();
	}

	public abstract QuizQuestion getNewQuestion();

	public abstract String getQuizName();
}