import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import ch.arrg.javabot.handlers.quiz.GuessWordHandler;
import ch.arrg.javabot.handlers.quiz.QuizQuestion;

public class MyTest {
	public static void main(String[] args) {
		GuessWordHandler gwh = new GuessWordHandler();
		QuizQuestion question = gwh.getNewQuestion();
		System.out.println(question);
	}
	
	public static void fixEncoding() throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get("data/log.csv"));
		String doubleEncoded = new String(bytes, "utf-8");
		byte[] bytes2 = doubleEncoded.getBytes("iso-8859-1");
		Files.write(Paths.get("data/log-fixed.csv"), bytes2);
	}
	
}
