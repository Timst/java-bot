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

import ch.arrg.javabot.Bot;
import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.util.HandlerUtils;
import ch.arrg.javabot.util.Replyer;

public abstract class AbstractQuizHandler implements CommandHandler {

	protected int QUESTION_TIMEOUT = 20;

	protected int QUESTION_DELAY = 4;

	protected int SCORE_NO = -1;

	protected int SCORE_YES = 2;

	protected int SCORE_LIMIT = 7;

	private Map<String, Integer> scores = new HashMap<String, Integer>();

	private QuizQuestion currentQuestion;

	@Override
	public void handle(Bot bot, String channel, String sender, String login, String hostname, String message) {
		final Replyer rep = HandlerUtils.makeReplyer(bot, channel);

		if (currentQuestion != null) {
			if (message.startsWith("+")) {
				onReply(sender, message, rep);
			}
		}

		if ((message = HandlerUtils.withKeyword(getQuizName(), message)) != null) {
			onCommand(rep);
		}
	}

	private void onReply(String sender, String message, final Replyer rep) {
		boolean success = currentQuestion.tryGuess(sender, message);

		int score = changeScore(sender, success);

		if (score >= SCORE_LIMIT) {
			endgame(rep);
			return;
		}

		if (success) {
			currentQuestion.success(rep, sender, score);

			Executors.newScheduledThreadPool(1).schedule(new Runnable() {
				@Override
				public void run() {
					askNewQuestion(rep);
				}
			}, QUESTION_DELAY, TimeUnit.SECONDS);
		}
	}

	private void onCommand(final Replyer rep) {
		if (currentQuestion == null) {
			rep.send("Starting the game !");
			askNewQuestion(rep);
		} else {
			currentQuestion.cancel(rep);
			currentQuestion = null;
			rep.send("Stopping the game !");
			endgame(rep);
		}
	}

	private void endgame(Replyer rep) {
		Map<Integer, Set<String>> scoreBoard = new TreeMap<Integer, Set<String>>(Collections.reverseOrder());
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
				rep.send("Game is over, " + e.getValue().iterator().next() + " won !");
				rep.send("Scoreboard: ");
			}

			int score = e.getKey();
			for (String user : e.getValue()) {
				rep.send("#" + rank + " " + user + " (" + score + ")");
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

	private void timeout(Replyer rep) {
		currentQuestion.timeout(rep);
		currentQuestion = null;
		askNewQuestion(rep);
	}

	private void askNewQuestion(final Replyer rep) {
		final QuizQuestion newQuestion = getNewQuestion();
		scheduleTimeout(rep, newQuestion);
		currentQuestion = newQuestion;
		currentQuestion.ask(rep);
	}

	private void scheduleTimeout(final Replyer rep, final QuizQuestion newQuestion) {
		Runnable timeout = new Runnable() {
			@Override
			public void run() {
				if (currentQuestion == newQuestion) {
					timeout(rep);
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