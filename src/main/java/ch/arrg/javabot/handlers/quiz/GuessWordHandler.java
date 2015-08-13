package ch.arrg.javabot.handlers.quiz;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.data.UserDb;
import ch.arrg.javabot.log.LogLine;
import ch.arrg.javabot.util.LogLines;

import com.google.common.base.Strings;

public class GuessWordHandler extends AbstractQuizHandler {

	public GuessWordHandler() {
		QUESTION_TIMEOUT = 25;
	}

	@Override
	public void help(BotContext ctx) {
		ctx.reply("Braisnchat trivia word game !!");
		ctx.reply("Use +guessword to start and stop the game");
	}

	@Override
	public QuizQuestion getNewQuestion() {
		return new GuessWordQuestion(LogLines.LOG_LINES);
	}

	@Override
	public String getQuizName() {
		return "guessword";
	}

	private static class GuessWordQuestion implements QuizQuestion {
		private static final int WORD_SIZE = 5;

		private final LogLine logLine;

		private final String word;
		private final String censored;

		private boolean solved = false;

		private List<String> tried = new ArrayList<>();

		public GuessWordQuestion(List<LogLine> lines) {
			// Valid sentences must be long enough
			// And have at least one valid word

			LogLine sentence = null;
			while (sentence == null) {
				int lIdx = (int) (Math.random() * lines.size());
				LogLine tmp = lines.get(lIdx);
				if (isValidLine(tmp)) {
					sentence = tmp;
				}
			}

			this.logLine = sentence;
			word = chooseWord(sentence);
			String replacement = Strings.repeat("*", word.length());
			censored = sentence.message.replaceAll(Pattern.quote(word), replacement);
		}

		private static String chooseWord(LogLine sentence) {
			String[] words = splitLine(sentence);
			while (true) {
				// Terminates because line is valid
				int idx = (int) (Math.random() * words.length);
				if (words[idx].length() > WORD_SIZE) {
					return words[idx];
				}
			}
		}

		private static boolean isValidLine(LogLine tmp) {

			if (!"pubmsg".equals(tmp.kind)) {
				return false;
			}
			if (tmp.message.length() < 30) {
				return false;
			}

			String[] words = splitLine(tmp);
			for (String word : words) {
				if (word.length() > WORD_SIZE) {
					// There's a valid word
					return true;
				}
			}

			return false;
		}

		private static String[] splitLine(LogLine tmp) {
			// TODO improve what a 'word' is
			return tmp.message.split(" ");
		}

		@Override
		public void success(BotContext ctx, int score) {
			ctx.reply("Correct ! The word was \"" + word + "\". " + ctx.sender + " now has " + score
					+ ". Next question...");
		}

		@Override
		public void ask(BotContext ctx) {
			ctx.reply("Guess the missing word: [" + logLine.user + ": " + censored + "]");
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
		public void cancel(BotContext ctx) {
			ctx.reply("The missing word was \"" + word + "\". Losers");
		}

		@Override
		public void timeout(BotContext ctx) {
			ctx.reply("The missing word was \"" + word + "\". Losers");
		}
	}
}