package ch.arrg.javabot.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import ch.arrg.javabot.log.LogLine;

public class LogLines {

	public static List<LogLine> LOG_LINES;

	static {
		try {
			LOG_LINES = readLog();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static List<LogLine> readLog() throws IOException {
		Reader reader = new InputStreamReader(new FileInputStream("data/log-fixed.csv"), "utf-8");
		try (CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL)) {

			List<LogLine> lines = new ArrayList<>();
			for (CSVRecord rec : parser) {
				LogLine ll = new LogLine();
				ll.id = Integer.parseInt(rec.get(0));
				ll.date = rec.get(3);
				ll.kind = rec.get(5);
				ll.message = rec.get(4);
				ll.user = rec.get(2);

				lines.add(ll);
			}

			System.out.println(lines.size() + " lines read");
			return lines;
		}
	}

}
