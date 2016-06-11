package ch.arrg.javabot.util;

import java.util.List;

import ch.arrg.javabot.log.DatabaseLogService;
import ch.arrg.javabot.log.LogLine;

public class LogLines {
	
	private static List<LogLine> LOG_LINES;
	
	static {
		try {
			// TODO incrementally update from DB
			LOG_LINES = DatabaseLogService.readAllLog("#braisnchat");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static List<LogLine> getLogLines() {
		return LOG_LINES;
	}
}
