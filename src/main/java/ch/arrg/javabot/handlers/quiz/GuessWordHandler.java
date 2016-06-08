package ch.arrg.javabot.handlers.quiz;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
		ctx.reply("Each sentence will have a word replaced with ****. Guess the missing word.");
		ctx.reply("Hint: the number of asterisks matches the number of characters in the word.");
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
		private static final int MIN_WORD_SIZE = 5;
		private static final int MAX_WORD_SIZE = 12;
		
		private final LogLine logLine;
		
		private final String missingWord;
		private final String censored;
		
		private boolean solved = false;
		
		private Set<String> tried = new HashSet<>();
		
		public GuessWordQuestion(List<LogLine> lines) {
			// Valid sentences must be long enough
			// And have at least one valid word
			
			LogLine sentence = null;
			while(sentence == null) {
				int lIdx = (int) (Math.random() * lines.size());
				LogLine tmp = lines.get(lIdx);
				if(isValidLine(tmp)) {
					sentence = tmp;
				}
			}
			
			this.logLine = sentence;
			missingWord = chooseWord(sentence);
			String replacement = Strings.repeat("*", missingWord.length());
			replacement = replacement + "[" + missingWord.length() + "]";
			censored = sentence.message.replaceAll(Pattern.quote(missingWord), replacement);
		}
		
		private static String chooseWord(LogLine sentence) {
			String[] words = splitLine(sentence);
			while(true) {
				// Terminates because line is valid
				int idx = (int) (Math.random() * words.length);
				String word = words[idx];
				if(isValidWord(word)) {
					return word;
				}
			}
		}
		
		private static boolean isValidLine(LogLine tmp) {
			
			if(!"pubmsg".equals(tmp.kind)) {
				return false;
			}
			if(tmp.message.length() < 30) {
				return false;
			}
			
			if(tmp.message.contains("http")) {
				return false;
			}
			
			String[] words = splitLine(tmp);
			for(String word : words) {
				if(isValidWord(word)) {
					// There's a valid word
					return true;
				}
			}
			
			return false;
		}
		
		private static boolean isValidWord(String word) {
			return word.length() > MIN_WORD_SIZE && word.length() < MAX_WORD_SIZE;
		}
		
		private static String[] splitLine(LogLine tmp) {
			// TODO improve what a 'word' is
			return tmp.message.split("[\\s(\\),.:!?\\[\\]<>]");
		}
		
		@Override
		public void success(BotContext ctx, int score) {
			ctx.reply("Correct ! The word was \"" + missingWord + "\". " + ctx.sender
					+ " now has " + score + ". Next question...");
		}
		
		@Override
		public void ask(BotContext ctx) {
			ctx.reply("Guess : [" + logLine.user + ": " + censored + "]");
		}
		
		@Override
		public Boolean tryGuess(String sender, String message) {
			if(solved)
				return false;
			
			String canonGuess = message.replaceAll("\\+", "").toLowerCase();
			String canonCorrect = missingWord.toLowerCase();
			
			if(canonGuess.length() != canonCorrect.length()) {
				return null;
			}
			
			String cSender = UserDb.canonize(sender);
			if(tried.contains(cSender)) {
				return null;
			}
			tried.add(cSender);
			
			if(canonCorrect.equals(canonGuess)) {
				solved = true;
				return true;
			}
			
			return false;
		}
		
		@Override
		public void cancel(BotContext ctx) {
			ctx.reply("The missing word was \"" + missingWord + "\". Losers");
		}
		
		@Override
		public void timeout(BotContext ctx) {
			ctx.reply("The missing word was \"" + missingWord + "\". Losers");
		}
	}
}