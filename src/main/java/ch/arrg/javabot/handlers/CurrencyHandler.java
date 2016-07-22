package ch.arrg.javabot.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.arrg.javabot.CommandHandler;
import ch.arrg.javabot.data.BotContext;
import ch.arrg.javabot.util.HtmlReaderHelper;

public class CurrencyHandler implements CommandHandler {
	
	private final static String NUM_REGEX = "(\\d+([.,]\\d+)?)";
	private final static String[] CURRENCIES = { "GBP", "USD", "AUD", "NOK", "CHF", "£", "$" };
	
	private static List<Pattern> PREFIX = new ArrayList<>();
	private static List<Pattern> SUFFIX = new ArrayList<>();
	
	static {
		makePatterns();
	}
	
	private static void makePatterns() {
		for(String curr : CURRENCIES) {
			String currWithParen = "(" + Pattern.quote(curr) + ")";
			String regexPrefix = currWithParen + " ?" + NUM_REGEX;
			String regexSuffix = NUM_REGEX + " ?" + currWithParen;
			
			PREFIX.add(Pattern.compile(regexPrefix));
			SUFFIX.add(Pattern.compile(regexSuffix));
		}
	}
	
	@Override
	public void handle(BotContext ctx) {
		String m = ctx.message;
		
		match(ctx, PREFIX, m, true);
		match(ctx, SUFFIX, m, false);
	}
	
	private void match(BotContext ctx, List<Pattern> pats, String msg, boolean isPrefix) {
		for(Pattern p : pats) {
			Matcher matcher = p.matcher(msg.toUpperCase());
			if(matcher.find()) {
				if(isPrefix) {
					onMatch(ctx, matcher.group(1), matcher.group(2));
				} else {
					onMatch(ctx, matcher.group(3), matcher.group(1));
				}
			}
		}
	}
	
	private void onMatch(BotContext ctx, String currencyS, String amountS) {
		try {
			String currency = normalizeCurrency(currencyS);
			double amount = Double.parseDouble(amountS);
			Double rate = getRate(currency);
			if(rate != null) {
				double amountEuro = amount / rate;
				String amountEuroS = String.format("%.2f", amountEuro);
				ctx.reply("(" + amountS + " " + currencyS + " = " + amountEuroS + " EUR)");
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	private Double getRate(String currency) {
		String req = "http://api.fixer.io/latest?symbols=" + currency;
		// {"base":"EUR","date":"2016-07-21","rates":{"NOK":9.3541}}
		
		Pattern pat = Pattern.compile("\\{\"" + currency + "\":(\\d+(\\.\\d+)?)\\}");
		
		try (BufferedReader in = HtmlReaderHelper.openUrlForRead(req)) {
			String inputLine;
			
			while((inputLine = in.readLine()) != null) {
				Matcher matcher = pat.matcher(inputLine);
				if(matcher.find()) {
					return Double.parseDouble(matcher.group(1));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private String normalizeCurrency(String currency) {
		if(currency.equals("$"))
			return "USD";
		if(currency.equals("£"))
			return "GBP";
		return currency;
	}
	
	@Override
	public String getName() {
		return "currency";
	}
	
	@Override
	public void help(BotContext ctx) {
		ctx.reply("Automatically converts currencies to €.");
	}
	
}
