package ch.arrg.javabot.log;

import ch.arrg.javabot.Const;

public class DatabaseLogServiceProvider {
	
	private static DatabaseLogService INST;
	
	static {
		if(Const.str("db.enable").equals("true")) {
			INST = new DatabaseLogServiceImpl();
		} else {
			INST = new DatabaseLogServiceDummy();
		}
	}
	
	public static DatabaseLogService get() {
		return INST;
	}
}
