package edu.isistan.spellchecker.corrector.impl;

import java.util.Set;
import java.util.TreeSet;

import edu.isistan.spellchecker.corrector.Corrector;
import edu.isistan.spellchecker.corrector.Dictionary;
import edu.isistan.spellchecker.tokenizer.TokenScanner;

/**
 * Este corrector sugiere correciones cuando dos letras adyacentes han sido cambiadas.
 * <p>
 * Un error común es cambiar las letras de orden, e.g.
 * "with" -> "wiht". Este corrector intenta dectectar palabras con exactamente un swap.
 * <p>
 * Por ejemplo, si la palabra mal escrita es "haet", se debe sugerir
 * tanto "heat" como "hate".
 * <p>
 * Solo cambio de letras contiguas se considera como swap.
 */
public class SwapCorrector extends Corrector {

	private Dictionary dictionary;

	/**
	 * Construcye el SwapCorrector usando un Dictionary.
	 *
	 * @param dict 
	 * @throws IllegalArgumentException si el diccionario provisto es null
	 */
	public SwapCorrector(Dictionary dict) {
		if (dict == null) {
			throw new IllegalArgumentException("El diccionario no puede ser null.");
		}
		this.dictionary = dict;
	}

	/**
	 * 
	 * Este corrector sugiere correciones cuando dos letras adyacentes han sido cambiadas.
	 * <p>
	 * Un error común es cambiar las letras de orden, e.g.
	 * "with" -> "wiht". Este corrector intenta dectectar palabras con exactamente un swap.
	 * <p>
	 * Por ejemplo, si la palabra mal escrita es "haet", se debe sugerir
	 * tanto "heat" como "hate".
	 * <p>
	 * Solo cambio de letras contiguas se considera como swap.
	 * <p>
	 * Ver superclase.
	 *
	 * @param wrong 
	 * @return retorna un conjunto (potencialmente vacío) de sugerencias.
	 * @throws IllegalArgumentException si la entrada no es una palabra válida 
	 */
	public Set<String> getCorrections(String wrong)
	{
		if (dictionary.isWord(wrong)) {
			return new TreeSet<>();
		}

		if (!TokenScanner.isWord(wrong)) {
			throw new IllegalArgumentException();
		}

		TreeSet<String> correctionSet = new TreeSet<>();

		//First swap
		String swaped = wrong.charAt(1) + wrong.charAt(0) + wrong.substring(2);
		if(dictionary.isWord(swaped.trim().toLowerCase())) {
			correctionSet.add(swaped);
		}

		//Swap to N-1
		for(int i =0; i<wrong.length()-2; i++) {
			swaped = wrong.substring(0, i) + wrong.charAt(i+1) + wrong.charAt(i) + wrong.substring(i+2);
			if(dictionary.isWord(swaped.trim().toLowerCase())) {
				correctionSet.add(swaped);
			}
		}

		//Last swap
		swaped = wrong.substring(0, wrong.length()-2) + wrong.substring(wrong.length()-1) + wrong.charAt(wrong.length()-2);
		if(dictionary.isWord(swaped.trim().toLowerCase())) {
			correctionSet.add(swaped);
		}

		return matchCase(wrong, correctionSet);
	}
}
