package edu.isistan.spellchecker;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Scanner;
import java.util.Set;

import edu.isistan.spellchecker.corrector.Corrector;
import edu.isistan.spellchecker.corrector.Dictionary;
import edu.isistan.spellchecker.tokenizer.TokenScanner;

public class SpellChecker {
	private Corrector corr;
	private Dictionary dict;

	public SpellChecker(Corrector c, Dictionary d) {
		corr = c;
		dict = d;
	}

	private int getNextInt(int min, int max, Scanner sc) {
		while (true) {
			try {
				int choice = Integer.parseInt(sc.next());
				if (choice >= min && choice <= max) {
					return choice;
				}
			} catch (NumberFormatException ex) {
				// No era un número. Ignorar y solicitar nuevamente.
			}
			System.out.println("Entrada inválida. Inténtelo nuevamente.");
		}
	}

	private String getNextString(Scanner sc) {
		return sc.next();
	}

	public void checkDocument(Reader in, InputStream input, Writer out) throws IOException {
		Scanner sc = new Scanner(input);
		TokenScanner doc = new TokenScanner(in);
		while (doc.hasNext()) {
			String wordToBeRead = doc.next();
			//non-word tokens ouputted verbatim
			if (!TokenScanner.isWord(wordToBeRead)) {
				out.write(wordToBeRead);
				//words in the dictionary ouputted verbatim
			} else if (dict.isWord(wordToBeRead)) {
				out.write(wordToBeRead);
			} else {
				Set<String> correct = corr.getCorrections(wordToBeRead);
				String[] correctionsArray = correct.toArray(new String[correct.size()]);
				System.out.println("The word: " + wordToBeRead + " is not in the dictionary. Please enter the number "
						+ "corresponding with the appropriate action: ");
				System.out.println("0: Ignore and continue");
				System.out.println("1: Replace with another word");
				for (int i = 0; i < correctionsArray.length; i++) {
					System.out.println("Replace with " + correctionsArray[i]);
				}
				int userSelection = getNextInt(0, correctionsArray.length + 1, sc);
				//Output depending on user selection
				if (userSelection == 0) {
					out.write(wordToBeRead);
				} else if (userSelection == 1) {
					out.write(getNextString(sc));
				} else {
					out.write(correctionsArray[userSelection - 2]);
				}
			}
		}
	}

}


