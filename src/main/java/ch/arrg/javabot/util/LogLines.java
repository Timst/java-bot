package ch.arrg.javabot.util;

import java.util.List;

import ch.arrg.javabot.log.DatabaseLogServiceProvider;
import ch.arrg.javabot.log.LogLine;

public class LogLines {

	private static List<LogLine> LOG_LINES;

	static {
		try {
			// TODO incrementally update from DB
			// or deprecate in favor of DLService
			LOG_LINES = DatabaseLogServiceProvider.get().readAllLog("#braisnchat");
		} catch (Exception e) {
			Logging.logException(e);
		}
	}

	public static List<LogLine> getLogLines() {
		return LOG_LINES;
	}
}
