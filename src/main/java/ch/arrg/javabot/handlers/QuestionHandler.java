package ch.arrg.javabot.handlers;

import java.util.Scanner;
import java.util.concurrent.Executors;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.util.CommandMatcher;
import ch.arrg.javabot.util.Logging;

public class QuestionHandler implements CommandHandler {
	
	private static boolean busy = false;
	private final Scanner scan;
	
	public QuestionHandler() {
		scan = new Scanner(System.in);
	}
	
	@Override
	public void handle(BotContext ctx) {
		
		if(CommandMatcher.make("+ask").matches(ctx.message)) {
			if(busy) {
				ctx.reply("Ask again in a minute, I'm busy");
				return;
			}
			
			busy = true;
			Executors.newFixedThreadPool(1).execute(new QuestionRunnable(ctx));
		}
	}
	
	@Override
	public String getName() {
		return "+ask";
	}
	
	@Override
	public void help(BotContext ctx) {
		ctx.reply("Get the answer to any question using text mining and the Internet");
		ctx.reply("Use +ask <question> and be patient");
	}
	
	private class QuestionRunnable implements Runnable {
		
		private BotContext ctx;
		
		public QuestionRunnable(BotContext ctx) {
			this.ctx = ctx;
		}
		
		@Override
		public void run() {
			try {
				ctx.reply("Querying google for relevant documents...");
				Thread.sleep(rand(1000, 3000));
				
				ctx.reply("Fetching documents...");
				Thread.sleep(rand(3000, 6000));
				
				ctx.reply("Indexing documents...");
				Thread.sleep(rand(7000, 12000));
				
				ctx.reply("Performing query boosting...");
				Thread.sleep(rand(500, 1000));
				
				ctx.reply("Computing tf/idf...");
				Thread.sleep(rand(15000, 26000));
				
				ctx.reply("Extracting most probable answer (be patient)...");
				
				Logging.log("Type answer now");
				String line = scan.nextLine();
				Logging.log("Type confidence now");
				String conf = scan.nextLine();
				ctx.reply("Answer is: " + line + " (" + conf + "% confidence)");
				
			} catch (InterruptedException e) {
				ctx.reply("Oops (Interrupted Exception)");
			}
			
			busy = false;
		}
		
		private long rand(int min, int max) {
			return (long) (min + Math.random() * (max - min));
		}
		
	}
}