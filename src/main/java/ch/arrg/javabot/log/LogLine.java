package ch.arrg.javabot.log;

import java.io.Serializable;

public class LogLine implements Serializable {
	public int id;
	public String user;
	public String date;
	public String kind;
	public String message;
}