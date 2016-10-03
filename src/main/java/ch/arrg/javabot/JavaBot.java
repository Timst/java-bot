package ch.arrg.javabot;

/** Main class
 * 
 * @author tgi */
public class JavaBot {
	
	// TODO readme.md and some documentation
	// TODO configure handlers from file
	// TODO load/save configuration via commands at runtime
	
	public static void main(String[] args) throws Exception {
		new BotImpl().start();
	}
}
