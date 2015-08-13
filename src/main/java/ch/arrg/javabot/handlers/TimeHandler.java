package ch.arrg.javabot.handlers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import ch.arrg.javabot.Bot;
import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.util.HandlerUtils;
import ch.arrg.javabot.util.Replyer;

public class TimeHandler implements CommandHandler {

	public static final String TZ_RECORD = "tz";

	@Override
	public void handle(Bot bot, String channel, String sender, String login,
			String hostname, String message) {

		Replyer rep = HandlerUtils.makeReplyer(bot, channel);

		if ((message = HandlerUtils.withKeyword("time", message)) != null) {

			String[] words = message.split("\\s+");
			String zoneName = words[0];
			if (zoneName.equals("")) {
				String saved = bot.getUserData(sender).getRecord(TZ_RECORD);
				if (saved != null) {
					zoneName = saved;
				} else {
					rep.send("I don't know about your timezone, honey");
					return;
				}
			}

			String time = getRemoteTime(zoneName);
			rep.send("It is " + time + " (" + zoneName + ")");
		}

	}

	private String getRemoteTime(String zoneName) {
		TimeZone targetTz = TimeZone.getTimeZone(zoneName);
		TimeZone localTz = TimeZone.getDefault();
		Calendar cal = Calendar.getInstance();

		// Offset to local
		cal.setTimeZone(localTz);
		cal.add(Calendar.MILLISECOND, localTz.getRawOffset() * -1);
		if (localTz.inDaylightTime(cal.getTime())) {
			cal.add(Calendar.MILLISECOND, cal.getTimeZone().getDSTSavings()
					* -1);
		}

		// Offset to target
		cal.add(Calendar.MILLISECOND, targetTz.getRawOffset());
		if (targetTz.inDaylightTime(cal.getTime())) {
			cal.add(Calendar.MILLISECOND, targetTz.getDSTSavings());
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		String string = sdf.format(cal.getTime());
		return string;
	}

	@Override
	public String getName() {
		return "+time";
	}

	@Override
	public void help(Replyer rep, String message) {
		rep.send("Get timezone aware time");
		rep.send("Use +time <timezone> to get the time of a particular timezone.");
		rep.send("If your timezone is recorded, you can just use +time.");
	}

}
