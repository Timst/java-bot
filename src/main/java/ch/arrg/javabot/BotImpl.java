package ch.arrg.javabot;

import java.io.IOException;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;

import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.data.UserData;
import ch.arrg.javabot.log.DatabaseLogService;
import ch.arrg.javabot.log.DatabaseLogService.LogEvent;

/** Main bot logic
 * 
 * @author tgi */
public class BotImpl extends PircBot implements Bot {
	
	private static final String ENCODING = "utf-8";
	
	private BotLogic logic = new BotLogic();
	
	private boolean isPaused = false;
	
	public BotImpl() throws Exception {
		setName(Const.BOT_NAME);
		setLogin(Const.BOT_NAME);
		setEncoding(ENCODING);
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
	protected void onMessage(String channel, String sender, String login, String hostname,
			String message) {
		BotContext ctx = new BotContext(this, channel, sender, login, hostname, message);
		
		DatabaseLogService.logEvent(LogEvent.MESSAGE, ctx);
		logic.onMessage(ctx);
	}
	
	@Override
	protected void onJoin(String channel, String sender, String login, String hostname) {
		BotContext ctx = new BotContext(this, channel, sender, login, hostname, null);
		DatabaseLogService.logEvent(LogEvent.JOIN, ctx);
		logic.onJoin(ctx);
	}
	
	@Override
	protected void onQuit(String sender, String login, String hostname, String reason) {
		BotContext ctx = new BotContext(this, Const.CHANNEL, sender, login, hostname, reason);
		DatabaseLogService.logEvent(LogEvent.QUIT, ctx);
	}
	
	@Override
	protected void onPart(String channel, String sender, String login, String hostname) {
		BotContext ctx = new BotContext(this, channel, sender, login, hostname, "part");
		DatabaseLogService.logEvent(LogEvent.PART, ctx);
	}
	
	@Override
	protected void onAction(String sender, String login, String hostname, String channel,
			String action) {
		BotContext ctx = new BotContext(this, channel, sender, login, hostname, action);
		DatabaseLogService.logEvent(LogEvent.JOIN, ctx);
	}
	
	@Override
	protected void onTopic(String channel, String topic, String setBy, long date,
			boolean changed) {
		// TODO logging topic : hostname and co ?
		BotContext ctx = new BotContext(this, channel, setBy, setBy, setBy, topic);
		DatabaseLogService.logEvent(LogEvent.TOPIC, ctx);
	}
	
	@Override
	protected void onNickChange(String oldNick, String login, String hostname, String newNick) {
		BotContext ctx = new BotContext(this, Const.CHANNEL, oldNick, login, hostname, newNick);
		
		DatabaseLogService.logEvent(LogEvent.NICK, ctx);
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
		quitServer(Const.QUIT_MESSAGE);
		System.exit(1);
	}
	
	@Override
	public void adminPause() {
		isPaused = true;
	}
	
	@Override
	public void adminUnpause() {
		isPaused = false;
	}
	
	@Override
	public boolean isPaused() {
		return isPaused;
	}
}
