package edu.isistan.spellchecker;
import edu.isistan.spellchecker.corrector.Corrector;
import edu.isistan.spellchecker.corrector.Dictionary;
import edu.isistan.spellchecker.corrector.impl.FileCorrector;
import edu.isistan.spellchecker.corrector.impl.SwapCorrector;
import edu.isistan.spellchecker.tokenizer.TokenScanner;
import org.junit.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.NoSuchElementException;
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

    // Token Scanner tests
    @Test
    public void testEmptyInput() throws IOException {
        String input = "";
        TokenScanner scanner = new TokenScanner(new StringReader(input));
        assertFalse(scanner.hasNext());
    }

    @Test
    public void testSingleWordToken() throws IOException {
        String input = "hello";
        TokenScanner scanner = new TokenScanner(new StringReader(input));
        assertTrue(scanner.hasNext());
        assertEquals("hello", scanner.next());
        assertFalse(scanner.hasNext());
    }

    @Test
    public void testSingleNonWordToken() throws IOException {
        String input = "123";
        TokenScanner scanner = new TokenScanner(new StringReader(input));
        assertTrue(scanner.hasNext());
        assertEquals("123", scanner.next());
        assertFalse(scanner.hasNext());
    }

    @Test
    public void testMixedTokensEndingWithWord() throws IOException {
        String input = "Hello, world!";
        TokenScanner scanner = new TokenScanner(new StringReader(input));
        assertTrue(scanner.hasNext());
        assertEquals("Hello", scanner.next());
        assertTrue(scanner.hasNext());
        assertEquals(",", scanner.next());
        assertTrue(scanner.hasNext());
        assertEquals("world", scanner.next());
        assertTrue(scanner.hasNext());
        assertEquals("!", scanner.next());
        assertFalse(scanner.hasNext());
    }

    @Test
    public void testMixedTokensEndingWithNonWord() throws IOException {
        String input = "Hello, world!!!";
        TokenScanner scanner = new TokenScanner(new StringReader(input));
        assertTrue(scanner.hasNext());
        assertEquals("Hello", scanner.next());
        assertTrue(scanner.hasNext());
        assertEquals(",", scanner.next());
        assertTrue(scanner.hasNext());
        assertEquals("world", scanner.next());
        assertTrue(scanner.hasNext());
        assertEquals("!!!", scanner.next());
        assertFalse(scanner.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testNoSuchElementException() throws IOException {
        String input = "Hello,";
        TokenScanner scanner = new TokenScanner(new StringReader(input));
        while (scanner.hasNext()) {
            scanner.next();
        }
        // The next call to scanner.next() should throw NoSuchElementException
        scanner.next();
    }

    // Tests File Corrector

    @Test
    public void testFileWithExtraSpaces() throws IOException, FileCorrector.FormatException {
        String inputFileContent = "  palabra1   ,   correccion1  \n" +
                "palabra2,correccion2  \n" +
                "  palabra3  ,   correccion3   ";
        FileCorrector corrector = new FileCorrector(new StringReader(inputFileContent));

        Set<String> corrections1 = corrector.getCorrections("palabra1");
        assertEquals(1, corrections1.size());
        assertTrue(corrections1.contains("correccion1"));

        Set<String> corrections2 = corrector.getCorrections("palabra2");
        assertEquals(1, corrections2.size());
        assertTrue(corrections2.contains("correccion2"));

        Set<String> corrections3 = corrector.getCorrections("palabra3");
        assertEquals(1, corrections3.size());
        assertTrue(corrections3.contains("correccion3"));
    }

    @Test
    public void testWordWithoutCorrections() throws IOException, FileCorrector.FormatException {
        String inputFileContent = "palabra,correccion";
        FileCorrector corrector = new FileCorrector(new StringReader(inputFileContent));

        Set<String> corrections = corrector.getCorrections("inexistente");
        assertTrue(corrections.isEmpty());
    }

    @Test
    public void testWordWithMultipleCorrections() throws IOException, FileCorrector.FormatException {
        String inputFileContent =
                "palabra,correccion1\n" +
                        "palabra,correccion2\n" +
                        "palabra,correccion3";
        FileCorrector corrector = new FileCorrector(new StringReader(inputFileContent));

        Set<String> corrections = corrector.getCorrections("palabra");

        assertEquals(3, corrections.size());
        assertTrue(corrections.contains("correccion1"));
        assertTrue(corrections.contains("correccion2"));
        assertTrue(corrections.contains("correccion3"));
    }

    @Test
    public void testDistintasCapitalizacionesFileCorrector() throws IOException,FileCorrector.FormatException {
        Corrector c = FileCorrector.make("misspellings.txt");
        assertEquals("surPRiZe -> {surprise}", makeSet(new String[]{"surprise"}), c.getCorrections("surPRiZe"));
        assertEquals("SUrPRiZe -> {Surprise}", makeSet(new String[]{"Surprise"}), c.getCorrections("SUrPRiZe"));
        assertEquals("surPRiZe -> {surprise}", makeSet(new String[]{"surprise"}), c.getCorrections("surPRiZe"));
    }

    // Metodo auxiliar para comparaciones
    private Set<String> makeSet(String[] strings) {
        Set<String> mySet = new TreeSet<String>();
        for (String s : strings) {
            mySet.add(s);
        }
        return mySet;
    }
}