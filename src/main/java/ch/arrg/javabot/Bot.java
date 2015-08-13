package ch.arrg.javabot;

import ch.arrg.javabot.data.UserData;

public interface Bot {

	public void sendMsg(String target, String message);

	public UserData getUserData(String user);

	public void quit();
}
