package ch.arrg.javabot.log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import ch.arrg.javabot.Const;
import ch.arrg.javabot.data.BotContext;

// TODO Database logging as a handler ?
public class DatabaseLogService {
	
	public enum LogEvent {
		MESSAGE("pubmsg"), //
		ACTION("action"), //
		JOIN("join"), //
		QUIT("quit"), //
		PART("part"), //
		NICK("nick"), //
		TOPIC("topic");
		
		private final String dbName;
		
		private LogEvent(String dbName) {
			this.dbName = dbName;
		}
	}
	
	public static void logEvent(LogEvent inType, BotContext ctx) {
		try (Connection conn = getConnection()) {
			String query = "INSERT INTO `"
					+ Const.DB_TABLE
					+ "` (channel, name, time, message, type, hidden) VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			
			String channel = ctx.channel;
			String name = ctx.sender;
			Timestamp time = new Timestamp(new java.util.Date().getTime());
			String message = ctx.message;
			String type = inType.dbName;
			String hidden = "F";
			
			preparedStatement.setString(1, channel);
			preparedStatement.setString(2, name);
			preparedStatement.setTimestamp(3, time);
			preparedStatement.setString(4, message);
			preparedStatement.setString(5, type);
			preparedStatement.setString(6, hidden);
			
			preparedStatement.executeUpdate();
			
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static Connection getConnection() throws ClassNotFoundException, SQLException {
		// Load DB driver
		Class.forName(Const.DB_DRIVER);
		
		// Connect using db string
		Connection connect = DriverManager.getConnection(Const.DB_CONN_STRING);
		return connect;
	}
}
