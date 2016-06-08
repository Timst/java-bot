package ch.arrg.javabot.handlers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.IrcEventHandler;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.data.UserDb;
import ch.arrg.javabot.util.CommandMatcher;

public class MemoHandler implements CommandHandler, IrcEventHandler {
	
	// TODO use UserDb
	Map<String, List<MemoMessage>> messages = new HashMap<String, List<MemoMessage>>();
	
	@Override
	public void handle(BotContext ctx) {
		
		CommandMatcher matcher = CommandMatcher.make("+memo");
		if(matcher.matches(ctx.message)) {
			String user = matcher.nextWord();
			
			MemoMessage msg = new MemoMessage(ctx.sender, user, new Date(),
					matcher.remaining());
			storeMessage(msg);
			
			ctx.reply("Ok I'll tell " + user + " when xir get back.");
		}
	}
	
	private void storeMessage(MemoMessage msg) {
		String userCanon = UserDb.canonize(msg.to);
		
		if(!messages.containsKey(userCanon)) {
			messages.put(userCanon, new ArrayList<MemoMessage>());
		}
		
		messages.get(userCanon).add(msg);
	}
	
	@Override
	public void onJoin(String user, BotContext ctx) {
		String userCanon = UserDb.canonize(user);
		
		List<MemoMessage> list = messages.get(userCanon);
		if(list != null && !list.isEmpty()) {
			ctx.reply("Hi " + user + ", here are messages for you : ");
			for(MemoMessage msg : list) {
				ctx.reply("From " + msg.from + " on " + msg.when + " : " + msg.msg);
			}
			
			list.clear();
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
