package ch.arrg.javabot.handlers.quiz;

import java.util.ArrayList;
import java.util.List;

import ch.arrg.javabot.data.UserDb;
import ch.arrg.javabot.log.LogLine;
import ch.arrg.javabot.util.LogLines;
import ch.arrg.javabot.util.Replyer;

public class GuessWhoHandler extends AbstractQuizHandler {

	public GuessWhoHandler() {

	}

	@Override
	public void help(Replyer rep, String message) {
		rep.send("Braisnchat trivia game !!");
		rep.send("Use +guesswho to start and stop the game");
	}

	@Override
	public QuizQuestion getNewQuestion() {
		while (true) {
			int idx = (int) (Math.random() * LogLines.LOG_LINES.size());
			LogLine tmp = LogLines.LOG_LINES.get(idx);
			if ("pubmsg".equals(tmp.kind)) {
				if (tmp.message.length() > 20) {
					return new GuessWhoQuestion(tmp);
				}
			}
		}
	}

	@Override
	public String getQuizName() {
		return "guesswho";
	}

	private static class GuessWhoQuestion implements QuizQuestion {
		private final LogLine logLine;

		private boolean solved = false;

		private List<String> tried = new ArrayList<String>();

		public GuessWhoQuestion(LogLine line) {
			this.logLine = line;
		}

		@Override
		public boolean tryGuess(String sender, String message) {
			if (solved)
				return false;

			String cSender = UserDb.canonize(sender);
			if (tried.contains(cSender)) {
				return false;
			}
			tried.add(cSender);

			String canonGuess = UserDb.canonize(message);
			String canonCorrect = UserDb.canonize(logLine.user);

			if (canonCorrect.equals(canonGuess)) {
				solved = true;
				return true;
			}

			return false;
		}

		@Override
		public void cancel(Replyer rep) {
			rep.send("It was " + logLine.user + " who said \"" + logLine.message + "\". Losers");
		}

		@Override
		public void timeout(Replyer rep) {
			rep.send("It was " + logLine.user + " who said \"" + logLine.message + "\". Losers");
		}

		@Override
		public void success(Replyer rep, String sender, int score) {
			rep.send("Correct ! It was " + logLine.user + " who said it. " + sender + " now has " + score
					+ ". Next question...");
		}

		@Override
		public void ask(Replyer rep) {
			rep.send("Who said: \"" + logLine.message + "\" ?");
		}
	}
}