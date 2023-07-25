package edu.isistan.spellchecker.corrector.impl;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import edu.isistan.spellchecker.corrector.Corrector;
import edu.isistan.spellchecker.corrector.Dictionary;
import edu.isistan.spellchecker.tokenizer.TokenScanner;

/**
 *
 * Un corrector inteligente que utiliza "edit distance" para generar correcciones.
 *
 * La distancia de Levenshtein es el número minimo de ediciones que se deber
 * realizar a un string para igualarlo a otro. Por edición se entiende:
 * <ul>
 * <li> insertar una letra
 * <li> borrar una letra
 * <li> cambiar una letra
 * </ul>
 *
 * Una "letra" es un caracter a-z (no contar los apostrofes).
 * Intercambiar letras (thsi -> this) <it>no</it> cuenta como una edición.
 * <p>
 * Este corrector sugiere palabras que esten a edit distance uno.
 */
public class Levenshtein extends Corrector {

	private Dictionary dictionary;

	/**
	 * Construye un Levenshtein Corrector usando un Dictionary.
	 * Debe arrojar <code>IllegalArgumentException</code> si el diccionario es null.
	 *
	 * @param dict
	 */
	public Levenshtein(Dictionary dict) {
		if (dict == null) {
			throw new IllegalArgumentException("El diccionario no puede ser null.");
		}
		this.dictionary = dict;
	}

	/**
	 * @param s palabra
	 * @return todas las palabras a erase distance uno
	 */
	Set<String> getDeletions(String s) {
		TreeSet<String> out = new TreeSet<String>();
		String result;
		for (int i = 0; i < s.length(); i++) {
			StringBuilder deletion = new StringBuilder(s);
			result = deletion.deleteCharAt(i).toString();
			if(dictionary.isWord(result.trim().toLowerCase())){
				out.add(result);
			}
		}
		return out;
	}

	/**
	 * @param s palabra
	 * @return todas las palabras a substitution distance uno
	 */
	public Set<String> getSubstitutions(String s) {
		TreeSet<String> out = new TreeSet<String>();
		String result;
		for (int i = 0; i < s.length(); i++) {
			for (char c = 'a'; c <= 'z'; c++) {
				StringBuilder substitution = new StringBuilder(s);
				substitution.setCharAt(i, c);
				result = substitution.toString().trim().toLowerCase();
				if(dictionary.isWord(result) && !result.equals(s)){
					out.add(result);
				}
			}
		}
		return out;
	}


	/**
	 * @param s palabra
	 * @return todas las palabras a insert distance uno
	 */
	public Set<String> getInsertions(String s) {
		TreeSet<String> out = new TreeSet<String>();
		String result;
		for (int i = 0; i <= s.length(); i++) {
			for (char c = 'a'; c <= 'z'; c++) {
				StringBuilder insertion = new StringBuilder(s);
				insertion.insert(i, c);
				result = insertion.toString();
				if(dictionary.isWord(result.trim().toLowerCase())){
					out.add(result);
				}
			}
		}
		return out;
	}

	public Set<String> getCorrections(String wrong) {
		if (dictionary.isWord(wrong)) {
			return new TreeSet<String>();
		}

		if (!TokenScanner.isWord(wrong)) {
			throw new IllegalArgumentException("El argumento no es una palabra");
		}

		TreeSet<String> corrections = new TreeSet<String>();
		corrections.addAll(this.getDeletions(wrong));
		corrections.addAll(this.getSubstitutions(wrong));
		corrections.addAll(this.getInsertions(wrong));

		if (Character.isUpperCase(wrong.toCharArray()[0])){
			corrections = corrections.stream().map(correction -> correction.substring(0,1).toUpperCase() + correction.substring(1).toLowerCase()).collect(Collectors.toCollection(TreeSet::new));
		}

		return corrections;
	}
}
