package edu.isistan.spellchecker.corrector;

import edu.isistan.spellchecker.tokenizer.TokenScanner;

public class Trie {

    public static final int ALPHABET_SIZE = 27; //26 letters + " ´ "
    private TrieNode root = null;

    public Trie(TokenScanner ts){
        if(ts == null){
            throw new IllegalArgumentException();
        }
        this.root = new TrieNode();
        while (ts.hasNext()){
            String word = ts.next();
            if(TokenScanner.isWord(word)){
                this.insert(word.toLowerCase());
            }
        }
        
    }

    private int setIndex(char c){
        if(c == '\''){
            return 26;
        }
        else {
            return c - 'a'; //the ascii code of 'a' is subtracted from the character(c)
        }
    }

    public void insert(String toInsert) {
        TrieNode current = this.root;
        int index;

        for (char c: toInsert.toCharArray()) {
            index = setIndex(c);

            if(current.getChild()[index] == null){
                current.getChild()[index] = new TrieNode();
            }
            current = current.getChild()[index];
        }
        current.setEndOfWord(true); //mark last node as leaf
    }

    public boolean isWord(String word){
        if(word != null){
            TrieNode current = this.root;
            int index;

            for (char c: word.toCharArray()) {
                index = setIndex(c);

                if(current.getChild()[index] == null){
                    return false;
                }
                current = current.getChild()[index];
            }
            return current.getEndOfWord();
        }
        return false;
    }
}
