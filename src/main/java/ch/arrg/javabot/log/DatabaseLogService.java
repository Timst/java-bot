package ch.arrg.javabot.log;

import java.util.List;

import ch.arrg.javabot.data.BotContext;

public interface DatabaseLogService {
	
	void logEvent(LogEvent inType, BotContext ctx);
	
	List<LogLine> readAllLog(String channel);
	
	LogLine lastMessageByUser(String channel, String user);
	
	int getNumberOfMessagesSince(String channel, int lastId);
	
	List<LogLine> getMessagesSinceId(String channel, int lastId);
	
	LogLine getById(String channel, Integer lineId);
	
}