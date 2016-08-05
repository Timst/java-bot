package ch.arrg.javabot;

import java.util.Map;
import java.util.TreeMap;

import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.data.DataStoreUtils;
import ch.arrg.javabot.data.UserData;
import ch.arrg.javabot.data.UserDb;
import ch.arrg.javabot.handlers.AdminHandler;
import ch.arrg.javabot.handlers.CurrencyHandler;
import ch.arrg.javabot.handlers.HelloHandler;
import ch.arrg.javabot.handlers.LastSeenHandler;
import ch.arrg.javabot.handlers.MarkovHandler;
import ch.arrg.javabot.handlers.MemoHandler;
import ch.arrg.javabot.handlers.QuoteLogHandler;
import ch.arrg.javabot.handlers.RecordHandler;
import ch.arrg.javabot.handlers.SteamUrlHandler;
import ch.arrg.javabot.handlers.TimeHandler;
import ch.arrg.javabot.handlers.UrlTitleHandler;
import ch.arrg.javabot.handlers.UserInfoHandler;
import ch.arrg.javabot.handlers.YoutubeHandler;
import ch.arrg.javabot.handlers.quiz.GuessWhoHandler;
import ch.arrg.javabot.handlers.quiz.GuessWordHandler;
import ch.arrg.javabot.util.CommandMatcher;
import ch.arrg.javabot.util.Logging;

// TODO MissedChatsHandler : on join, indicates how many lines you've missed
// TODO ImageDetectionHandler : automatic image description 
// TODO auto pause main bot when beta bot joins
// TODO MoratoireHandler : pose des moratoires sur des sujets de conv
// TODO fix canonisation and check over all known nicks
// TODO contextual timezone conversions
// TODO (unrelated) charts API for the log

public class BotLogic {
	
	private Map<String, CommandHandler> handlers = new TreeMap<>();
	private Map<String, IrcEventHandler> eventHandlers = new TreeMap<>();
	
	private final UserDb userDb;
	
	public BotLogic() throws Exception {
		addHandler(new HelloHandler());
		addHandler(new TimeHandler());
		addHandler(new RecordHandler());
		addHandler(new UserInfoHandler());
		addHandler(new GuessWhoHandler());
		addHandler(new GuessWordHandler());
		addHandler(new AdminHandler());
		addHandler(new YoutubeHandler());
		addHandler(new UrlTitleHandler());
		addHandler(new SteamUrlHandler());
		addHandler(new LastSeenHandler());
		addHandler(new MarkovHandler());
		addHandler(new MemoHandler());
		addHandler(new QuoteLogHandler());
		addHandler(new CurrencyHandler());
		
		userDb = DataStoreUtils.fromFile(Const.DATA_FILE);
		DataStoreUtils.saveOnQuit(Const.DATA_FILE, userDb);
	}
	
	private void addHandler(CommandHandler h) {
		handlers.put(h.getName(), h);
		
		if(h instanceof IrcEventHandler) {
			eventHandlers.put(h.getName(), (IrcEventHandler) h);
		}
	}
	
	protected void onMessage(BotContext ctx) {
		if(ctx.isPaused()) {
			onMessagePauseMode(ctx);
			return;
		}
		
		String message = ctx.message.trim();
		
		CommandMatcher matcher = CommandMatcher.make("+help");
		if(matcher.matches(message)) {
			onHelp(ctx, matcher.nextWord());
			return;
		}
		
		for(CommandHandler handler : handlers.values()) {
			try {
				handler.handle(ctx);
			} catch (Exception e) {
				Logging.logException(e);
			}
		}
	}
	
	protected void onMessagePauseMode(BotContext ctx) {
		for(CommandHandler handler : handlers.values()) {
			if(!(handler instanceof AdminHandler))
				continue;
			
			try {
				handler.handle(ctx);
			} catch (Exception e) {
				Logging.logException(e);
			}
		}
	}
	
	public void onJoin(BotContext ctx) {
		if(ctx.isPaused())
			return;
		
		for(IrcEventHandler handler : eventHandlers.values()) {
			try {
				handler.onJoin(ctx.sender, ctx);
			} catch (Exception e) {
				Logging.logException(e);
			}
		}
	}
	
	private void onHelp(BotContext ctx, String topic) {
		
		if(handlers.containsKey(topic)) {
			handlers.get(topic).help(ctx);
		} else {
			
			ctx.reply("Here are known handlers: ");
			StringBuilder sb = new StringBuilder();
			for(CommandHandler handler : handlers.values()) {
				sb.append(handler.getName()).append(" ");
			}
			ctx.reply(sb.toString());
			ctx.reply("Send +help <handler> to get more info on a particular one.");
		}
	}
	
	public UserData getUserData(String user) {
		return userDb.getOrCreateUserData(user);
	}
	
}
