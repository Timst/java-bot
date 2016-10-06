package ch.arrg.javabot.handlers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.util.CommandMatcher;

public class FiveThirtyEightHandler implements CommandHandler {

	private final static String KEYWORD = "+538";
	private final static String BASE_URL = "http://projects.fivethirtyeight.com/2016-election-forecast/";
	
	@Override
	public void handle(BotContext ctx) {
		CommandMatcher matcher = CommandMatcher.make(KEYWORD);

		if(!matcher.matches(ctx.message)) {
			return;
		}
		
		
		//Other types are updated locally; can't retrieve them with Soup.
//		try {
//			PollType type = PollType.getAsStream().filter(p -> p.keyword.equals(poll)).findFirst().get();
//			RetrieveInfo(ctx, type);
//		} catch (NoSuchElementException e) {
//			help(ctx);
//		}
		
		RetrieveInfo(ctx, PollType.POLLS_ONLY);
	}
	
	private void RetrieveInfo(BotContext ctx, PollType type) {
		try {
			Document doc = Jsoup.connect(BASE_URL + type.getUrlExtension()).get();
			
			String hillary = doc.select(".powerbarheads .dem .candidate-val").first().text();
			String trump = doc.select(".powerbarheads .rep .candidate-val").first().text();
			String updatedMilliseconds = doc.select(".tab-timestamp .timestamp-time-presidency").first().attr("data-timestamp");
			Date updated = new Date(Long.parseLong(updatedMilliseconds));
			
			
			ctx.reply("Latest poll-only information: Hillary Clinton " + hillary + ", Donald Trump " + trump + ". Updated " + new SimpleDateFormat("dd/MM/YY HH:mm zzz").format(updated) + ".");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return KEYWORD;
	}

	@Override
	public void help(BotContext ctx) {
		ctx.reply("Returns the latest 538 2016 US presidential election polls-only data");
	}

	private enum PollType {
		POLLS_ONLY("polls-only", ""),
		POLLS_PLUS("polls-plus", "#plus"),
		NOW_CAST("now-cast", "#now");
		
		private final String keyword;
		private final String urlExtension;
		
		private PollType(String keyword, String urlExtension) {
			this.keyword = keyword;
			this.urlExtension = urlExtension;
		}
		
		public String getKeyword() { return keyword; }
		public String getUrlExtension() { return urlExtension; }
		public static Stream<PollType> getAsStream() { return Arrays.stream(values()); }
	}
}
