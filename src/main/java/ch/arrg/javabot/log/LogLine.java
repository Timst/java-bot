package ch.arrg.javabot.log;

import java.io.Serializable;
import java.util.Date;

public class LogLine implements Serializable {
	public int id;
	public String user;
	public Date date;
	public String kind;
	public String message;
}