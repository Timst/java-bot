package ch.arrg.javabot;

import java.io.IOException;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;

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

/**
 * Main bot logic
 * 
 * @author tgi
 */
public class BotImpl extends PircBot implements Bot {

	private Map<String, CommandHandler> handlers = new TreeMap<>();

	private final UserDb userDb;

	public BotImpl() throws Exception {
		addHandler(new HelloHandler());
		addHandler(new TimeHandler());
		addHandler(new RecordHandler());
		addHandler(new UserInfoHandler());
		addHandler(new QuestionHandler());
		addHandler(new GuessWhoHandler());
		addHandler(new GuessWordHandler());
		addHandler(new QuitHandler());

		setName(Const.BOT_NAME);
		setLogin(Const.BOT_NAME);

		userDb = DataStoreUtils.fromFile(Const.DATA_FILE);
		DataStoreUtils.saveOnQuit(Const.DATA_FILE, userDb);
	}

	public void start() throws NickAlreadyInUseException, IOException, IrcException {
		setEncoding("utf-8");
		connect(Const.SERVER_URL, Const.SERVER_PORT);
	}

	private void addHandler(CommandHandler h) {
		handlers.put(h.getName(), h);
	}

	@Override
	protected void onConnect() {
		super.onConnect();
		joinChannel(Const.CHANNEL);
	}

	@Override
	protected void onMessage(String channel, String sender, String login, String hostname, String message) {

		message = message.trim();

		if (message.startsWith("+help")) {
			onHelp(channel, message);
			return;
		}

		for (CommandHandler handler : handlers.values()) {
			try {
				handler.handle(this, channel, sender, login, hostname, message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onJoin(String channel, String sender, String login, String hostname) {

	}

	private void onHelp(String channel, String message) {

		String handlerName = HandlerUtils.getWord(message, 1);
		if (handlers.containsKey(handlerName)) {
			handlers.get(handlerName).help(HandlerUtils.makeReplyer(this, channel), message);
		} else {

			sendMsg(channel, "Here are known handlers: ");
			StringJoiner join = new StringJoiner(" ");
			for (CommandHandler handler : handlers.values()) {
				join.add(handler.getName());
			}
			sendMsg(channel, join.toString());
			sendMsg(channel, "Send +help <handler> to get more info on a particular one.");
		}
	}

	@Override
	public void sendMsg(String target, String message) {
		sendMessage(target, message);
	}

	@Override
	public UserData getUserData(String user) {
		return userDb.getOrCreateUserData(user);
	}

	@Override
	public void quit() {
		quitServer("Thanks mr skeltal");
		System.exit(1);
	}
}
