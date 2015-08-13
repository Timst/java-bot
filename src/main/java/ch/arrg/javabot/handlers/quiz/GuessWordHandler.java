package ch.arrg.javabot.handlers.quiz;

import java.util.ArrayList;
import java.util.List;

import ch.arrg.javabot.data.UserDb;
import ch.arrg.javabot.log.LogLine;
import ch.arrg.javabot.util.HandlerUtils;
import ch.arrg.javabot.util.LogLines;
import ch.arrg.javabot.util.Replyer;

import com.google.common.base.Strings;

public class GuessWordHandler extends AbstractQuizHandler {

	public GuessWordHandler() {
		QUESTION_TIMEOUT = 30;
	}

	@Override
	public void help(Replyer rep, String message) {
		rep.send("Braisnchat trivia word game !!");
		rep.send("Use +guessword to start and stop the game");
	}

	@Override
	public QuizQuestion getNewQuestion() {
		while (true) {
			try {
				LogLine sentence = selectSentence();
				return new GuessWordQuestion(sentence);
			} catch (IllegalStateException e) {
			}
		}
	}

	private LogLine selectSentence() {
		while (true) {
			int idx = (int) (Math.random() * LogLines.LOG_LINES.size());
			LogLine tmp = LogLines.LOG_LINES.get(idx);
			if ("pubmsg".equals(tmp.kind)) {
				if (tmp.message.length() > 30) {
					return tmp;
				}
			}
		}
	}

	@Override
	public String getQuizName() {
		return "guessword";
	}

	private static class GuessWordQuestion implements QuizQuestion {
		private final LogLine logLine;

		private final String word;
		private final String censored;

		private boolean solved = false;

		private List<String> tried = new ArrayList<String>();

		public GuessWordQuestion(LogLine line) {
			this.logLine = line;

			int tries = 0;
			String[] words = line.message.split(" ");
			String chosen = null;
			while (chosen == null) {
				int idx = HandlerUtils.random(0, words.length - 1);
				String cand = words[idx];
				if (cand.length() > 5) {
					chosen = cand;
				}
				tries++;
				if (tries > 10) {
					throw new IllegalStateException();
				}
			}

			word = chosen;
			String replacement = Strings.repeat("*", word.length());
			censored = line.message.replaceAll(word, replacement);
		}

		@Override
		public void success(Replyer rep, String sender, int score) {
			rep.send("Correct ! The word was \"" + word + "\". " + sender + " now has " + score + ". Next question...");
		}

		@Override
		public void ask(Replyer rep) {
			rep.send("Guess the missing word: [" + logLine.user + ": " + censored + "]");
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

			String canonGuess = message.replaceAll("\\+", "").toLowerCase();
			String canonCorrect = word.toLowerCase();

			if (canonCorrect.equals(canonGuess)) {
				solved = true;
				return true;
			}

			return false;
		}

		@Override
		public void cancel(Replyer rep) {
			rep.send("The missing word was \"" + word + "\". Losers");
		}

		@Override
		public void timeout(Replyer rep) {
			rep.send("The missing word was \"" + word + "\". Losers");
		}
	}
}