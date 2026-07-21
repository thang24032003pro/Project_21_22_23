package com.dict.entity;

import java.util.LinkedList;

public class Definition {
    private PartOfSpeech partOfSpeech;
    private String meaning;
    private LinkedList<ExampleSentence> exampleSentences;

    public Definition() {
        this.exampleSentences = new LinkedList<>();
    }

    public Definition(PartOfSpeech partOfSpeech, String meaning) {
        this();
        this.partOfSpeech = partOfSpeech;
        this.meaning = meaning;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public LinkedList<ExampleSentence> getExampleSentences() {
        return exampleSentences;
    }

    public void setExampleSentences(LinkedList<ExampleSentence> exampleSentences) {
        this.exampleSentences = exampleSentences;
    }
}
