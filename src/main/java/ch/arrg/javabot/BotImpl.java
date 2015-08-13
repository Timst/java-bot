package ch.arrg.javabot;

import java.io.IOException;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;

import ch.arrg.javabot.data.UserData;

/**
 * Main bot logic
 * 
 * @author tgi
 */
public class BotImpl extends PircBot implements Bot {

	private BotLogic logic = new BotLogic();

	public BotImpl() throws Exception {
		setName(Const.BOT_NAME);
		setLogin(Const.BOT_NAME);
		setEncoding("utf-8");
	}

	public void start() throws NickAlreadyInUseException, IOException, IrcException {
		connect(Const.SERVER_URL, Const.SERVER_PORT);
	}

	@Override
	protected void onConnect() {
		super.onConnect();
		joinChannel(Const.CHANNEL);
	}

	@Override
	protected void onMessage(String channel, String sender, String login, String hostname, String message) {
		logic.onMessage(this, channel, sender, login, hostname, message);
	}

	@Override
	protected void onJoin(String channel, String sender, String login, String hostname) {

	}

	@Override
	public void sendMsg(String target, String message) {
		sendMessage(target, message);
	}

	@Override
	public UserData getUserData(String user) {
		return logic.getUserData(user);
	}

	@Override
	public void quit() {
		quitServer("Thanks mr skeltal");
		System.exit(1);
	}
}
