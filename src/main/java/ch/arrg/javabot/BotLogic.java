package ch.arrg.javabot;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.data.DataStoreUtils;
import ch.arrg.javabot.data.UserData;
import ch.arrg.javabot.data.UserDb;
import ch.arrg.javabot.handlers.AdminHandler;
import ch.arrg.javabot.handlers.CurrencyHandler;
import ch.arrg.javabot.handlers.HelloHandler;
import ch.arrg.javabot.handlers.JoinMissedLogHandler;
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

// TODO ImageDetectionHandler : automatic image description 
// TODO auto pause main bot when beta bot joins
// TODO MoratoireHandler : pose des moratoires sur des sujets de conv
// TODO fix canonisation and check over all known nicks
// TODO (unrelated) charts API for the log
// TODO names and help for event handlers

public class BotLogic {
	
	private Map<String, CommandHandler> handlers = new TreeMap<>();
	private Set<CommandHandler> disabled = new HashSet<>();
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
		addEventHandler(new JoinMissedLogHandler());
		
		userDb = DataStoreUtils.fromFile(Const.DATA_FILE);
		DataStoreUtils.saveOnQuit(Const.DATA_FILE, userDb);
	}
	
	private void addHandler(CommandHandler h) {
		handlers.put(h.getName(), h);
		
		if(h instanceof IrcEventHandler) {
			eventHandlers.put(h.getName(), (IrcEventHandler) h);
		}
	}
	
	private void addEventHandler(IrcEventHandler h) {
		eventHandlers.put(h.getClass().getName(), h);
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
				if(disabled.contains(handler))
					continue;
				
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
	
	public Boolean toggleHandler(String handlerName) {
		CommandHandler handler = handlers.get(handlerName);
		if(handler == null)
			return null;
		
		if(disabled.contains(handler)) {
			disabled.remove(handler);
			return true;
		} else {
			disabled.add(handler);
			return false;
		}
	}
	
}
