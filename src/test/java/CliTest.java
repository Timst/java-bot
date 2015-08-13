import java.util.Scanner;

import ch.arrg.javabot.Bot;
import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.data.UserData;
import ch.arrg.javabot.data.UserDb;
import ch.arrg.javabot.handlers.HelloHandler;

public class CliTest {
	public static void main(String[] args) {

		Bot bot = new FakeBot();

		CommandHandler ch = new HelloHandler();
		try (Scanner s = new Scanner(System.in)) {
			while (s.hasNextLine()) {
				String line = s.nextLine();
				if (line.equals("")) {
					break;
				}
				ch.handle(bot, "##braisnchat", "arrg_ch", "arrg", "hostname", line);
			}
		}
	}

	static class FakeBot implements Bot {
		UserDb db = new UserDb();

		@Override
		public void sendMsg(String target, String message) {
			System.out.println("> " + message);
		}

		@Override
		public UserData getUserData(String user) {
			return db.getOrCreateUserData(user);
		}

		@Override
		public void quit() {
			System.out.println("Quit");
		}
	}
}
