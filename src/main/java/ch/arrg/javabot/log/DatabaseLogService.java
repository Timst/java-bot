package ch.arrg.javabot.log;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import ch.arrg.javabot.Const;
import ch.arrg.javabot.data.BotContext;

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
			
			String query = "INSERT INTO `?` (channel, name, time, message, type, hidden) VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			
			String channel = ctx.channel;
			String name = ctx.sender;
			Date time = new Date(new java.util.Date().getTime());
			String message = ctx.message;
			String type = inType.dbName;
			String hidden = "F";
			
			// Parameters start with 1
			preparedStatement.setString(1, Const.DB_TABLE);
			preparedStatement.setString(2, channel);
			preparedStatement.setString(3, name);
			preparedStatement.setDate(4, time);
			preparedStatement.setString(5, message);
			preparedStatement.setString(6, type);
			preparedStatement.setString(7, hidden);
			preparedStatement.executeUpdate();
			
		} catch (ClassNotFoundException | SQLException e) {
			// TODO when logging to DB fails, dump to file ?
			throw new IllegalStateException("Couldn't log.", e);
		}
	}
	
	private static Connection getConnection() throws ClassNotFoundException, SQLException {
		// This will load the MySQL driver, each DB has its own driver
		Class.forName(Const.DB_DRIVER);
		// Setup the connection with the DB
		Connection connect = DriverManager.getConnection(Const.DB_CONN_STRING);
		return connect;
	}
}
