package ch.arrg.javabot;

import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.data.DataStoreUtils;
import ch.arrg.javabot.data.UserData;
import ch.arrg.javabot.data.UserDb;
import ch.arrg.javabot.handlers.HelloHandler;
import ch.arrg.javabot.handlers.QuestionHandler;
import ch.arrg.javabot.handlers.QuitHandler;
import ch.arrg.javabot.handlers.RecordHandler;
import ch.arrg.javabot.handlers.TimeHandler;
import ch.arrg.javabot.handlers.UserInfoHandler;
import ch.arrg.javabot.handlers.quiz.GuessWhoHandler;
import ch.arrg.javabot.handlers.quiz.GuessWordHandler;
import ch.arrg.javabot.util.HandlerUtils;

public class BotLogic {

	private Map<String, CommandHandler> handlers = new TreeMap<>();

	private final UserDb userDb;

	public BotLogic() throws Exception {
		addHandler(new HelloHandler());
		addHandler(new TimeHandler());
		addHandler(new RecordHandler());
		addHandler(new UserInfoHandler());
		addHandler(new QuestionHandler());
		addHandler(new GuessWhoHandler());
		addHandler(new GuessWordHandler());
		addHandler(new QuitHandler());

		userDb = DataStoreUtils.fromFile(Const.DATA_FILE);
		DataStoreUtils.saveOnQuit(Const.DATA_FILE, userDb);
	}

	private void addHandler(CommandHandler h) {
		handlers.put(h.getName(), h);
	}

	protected void onMessage(BotContext ctx) {
		String message = ctx.message.trim();

		if (message.startsWith("+help")) {
			onHelp(ctx);
			return;
		}

		for (CommandHandler handler : handlers.values()) {
			try {
				handler.handle(ctx.bot, ctx.channel, ctx.sender, ctx.login, ctx.hostname, message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void onHelp(BotContext ctx) {

		String handlerName = HandlerUtils.getWord(ctx.message, 1);
		if (handlers.containsKey(handlerName)) {
			handlers.get(handlerName).help(ctx.replyer(), ctx.message);
		} else {

			ctx.reply("Here are known handlers: ");
			StringJoiner join = new StringJoiner(" ");
			for (CommandHandler handler : handlers.values()) {
				join.add(handler.getName());
			}
			ctx.reply(join.toString());
			ctx.reply("Send +help <handler> to get more info on a particular one.");
		}
	}

	public UserData getUserData(String user) {
		return userDb.getOrCreateUserData(user);
	}

}
