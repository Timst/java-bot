package ch.arrg.javabot.handlers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.arrg.javabot.IrcEventHandler;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.data.UserData;
import ch.arrg.javabot.data.UserDb;
import ch.arrg.javabot.util.CommandMatcher;

public class MemoHandler implements IrcEventHandler {
	
	private static final String DATA_KEY = "MemoHandler";
	
	@Override
	public void handle(BotContext ctx) {
		
		CommandMatcher matcher = CommandMatcher.make("+memo");
		if(matcher.matches(ctx.message)) {
			String user = matcher.nextWord();
			
			MemoMessage msg = new MemoMessage(ctx.sender, user, new Date(), matcher.remaining());
			storeMessage(ctx, msg);
			
			ctx.reply("Ok I'll tell " + user + " when xir get back.");
		}
	}
	
	private static void storeMessage(BotContext ctx, MemoMessage msg) {
		String userCanon = UserDb.canonize(msg.to);
		
		UserData dat = ctx.getUserData(userCanon);
		List<MemoMessage> messages = dat.getOrInit(DATA_KEY, new ArrayList<MemoMessage>());
		
		messages.add(msg);
		
		dat.setRecord(DATA_KEY, messages);
	}
	
	@Override
	public void onJoin(String user, BotContext ctx) {
		String userCanon = UserDb.canonize(user);
		
		UserData dat = ctx.getUserData(userCanon);
		List<MemoMessage> messages = dat.getOrInit(DATA_KEY, new ArrayList<MemoMessage>());
		
		if(messages != null && !messages.isEmpty()) {
			ctx.reply("Hi " + user + ", here are messages for you : ");
			for(MemoMessage msg : messages) {
				ctx.reply("From " + msg.from + " on " + msg.when + " : " + msg.msg);
			}
			
			messages.clear();
			dat.setRecord(DATA_KEY, messages);
		}
	}
	
	static class MemoMessage implements Serializable {
		private final String from;
		private final String to;
		private final Date when;
		private final String msg;
		
		public MemoMessage(String from, String to, Date when, String msg) {
			super();
			this.from = from;
			this.to = to;
			this.when = when;
			this.msg = msg;
		}
	}
	
	@Override
	public String getName() {
		return "+memo";
	}
	
	@Override
	public void help(BotContext ctx) {
		ctx.reply("Store a message for when a user connects.");
		ctx.reply("Use +memo <user> <message> to store a message.");
	}
}
