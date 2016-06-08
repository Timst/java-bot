package ch.arrg.javabot.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.data.UserDb;
import ch.arrg.javabot.log.LogLine;
import ch.arrg.javabot.util.CommandMatcher;
import ch.arrg.javabot.util.LogLines;

/** @author Thomas */
public class MarkovHandler implements CommandHandler {
	
	// Cache of models by user
	private Map<String, MarkovModel> modelsByUser = new HashMap<>();
	private static final int MODEL_DEPTH = 2;
	
	@Override
	public void handle(BotContext ctx) {
		
		CommandMatcher matcher = CommandMatcher.make("+markov");
		if(matcher.matches(ctx.message)) {
			String user = matcher.nextWord();
			String userCanon = UserDb.canonize(user);
			
			if(!modelsByUser.containsKey(userCanon)) {
				MarkovModel model = ModelBuilder.buildModel(MODEL_DEPTH, userCanon);
				stats(ctx, model);
				modelsByUser.put(userCanon, model);
			}
			
			String sentence = "";
			do {
				sentence = modelsByUser.get(userCanon).predict();
			} while(sentence != null && sentence.length() < 50);
			
			if(sentence == null)
				ctx.reply("Not enough data.");
			else
				ctx.reply(user + ": " + sentence);
		}
	}
	
	private void stats(BotContext ctx, MarkovModel model) {
		double sum = 0;
		for(List<?> l : model.probs.values()) {
			sum += l.size();
		}
		int size = model.probs.size();
		double avg = sum / size;
		String info = "Built markov model N=" + MODEL_DEPTH + " with " + size + " states and "
				+ avg + " avg transitions.";
		
		ctx.reply(info);
		System.out.println(info);
	}
	
	@Override
	public String getName() {
		return "+markov";
	}
	
	@Override
	public void help(BotContext ctx) {
		ctx.reply("Use +markov <user>");
	}
	
	/** A Markov model wraps a Map of Words (input state) to a list of possible
	 * output states.
	 * Identical output states can be repeated multiple times which models
	 * transition probability.
	 * Picking the right next state is just a matter of picking one state in the
	 * list at random. */
	private static class MarkovModel {
		private final int depth;
		private final Map<Words, List<Words>> probs;
		
		public MarkovModel(Map<Words, List<Words>> probs) {
			if(probs.size() == 0) {
				depth = 0;
			} else {
				depth = probs.keySet().iterator().next().len;
			}
			
			this.probs = probs;
		}
		
		/** Predict a sentence from the model. */
		public String predict() {
			if(probs.size() == 0) {
				return null;
			}
			
			StringBuilder out = new StringBuilder();
			
			// Go through states iteratively.
			// Each new call to predictNextState will append one word to the
			// output.
			Words curr = emptyTuple();
			while(curr != null) {
				Words next = predictNextState(curr);
				
				if(next != null) {
					out.append(next.lastWord()).append(" ");
				}
				
				// Break off long sentences
				if(out.length() > 300) {
					break;
				}
				
				curr = next;
			}
			
			return out.toString();
		}
		
		/** Initial seed is the empty tuple, with the proper size. */
		private Words emptyTuple() {
			String[] zero = new String[depth];
			Arrays.fill(zero, "");
			Words emptyTuple = new Words(zero, 0, depth);
			return emptyTuple;
		}
		
		/** Predict next state from the current one. Returns null if no known
		 * next state exists. */
		private Words predictNextState(Words init) {
			
			List<Words> follow = probs.get(init);
			if(follow == null || follow.size() == 0) {
				return null;
			}
			
			int nextIdx;
			if(follow.size() == 1) {
				nextIdx = 0;
			} else {
				// When multiple options exists, never take the first one,
				// because that leads to a verbatim sentence.
				nextIdx = 1 + (int) (Math.random() * (follow.size() - 1));
			}
			
			return follow.get(nextIdx);
		}
	}
	
	private static class ModelBuilder {
		
		public static MarkovModel buildModel(int depth, String user) {
			
			HashMap<Words, List<Words>> out = new HashMap<Words, List<Words>>();
			
			for(LogLine line : LogLines.LOG_LINES) {
				if(!line.kind.equals("pubmsg"))
					continue;
				
				if(!line.user.toLowerCase().contains(user))
					continue;
				
				readLine(out, line, depth);
			}
			
			return new MarkovModel(out);
		}
		
		private static void readLine(HashMap<Words, List<Words>> out, LogLine l, int depth) {
			String[] words = l.message.split("\\s+");
			String[] wordsPlusBlanks = new String[words.length + depth];
			for(int i = 0; i < depth; i++) {
				wordsPlusBlanks[i] = "";
			}
			for(int i = 0; i < words.length; i++) {
				wordsPlusBlanks[i + depth] = words[i];
			}
			
			Words prev = null;
			for(int i = 0; i < wordsPlusBlanks.length - depth + 1; i++) {
				Words curr = new Words(wordsPlusBlanks, i, depth);
				
				if(prev != null) {
					addPair(out, prev, curr);
				}
				
				prev = curr;
			}
			
		}
		
		private static void addPair(HashMap<Words, List<Words>> out, Words prev, Words curr) {
			if(!out.containsKey(prev)) {
				out.put(prev, new ArrayList<Words>());
			}
			
			out.get(prev).add(curr);
		}
	}
	
	/** A tuple of words of length `len`. For performance reasons the source
	 * string array
	 * is shared among multiple instances, `from` denotes the start of the tuple
	 * in the array. */
	private static class Words {
		private final String[] words;
		private final int from;
		private final int len;
		
		public Words(String[] words, int from, int len) {
			this.words = words;
			this.from = from;
			this.len = len;
		}
		
		@Override
		public String toString() {
			return Arrays.toString(Arrays.copyOfRange(words, from, from + len));
		}
		
		public String lastWord() {
			return words[from + len - 1];
		}
		
		/** Tuple equality. Tuples are equal if the words of the tuple are equal,
		 * independently of
		 * the backing array and from index. */
		@Override
		public boolean equals(Object obj) {
			if(obj == null) {
				return false;
			}
			
			if(obj == this) {
				return true;
			}
			
			if(obj instanceof Words) {
				Words w = (Words) obj;
				
				if(w.len != len) {
					return false;
				}
				
				for(int i = 0; i < len; i++) {
					if(!w.words[w.from + i].equals(words[from + i])) {
						return false;
					}
				}
				
				return true;
			}
			
			return false;
		}
		
		@Override
		public int hashCode() {
			int hashCode = len;
			for(int i = 0; i < len; i++) {
				hashCode += words[from + i].hashCode();
			}
			
			return hashCode;
		}
	}
}
