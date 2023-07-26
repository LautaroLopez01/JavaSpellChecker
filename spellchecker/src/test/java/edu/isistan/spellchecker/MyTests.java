package edu.isistan.spellchecker;
import edu.isistan.spellchecker.corrector.Dictionary;
import edu.isistan.spellchecker.corrector.impl.SwapCorrector;
import edu.isistan.spellchecker.tokenizer.TokenScanner;
import org.junit.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

/** Cree sus propios tests. */
public class MyTests {

    /*
      --------------------- Dictionary Test ---------------------
    */

    //Chequear por una palabra que está en el diccionario.
    @Test
    public void testContainsWord() throws IOException {
        Dictionary d = Dictionary.make("smallDictionary.txt");
        assertTrue("'any' -> should be true ('any' in file)", d.isWord("any"));
    }

    //Chequear por una palabra que NO está en el diccionario.
    @Test
    public void testNotContainsWord() throws IOException{
        Dictionary d = Dictionary.make("smallDictionary.txt");
        assertFalse("'picaporte' -> should be false", d.isWord("picaporte"));
    }

    //Preguntar por el número de palabras en el diccionario.
    @Test
    public void testDictSize() throws IOException {
        Dictionary d = Dictionary.make("smallDictionary.txt");
        assertEquals("32", 32, d.getNumWords());
    }

    //Verificar que el String vacio “” no sea una palabra.
    @Test
    public void testEmptyString() throws IOException{
        Dictionary d = Dictionary.make("smallDictionary.txt");
        assertFalse("'' -> should be false", d.isWord(""));
    }

    //Chequear que la misma palabra con distintas capitalizaciones esté en el diccionario
    @Test
    public void testDistintasCaps() throws IOException{
        Dictionary d = Dictionary.make("smallDictionary.txt");
        assertTrue("'baNANA' -> should be true ('banana' in file)", d.isWord("baNANA"));
        assertTrue("'BanaNa' -> should be true ('banana' in file)", d.isWord("BanaNa"));
        assertTrue("'BANAna' -> should be true ('banana' in file)", d.isWord("BANAna"));
        assertTrue("'bANANA' -> should be true ('banana' in file)", d.isWord("bANANA"));
        assertTrue("'BANANA' -> should be true ('banana' in file)", d.isWord("BANANA"));
    }

    /*
      --------------------- SwapCorrector ---------------------
    */

    //Proveer un diccionario null.
    @Test
    public void testDiccionarioNull() throws IOException {
        try {
            Dictionary dic = null;
            new SwapCorrector(dic);
            fail("Expected IllegalArgumentException - null Dictionary");
        } catch (IllegalArgumentException e){
            //Do nothing - it's supposed to throw this
        }
    }

    //Pedir correcciones para una palabra que está en el diccionario.
    @Test
    public void testPalabraDiccionario() throws IOException {
        Reader reader = new FileReader("smallDictionary.txt");

        try {
            Dictionary dictionary = new Dictionary(new TokenScanner(reader));
            SwapCorrector swapCorrector = new SwapCorrector(dictionary);
            Set<String> out = new TreeSet<>(); // empty because it should not return a correction
            assertEquals(out, swapCorrector.getCorrections("it's"));
        } finally {
            reader.close();
        }
    }

    //Pedir correcciones para una palabra con distintas capitalizaciones.
    @Test
    public void testDistintasCapitalizacionesSwapCorrector() throws IOException {
        Reader reader = new FileReader("smallDictionary.txt");

        try {
            Dictionary dictionary = new Dictionary(new TokenScanner(reader));
            SwapCorrector swapCorrector = new SwapCorrector(dictionary);
            Set<String> out = new TreeSet<>();
            out.add("carrot");
            assertEquals(out, swapCorrector.getCorrections("caroRt"));
        } finally {
            reader.close();
        }
    }
}