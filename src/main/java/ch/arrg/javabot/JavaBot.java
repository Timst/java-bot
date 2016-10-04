package ch.arrg.javabot;

/** Main class
 *
 * @author tgi */
public class JavaBot {
	
	// TODO load/save configuration via commands at runtime
	
	public static void main(String[] args) throws Exception {
		new BotImpl().start();
	}
}
