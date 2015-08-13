package ch.arrg.javabot.handlers;

import java.util.Scanner;
import java.util.concurrent.Executors;

import ch.arrg.javabot.Bot;
import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.util.HandlerUtils;
import ch.arrg.javabot.util.Replyer;

public class QuestionHandler implements CommandHandler {

	private static boolean busy = false;
	private final Scanner scan;

	public QuestionHandler() {
		scan = new Scanner(System.in);
	}

	@Override
	public void handle(Bot bot, String channel, String sender, String login, String hostname, String message) {

		if ((message = HandlerUtils.withKeyword("ask", message)) != null) {
			Replyer rep = HandlerUtils.makeReplyer(bot, channel);
			if (busy) {
				rep.send("Ask again in a minute, I'm busy");
				return;
			}

			busy = true;
			Executors.newFixedThreadPool(1).execute(new QuestionRunnable(rep));
		}
	}

	@Override
	public String getName() {
		return "+ask";
	}

	@Override
	public void help(Replyer rep, String message) {
		rep.send("Get the answer to any question using text mining and the Internet");
		rep.send("Use +ask <question> and be patient");
	}

	private class QuestionRunnable implements Runnable {

		private Replyer rep;

		public QuestionRunnable(Replyer rep) {
			this.rep = rep;
		}

		@Override
		public void run() {
			try {
				rep.send("Querying google for relevant documents...");
				Thread.sleep(rand(1000, 3000));

				rep.send("Fetching documents...");
				Thread.sleep(rand(3000, 6000));

				rep.send("Indexing documents...");
				Thread.sleep(rand(7000, 12000));

				rep.send("Performing query boosting...");
				Thread.sleep(rand(500, 1000));

				rep.send("Computing tf/idf...");
				Thread.sleep(rand(15000, 26000));

				rep.send("Extracting most probable answer (be patient)...");

				System.out.println("Type answer now");
				String line = scan.nextLine();
				System.out.println("Type confidence now");
				String conf = scan.nextLine();
				rep.send("Answer is: " + line + " (" + conf + "% confidence)");

			} catch (InterruptedException e) {
				rep.send("Oops");
			}

			busy = false;
		}

		private long rand(int min, int max) {
			return (long) (min + Math.random() * (max - min));
		}

	}
}