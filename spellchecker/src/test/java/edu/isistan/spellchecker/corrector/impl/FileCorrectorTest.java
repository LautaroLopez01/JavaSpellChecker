package edu.isistan.spellchecker.corrector.impl;
import static org.junit.Assert.*;

import java.util.TreeSet;
import java.util.Set;
import java.io.*;



import org.junit.Test;

import edu.isistan.spellchecker.corrector.Corrector;
import edu.isistan.spellchecker.corrector.impl.FileCorrector;

public class FileCorrectorTest {


  private Set<String> makeSet(String[] strings) {
    Set<String> mySet = new TreeSet<String>();
    for (String s : strings) {
      mySet.add(s);
    }
    return mySet;
  }

  
  
  @Test public void testFileCorrectorNullReader() throws IOException, FileCorrector.FormatException {
    try {
      new FileCorrector(null);
      fail("Expected an IllegalArgumentException - cannot create FileCorrector with null.");
    } catch (IllegalArgumentException f) {    
      //Do nothing. It's supposed to throw an exception
    }
  }

  
  @Test public void testGetCorrection() throws IOException, FileCorrector.FormatException  {
    Corrector c = FileCorrector.make("smallMisspellings.txt");
    assertEquals("lyon -> lion", makeSet(new String[]{"lion"}), c.getCorrections("lyon"));
    TreeSet<String> set2 = new TreeSet<String>();
    assertEquals("TIGGER -> {Trigger,Tiger}", makeSet(new String[]{"Trigger","Tiger"}), c.getCorrections("TIGGER"));
  }


  
  @Test public void testInvalidFormat() throws IOException, FileCorrector.FormatException  {
	 try {
		  Corrector c = new FileCorrector(new StringReader("no comma in this puppy"));
	     fail("This is a bad format");
	 } catch (FileCorrector.FormatException e) {
	    // do nothing
	 }
  }

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

}
