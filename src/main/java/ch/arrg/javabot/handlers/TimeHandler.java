package ch.arrg.javabot.handlers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.util.CommandMatcher;
import ch.arrg.javabot.util.Logging;

import com.google.common.base.Joiner;

public class TimeHandler implements CommandHandler {
	
	public static final String TZ_RECORD = "tz";
	
	// TODO this picks up "I ate 9 hats"
	private final static String TIME_REGEX = "(\\d{1,2}) ?[h:.] ?(\\d{2})?\\b";
	private final static Pattern TIME_PAT = Pattern.compile(TIME_REGEX);
	
	// TODO make configurable or use people present in the chat's time zones
	private final static List<String> outputZones = Arrays.asList("Europe/London", "Europe/Oslo", "Australia/Sydney");
	
	@Override
	public void handle(BotContext ctx) {
		String m = ctx.message;
		
		CommandMatcher matcher = CommandMatcher.make("+time");
		if(matcher.matches(m)) {
			handleTimeCommand(ctx, matcher);
		} else {
			String userTz = getUserTz(ctx, "UTC");
			TimeRequest tr = findTimeRequest(userTz, m);
			if(tr != null) {
				handleTimeRequest(ctx, tr);
			}
		}
		
	}
	
	private void handleTimeCommand(BotContext ctx, CommandMatcher matcher) {
		String zoneName = matcher.nextWord();
		
		if(zoneName.equals("")) {
			String saved = getUserTz(ctx, null);
			if(saved != null) {
				zoneName = saved;
			} else {
				ctx.reply("I don't know about your timezone, honey");
				return;
			}
		}
		
		String time = getRemoteTime(zoneName);
		ctx.reply("It is " + time + " (" + zoneName + ")");
	}
	
	private String getUserTz(BotContext ctx, String defaultV) {
		String record = ctx.getRecordStr(TZ_RECORD);
		if(record == null || record.equals("")) {
			return defaultV;
		}
		return record;
	}
	
	private static String getRemoteTime(String zoneName) {
		TimeZone targetTz = TimeZone.getTimeZone(zoneName);
		TimeZone localTz = TimeZone.getDefault();
		Calendar cal = Calendar.getInstance();
		
		// Offset to local
		cal.setTimeZone(localTz);
		cal.add(Calendar.MILLISECOND, localTz.getRawOffset() * -1);
		if(localTz.inDaylightTime(cal.getTime())) {
			cal.add(Calendar.MILLISECOND, cal.getTimeZone().getDSTSavings() * -1);
		}
		
		// Offset to target
		cal.add(Calendar.MILLISECOND, targetTz.getRawOffset());
		if(targetTz.inDaylightTime(cal.getTime())) {
			cal.add(Calendar.MILLISECOND, targetTz.getDSTSavings());
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		String string = sdf.format(cal.getTime());
		return string;
	}
	
	private void handleTimeRequest(BotContext ctx, TimeRequest tr) {
		Date utcDate = getUTCDateFromRequest(tr);
		
		List<String> converted = new ArrayList<>();
		for(String tzName : outputZones) {
			if(tzName.equals(tr.requestTz))
				continue;
			
			TimeZone targetTz = TimeZone.getTimeZone(tzName);
			Calendar targetCal = Calendar.getInstance(targetTz);
			targetCal.setTime(utcDate);
			int hour = targetCal.get(Calendar.HOUR_OF_DAY);
			int minutes = targetCal.get(Calendar.MINUTE);
			
			converted.add(printTime(hour, minutes, tzName));
		}
		
		String body = Joiner.on(", ").join(converted);
		
		String prefix = "(" + printTime(tr.hour, tr.minutes, tr.requestTz) + " = ";
		String postfix = ")";
		ctx.reply(prefix + body + postfix);
	}
	
	private static Date getUTCDateFromRequest(TimeRequest tr) {
		TimeZone userTz = TimeZone.getTimeZone(tr.requestTz);
		Calendar userCal = Calendar.getInstance(userTz);
		userCal.set(Calendar.HOUR_OF_DAY, tr.hour);
		userCal.set(Calendar.MINUTE, tr.minutes);
		Date millis = userCal.getTime();
		return millis;
	}
	
	private static String printTime(int hour, int minutes, String tz) {
		String[] tzParts = tz.split("/");
		String tzLast = tzParts[tzParts.length - 1];
		return String.format("%02d", hour) + ":" + String.format("%02d", minutes) + " in " + tzLast;
	}
	
	private TimeRequest findTimeRequest(String userTz, String msg) {
		Matcher m = TIME_PAT.matcher(msg);
		if(m.find()) {
			TimeRequest tr = new TimeRequest();
			try {
				tr.hour = Integer.parseInt(m.group(1));
				String minutesStr = m.group(2);
				if(minutesStr == null) {
					minutesStr = "00";
				}
				tr.minutes = Integer.parseInt(minutesStr);
				tr.requestTz = userTz;
				return tr;
			} catch (NumberFormatException e) {
				Logging.logException(e);
				return null;
			}
		}
		
		return null;
	}
	
	@Override
	public String getName() {
		return "+time";
	}
	
	@Override
	public void help(BotContext ctx) {
		ctx.reply("Get timezone aware time");
		ctx.reply("Use +time <timezone> to get the time of a particular timezone.");
		ctx.reply("If your timezone is recorded (+record tz <yourTimeZone>), you can just use +time.");
	}
	
	private static class TimeRequest {
		String requestTz;
		int hour;
		int minutes;
	}
}
