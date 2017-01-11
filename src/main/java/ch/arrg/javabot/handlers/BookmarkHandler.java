package ch.arrg.javabot.handlers;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Joiner;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.data.UserData;
import ch.arrg.javabot.util.CommandMatcher;

public class BookmarkHandler implements CommandHandler {

	private static final String DATA_KEY = "BookmarkHandler";

	@Override
	public void handle(BotContext ctx) {

		CommandMatcher matcher = CommandMatcher.make("+bm");
		if(matcher.matches(ctx.message)) {
			String command = matcher.peekWord();
			if("create".equals(command)) {
				matcher.nextWord();
				String key = matcher.nextWord();
				String url = matcher.remaining();
				saveBookmark(ctx, key, url);
			} else if("delete".equals(command)) {
				matcher.nextWord();
				String key = matcher.nextWord();
				deleteBookmark(ctx, key);
			} else if("ls".equals(command)) {
				listBookmarks(ctx);
			} else {
				String key = matcher.nextWord();
				getBookmark(ctx, key);
			}
		}
	}

	private Map<String, String> getBookmarks(BotContext ctx) {
		UserData ud = ctx.getUserData(DATA_KEY);
		HashMap<String, String> bms = ud.getOrInit(DATA_KEY, new HashMap<String, String>());
		return bms;
	}

	private void saveBookmark(BotContext ctx, String key, String url) {
		getBookmarks(ctx).put(key, url);
		ctx.reply("Bookmark added");
	}

	private void deleteBookmark(BotContext ctx, String key) {
		String deleted = getBookmarks(ctx).remove(key);
		if(deleted != null) {
			ctx.reply("Bookmark deleted.");
		} else {
			ctx.reply("No such bookmark.");
		}
	}

	private void listBookmarks(BotContext ctx) {
		Map<String, String> bookmarks = getBookmarks(ctx);
		String keys = Joiner.on(", ").join(bookmarks.keySet());
		ctx.reply("Known bookmarks: " + keys);
	}

	private void getBookmark(BotContext ctx, String key) {
		String out = getBookmarks(ctx).get(key);
		if(out == null) {
			ctx.reply("No such bookmark.");
		} else {
			ctx.reply(out);
		}
	}

	@Override
	public String getName() {
		return "+bm";
	}

	@Override
	public void help(BotContext ctx) {
		ctx.reply("Save book marks for frequently used URLs/informations.");
		ctx.reply("Use +bm create <key> <url> to create a bookmark.");
		ctx.reply("+bm <key> to read it, +bm delete <key> to delete it.");
	}
}
