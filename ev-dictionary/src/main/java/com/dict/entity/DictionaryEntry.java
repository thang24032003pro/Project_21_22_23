package com.dict.entity;

import java.util.LinkedList;

public class DictionaryEntry {
    private Word word;
    private Pronunciation pronunciation;
    private LinkedList<Definition> definitions;
    private LinkedList<ExampleSentence> exampleSentences;
    private LinkedList<Synonym> synonyms;

    public DictionaryEntry() {
        this.definitions = new LinkedList<>();
        this.exampleSentences = new LinkedList<>();
        this.synonyms = new LinkedList<>();
    }

    public DictionaryEntry(Word word, Pronunciation pronunciation) {
        this();
        this.word = word;
        this.pronunciation = pronunciation;
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public Pronunciation getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(Pronunciation pronunciation) {
        this.pronunciation = pronunciation;
    }

    public LinkedList<Definition> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(LinkedList<Definition> definitions) {
        this.definitions = definitions;
    }

    public LinkedList<ExampleSentence> getExampleSentences() {
        return exampleSentences;
    }

    public void setExampleSentences(LinkedList<ExampleSentence> exampleSentences) {
        this.exampleSentences = exampleSentences;
    }

    public LinkedList<Synonym> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(LinkedList<Synonym> synonyms) {
        this.synonyms = synonyms;
    }
}
