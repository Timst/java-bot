package ch.arrg.javabot;

import java.io.FileInputStream;
import java.util.Properties;

import ch.arrg.javabot.util.Logging;

/** Application configuration.
 * 
 * @author tgi */
public class Const {
	private static final Properties props = new Properties();
	
	static {
		try (FileInputStream fis = new FileInputStream("data/config.properties")) {
			props.load(fis);
		} catch (Exception e) {
			Logging.logException(e);
		}
	}
	
	public static String str(String key) {
		return props.getProperty(key);
	}
	
	private static int asInt(String key) {
		return Integer.parseInt(props.getProperty(key));
	}
	
	public static final String BOT_NAME = str("bot.name");
	public static final String SERVER_URL = str("server.url");
	public static final int SERVER_PORT = asInt("server.port");
	public static final String CHANNEL = str("channel");
	public static final String DATA_FILE = str("data.file");
	
	public static final String QUIT_MESSAGE = str("quit.message");
	public static final String DB_DRIVER = str("db.driver");
	public static final String DB_CONN_STRING = str("db.conn.string");
	public static final String DB_TABLE = str("db.table");
}
