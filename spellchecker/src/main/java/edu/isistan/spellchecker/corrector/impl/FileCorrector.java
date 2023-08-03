package edu.isistan.spellchecker.corrector.impl;

import edu.isistan.spellchecker.corrector.Corrector;

import java.io.*;
import java.util.*;

public class FileCorrector extends Corrector {

	private Map<String, Set<String>> correctionsMap;

	public static class FormatException extends Exception {
		public FormatException(String msg) {
			super(msg);
		}
	}

	public FileCorrector(Reader r) throws IOException, FormatException {
		if (r == null) {
			throw new IllegalArgumentException("El Reader no puede ser null.");
		}
		correctionsMap = new TreeMap<>();

		try (BufferedReader br = new BufferedReader(r)) {
			String line;
			int lineNumber = 1;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (!line.isEmpty()) {
					String[] parts = line.split(",", 2);
					if (parts.length != 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
						throw new FormatException("Error de formato en línea " + lineNumber + ": " + line);
					}
					String wrong = parts[0].trim().toLowerCase();
					String correct = parts[1].trim();
					correctionsMap.computeIfAbsent(wrong, k -> new TreeSet<>()).add(correct);
				}
				lineNumber++;
			}
		}
	}

	public static FileCorrector make(String filename) throws IOException, FormatException {
		try (Reader r = new FileReader(filename)) {
			return new FileCorrector(r);
		}
	}

	public Set<String> getCorrections(String wrong) {
		String wrongLowerCase = wrong.trim().toLowerCase();
		Set<String> corrections = correctionsMap.getOrDefault(wrongLowerCase, Collections.emptySet());
		return matchCase(wrong, corrections);

	}
}
