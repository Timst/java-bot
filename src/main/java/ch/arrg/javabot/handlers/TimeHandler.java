package ch.arrg.javabot.handlers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.util.HandlerUtils;

public class TimeHandler implements CommandHandler {

	public static final String TZ_RECORD = "tz";

	@Override
	public void handle(BotContext ctx) {

		String message = ctx.message;
		if ((message = HandlerUtils.withKeyword("time", message)) != null) {

			String[] words = message.split("\\s+");
			String zoneName = words[0];
			if (zoneName.equals("")) {
				String saved = ctx.getUserData(ctx.sender).getRecord(TZ_RECORD);
				if (saved != null) {
					zoneName = saved;
				} else {
					ctx.reply("I don't know about your timezone, honey");
					return;
				}
			}

			String time = getRemoteTime(zoneName);
			ctx.reply("It is " + time + " (" + zoneName + ")");
		}

	}

	private static String getRemoteTime(String zoneName) {
		TimeZone targetTz = TimeZone.getTimeZone(zoneName);
		TimeZone localTz = TimeZone.getDefault();
		Calendar cal = Calendar.getInstance();

		// Offset to local
		cal.setTimeZone(localTz);
		cal.add(Calendar.MILLISECOND, localTz.getRawOffset() * -1);
		if (localTz.inDaylightTime(cal.getTime())) {
			cal.add(Calendar.MILLISECOND, cal.getTimeZone().getDSTSavings() * -1);
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
	public void help(BotContext ctx) {
		ctx.reply("Get timezone aware time");
		ctx.reply("Use +time <timezone> to get the time of a particular timezone.");
		ctx.reply("If your timezone is recorded, you can just use +time.");
	}

}
