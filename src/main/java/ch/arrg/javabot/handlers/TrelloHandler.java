package ch.arrg.javabot.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.http.impl.client.DefaultHttpClient;

import com.google.common.base.Joiner;
import com.julienvey.trello.TrelloHttpClient;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.ApacheHttpClient;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.Const;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.util.CommandMatcher;

// TODO Trello : remove DEBUG logging

public class TrelloHandler implements CommandHandler {
	private final com.julienvey.trello.Trello trello;
	private final String boardId;
	// TODO Trello : make configurable allowed lists
	private List<String> allowedLists = new ArrayList<String>();
	// TODO Trello : make configurable newListId
	private String newListId = "5607b4fa318dc47ac0060bbd";
	
	private CurrentTask currentTask;
	
	public TrelloHandler() {
		String apiKey = Const.str("trello.api_key");
		String token = Const.str("trello.api_token");
		boardId = Const.str("trello.board_id");
		
		// TODO deprecated
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		TrelloHttpClient thc = new ApacheHttpClient(httpClient);
		trello = new TrelloImpl(apiKey, token, thc);
		
		allowedLists.add("5607b5067c7a5934876c228a");
		allowedLists.add("5607b5017819b8f537470681");
		allowedLists.add("5607b4fa318dc47ac0060bbd");
	}
	
	@Override
	public String getName() {
		return "+trello";
	}
	
	@Override
	public void help(BotContext ctx) {
		ctx.reply("+trello (read|link|todo|description) <gamename> <streamnumber>");
	}
	
	@Override
	public void handle(BotContext ctx) {
		CommandMatcher cm = CommandMatcher.make("+trello");
		if(cm.matches(ctx.message)) {
			String action = cm.peekWord();
			if("todo".equals(action)) {
				handleMissing(ctx, cm);
			} else if("read".equals(action)) {
				handleRead(ctx, cm);
			} else if("link".equals(action)) {
				handleLink(ctx, cm);
			} else if("create".equals(action)) {
				handleCreate(ctx, cm);
			} else if("description".equals(action)) {
				handleDescription(ctx, cm);
			} else if("commit".equals(action)) {
				handleCommit(ctx, cm);
			} else if("cancel".equals(action)) {
				handleCancel(ctx, cm, false);
			}
		}
		
		if(currentTask != null) {
			tryRecordForCurrentTask(ctx);
		}
	}
	
	private void tryRecordForCurrentTask(BotContext ctx) {
		if(ctx.message.startsWith("+")) {
			return;
		}
		
		if(currentTask == null) {
			return;
		}
		
		if(!currentTask.owner.equals(ctx.sender)) {
			return;
		}
		
		currentTask.lines.add(ctx.message);
	}
	
	private void handleCancel(BotContext ctx, CommandMatcher cm, boolean silentOnNone) {
		if(currentTask != null) {
			String name = currentTask.card.game + " " + currentTask.card.number;
			ctx.reply("Cancelling pending description for " + name);
			currentTask = null;
		} else if(!silentOnNone) {
			ctx.reply("No pending description to cancel.");
		}
	}
	
	private void handleCommit(BotContext ctx, CommandMatcher cm) {
		if(currentTask == null) {
			ctx.reply("No pending description to commit.");
			return;
		}
		
		String name = currentTask.card.game + " " + currentTask.card.number;
		
		Optional<Card> cardO = getCard(currentTask.card);
		if(!cardO.isPresent()) {
			ctx.reply("Card `" + name + "` not found on Trello. Create it before commiting.");
			return;
		}
		
		Card card = cardO.get();
		String newDesc = Joiner.on("\n").join(currentTask.lines);
		card.setDesc(newDesc);
		trello.updateCard(card);
		ctx.reply("Card `" + name + "` updated : " + card.getUrl());

		currentTask = null;
	}
	
	private void handleDescription(BotContext ctx, CommandMatcher cm) {
		if(currentTask != null) {
			handleCancel(ctx, cm, true);
		}
		
		cm.nextWord();
		CardInfo ci = getCardInfoFromCommand(cm);
		Optional<Card> oc = getCard(ci);
		
		if(!oc.isPresent()) {
			ctx.reply("Card not found");
			return;
		}
		Card card = oc.get();
		
		ctx.reply("Enter title (with \"= \") and description for " + card.getName()
				+ ", use +trello commit or +trello cancel once finished.");
		
		CurrentTask ct = new CurrentTask();
		ct.card = ci;
		ct.lines = new ArrayList<String>();
		ct.owner = ctx.sender;
		
		currentTask = ct;
	}
	
	private void handleCreate(BotContext ctx, CommandMatcher cm) {
		cm.nextWord();
		String game = cm.nextWord();
		
		String newCardNoS = findNextNumber(game);
		String cardName = game + " " + newCardNoS;
		
		// Create card
		// TODO Trello create card : insert at end of list
		// TODO Trello create card : attach to a user
		Card card = new Card();
		card.setName(cardName);
		card.setPos(999);
		Card created = trello.createCard(newListId, card);
		
		// Reply
		String url = created.getUrl();
		ctx.reply("Card created : " + url);
	}
	
	private String findNextNumber(String game) {
		Stream<Card> cards = getAllNonArchivedCards();
		Optional<Integer> maxNoO = cards
				.map(c -> c.getName())
				.filter(n -> n.startsWith(game))
				.map(n -> getNumberFromName(n))
				.max(Integer::compare);
		
		int maxNo = maxNoO.orElse(0);
		
		int newCardNo = 1;
		if(maxNo > 0) {
			newCardNo = maxNo + 1;
		}
		
		return integerToPaddedString(newCardNo);
	}
	
	private Optional<Card> getCard(CardInfo ci) {
		String expectedName = ci.game + " " + ci.number;
		Stream<Card> cards = getAllNonArchivedCards();
		cards = cards.filter(c -> c.getName().equals(expectedName));
		return cards.findFirst();
	}
	
	private void handleRead(BotContext ctx, CommandMatcher cm) {
		cm.nextWord();
		CardInfo ci = getCardInfoFromCommand(cm);
		Optional<Card> oc = getCard(ci);
		
		if(!oc.isPresent()) {
			ctx.reply("Card not found");
			return;
		}
		
		Card c = oc.get();
		ctx.reply("Found card `" + c.getName() + "`, description : ");
		String desc = c.getDesc();
		String[] lines = desc.split("\\n");
		for(String line : lines) {
			ctx.reply("| " + line);
		}
	}
	
	private void handleLink(BotContext ctx, CommandMatcher cm) {
		cm.nextWord();
		CardInfo ci = getCardInfoFromCommand(cm);
		Optional<Card> oc = getCard(ci);
		
		if(!oc.isPresent()) {
			ctx.reply("Card not found");
			return;
		}
		
		Card c = oc.get();
		ctx.reply("Found card `" + c.getName() + "`: " + c.getUrl());
	}
	
	private void handleMissing(BotContext ctx, CommandMatcher cm) {
		Stream<Card> cards = getAllNonArchivedCards();
		
		// Group by game
		Map<String, List<CardInfo>> todo = cards
				.filter(c -> allowedLists.contains(c.getIdList()))
				.filter(c -> "".equals(c.getDesc()))
				.map(c -> infoFromCard(c))
				.collect(Collectors.groupingBy(c -> c.game));
		
		if(todo.isEmpty()) {
			ctx.reply("No cards to do");
		} else {
			int count = todo.values().stream().map(l -> l.size()).mapToInt(Integer::intValue).sum();
			ctx.reply(count + " cards are missing descriptions: ");
			
			// Sort sublists and display
			todo.forEach((g, l) -> ctx.reply(g + ": " + Joiner.on(", ").join(
					l.stream().map(ci -> ci.number).sorted().iterator())));
		}
	}
	
	// TODO may benefit from caching (for 1 request)
	private Stream<Card> getAllNonArchivedCards() {
		Stream<Card> allCards = trello.getBoardCards(boardId).stream();
		Stream<Card> notArchived = allCards.filter(c -> !c.isClosed());
		return notArchived;
	}
	
	private static CardInfo getCardInfoFromCommand(CommandMatcher cm) {
		CardInfo card = new CardInfo();
		card.game = cm.nextWord();
		card.number = cm.nextWord();
		return card;
	}
	
	private static CardInfo infoFromCard(Card c) {
		String cardName = c.getName();
		
		// Split title "gamename 01"
		int space = cardName.indexOf(' ');
		String game;
		String number;
		if(space == -1) {
			game = cardName;
			number = null;
		} else {
			game = cardName.substring(0, space);
			number = cardName.substring(space + 1);
		}
		
		CardInfo ci = new CardInfo();
		ci.game = game;
		ci.number = number;
		return ci;
	}
	
	private static class CardInfo {
		String game;
		String number;
	}
	
	private class CurrentTask {
		CardInfo card;
		String owner;
		List<String> lines;
	}
	
	/** pubg 02 -> 2 */
	private static int getNumberFromName(String name) {
		int space = name.indexOf(' ');
		String num = name.substring(space + 1);
		try {
			int n = Integer.parseInt(num);
			return n;
		} catch (NumberFormatException e) {
			// Ignore card
			// TODO log warning
			return -1;
		}
	}
	
	/** 1 -> "01"
	 * 10 -> "10" */
	private static String integerToPaddedString(int newCardNo) {
		String newCardNoS;
		if(newCardNo < 10) {
			newCardNoS = "0" + newCardNo;
		} else {
			newCardNoS = "" + newCardNo;
		}
		return newCardNoS;
	}
}
