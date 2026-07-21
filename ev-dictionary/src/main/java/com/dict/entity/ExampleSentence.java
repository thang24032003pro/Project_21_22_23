package com.dict.entity;

public class ExampleSentence {
    private String english;
    private String vietnamese;

    public ExampleSentence() {
    }

    public ExampleSentence(String english, String vietnamese) {
        this.english = english;
        this.vietnamese = vietnamese;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getVietnamese() {
        return vietnamese;
    }

    public void setVietnamese(String vietnamese) {
        this.vietnamese = vietnamese;
    }
}
