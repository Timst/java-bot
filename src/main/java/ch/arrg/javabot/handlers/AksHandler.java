package ch.arrg.javabot.handlers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.util.CommandMatcher;
import ch.arrg.javabot.util.Logging;

public class AksHandler implements CommandHandler {

	private final static String KEYWORD = "+aks";
	private final static String SEARCH_URL = "http://www.allkeyshop.com/catalogue/search.php?sort=nameAsc&q=";

	@Override
	public void handle(BotContext ctx) {
		CommandMatcher matcher = CommandMatcher.make(KEYWORD);

		if(!matcher.matches(ctx.message)) {
			return;
		}

		String game = matcher.remaining();

		if(game.isEmpty()) {
			help(ctx);
		} else {
			extractGamePrice(ctx, game);
		}

	}

	private void extractGamePrice(BotContext ctx, String game) {

		try {
			AksGameInfo gameInfo = getGameInfo(game);

			if(gameInfo != null) {
				ctx.reply("Cheapest price for " + gameInfo.name + ": " + gameInfo.price + " (" + gameInfo.url + ")");
			} else {
				ctx.reply("Couldn't find price info for this game.");
			}

		} catch (IOException e) {
			Logging.logException(e);
			ctx.reply("Couldn't connect to AKS");
			return;
		}
	}

	public static AksGameInfo getGameInfo(String gameName) throws IOException {
		// TODO gameName needs to be url_encoded
		Document doc = Jsoup.connect(SEARCH_URL + gameName).get();
		AksGameInfo gameInfo = parseDocument(doc);
		return gameInfo;
	}

	private static AksGameInfo parseDocument(Document doc) {
		// TODO this is brittle
		// One option would be to make the DOM paths configurable and access
		// them directly like e.g.
		// For price : .searchresults > table:nth-child(1) > tbody:nth-child(1)
		// > tr:nth-child(4) > td:nth-child(5) > strong:nth-child(2)
		if(doc.select(".searchresults").size() > 0) {

			Element searchResults = doc.select(".searchresults table tbody").first();

			for(Element tr : searchResults.children()) {
				// TODO could iterate over results to find best match

				if(tr.child(0).children().size() > 0) {
					Elements elements = tr.children();

					AksGameInfo gameInfo = new AksGameInfo();
					gameInfo.url = elements.first().select("a").first().attr("href");
					gameInfo.name = elements.get(1).text();
					gameInfo.price = elements.get(4).select("strong").first().text();

					return gameInfo;
				}
			}
		}

		return null;
	}

	@Override
	public String getName() {
		return KEYWORD;
	}

	@Override
	public void help(BotContext ctx) {
		ctx.reply("Retrieves the latest price of a game on AllKeyShop.");
		ctx.reply("Usage: +aks <game_name>");
	}

	public static class AksGameInfo {
		String name;
		String price;
		String url;
	}

}
