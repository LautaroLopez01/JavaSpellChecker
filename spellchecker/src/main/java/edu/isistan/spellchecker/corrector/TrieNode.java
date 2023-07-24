package edu.isistan.spellchecker.corrector;

import java.util.Arrays;

import static edu.isistan.spellchecker.corrector.Trie.ALPHABET_SIZE;

public class TrieNode {
    private TrieNode[] child = new TrieNode[ALPHABET_SIZE];
    private boolean endOfWord; //true si es una palabra final

    public TrieNode(){
        this.endOfWord = false;
        Arrays.fill(this.child, null);
    }

    public TrieNode[] getChild(){
        return this.child;
    }

    public void setEndOfWord(boolean b){
        this.endOfWord = b;
    }

    public boolean getEndOfWord(){
        return this.endOfWord;
    }
}
