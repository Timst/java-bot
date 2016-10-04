package ch.arrg.javabot.handlers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.util.CommandMatcher;

public class AksHandler implements CommandHandler {

	private final static String KEYWORD = "+aks";
	private final static String SEARCH_GAME_NAME = "GAMENAME";
	private final static String SEARCH_URL = "http://www.allkeyshop.com/catalogue/search.php?q=" + SEARCH_GAME_NAME + "&sort=nameAsc";
	
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
		String actualSearchUrl = SEARCH_URL.replace(SEARCH_GAME_NAME, game);
		
		String name = "";
		String price = "";
		String url = "";
		
		try {
			Document doc = Jsoup.connect(actualSearchUrl).get();
			
			if(doc.select(".searchresults").size() > 0) {
				Element searchResults = doc.select(".searchresults table tbody").first();
				
				for(Element tr : searchResults.children()) {
					if(tr.child(0).children().size() > 0) {
						Elements elements = tr.children();
						
						url = elements.first().select("a").first().attr("href");
						name = elements.get(1).text();
						price = elements.get(4).select("strong").first().text();
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!name.isEmpty()) {
			ctx.reply("Cheapest price for " + name + ": " + price + " (" + url + ")");
		} else {
			ctx.reply("Didn't find a match.");
		}
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

}
	