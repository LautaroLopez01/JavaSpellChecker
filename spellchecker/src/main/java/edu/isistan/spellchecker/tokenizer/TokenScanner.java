package edu.isistan.spellchecker.tokenizer;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Dado un archivo provee un método para recorrerlo.
 */
public class TokenScanner implements Iterator<String> {
    private Reader reader;
    private int currentChar;

    /**
     * Crea un TokenScanner.
     * <p>
     * Como un iterador, el TokenScanner solo debe leer lo justo y
     * necesario para implementar los métodos next() y hasNext().
     * No se debe leer toda la entrada de una.
     * <p>
     *
     * @param in fuente de entrada
     * @throws IOException              si hay algún error leyendo.
     * @throws IllegalArgumentException si el Reader provisto es null
     */
    public TokenScanner(Reader in) throws IOException {
        if (in == null) {
            throw new IllegalArgumentException("El Reader provisto es null.");
        }
        reader = in;
        currentChar = reader.read();
    }

    /**
     * Determina si un carácer es una caracter válido para una palabra.
     * <p>
     * Un caracter válido es una letra (
     * Character.isLetter) o una apostrofe '\''.
     *
     * @param c
     * @return true si es un caracter
     */
    public static boolean isWordCharacter(int c) {
        return Character.isLetter(c) || c == '\'';
    }

    /**
     * Determina si un string es una palabra válida.
     * Null no es una palabra válida.
     * Un string que todos sus caracteres son válidos es una
     * palabra. Por lo tanto, el string vacío es una palabra válida.
     *
     * @param s
     * @return true si el string es una palabra.
     */
    public static boolean isWord(String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }
        for (char c : s.toCharArray()) {
            if (!isWordCharacter(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determina si hay otro token en el reader.
     */
    public boolean hasNext() {
        return currentChar != -1;
    }

    /**
     * Retorna el siguiente token.
     *
     * @throws NoSuchElementException cuando se alcanzó el final de stream
     */
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No hay más tokens disponibles.");
        }

        StringBuilder tokenBuilder = new StringBuilder();

        try {
            if (isWordCharacter(currentChar) && hasNext()) {
                while ((isWordCharacter(currentChar) && hasNext())) {
                    tokenBuilder.append((char) currentChar);
                    currentChar = reader.read();
                }
            } else {
                while (!isWordCharacter(currentChar) && hasNext()) { // Se descarta token no palabra
                    tokenBuilder.append((char) currentChar);
                    currentChar = reader.read();
                }
            }

            return tokenBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el siguiente token.", e);
        }
    }
}
