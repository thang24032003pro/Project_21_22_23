package com.dict.entity;

public class Pronunciation {
    private String phonetic;

    public Pronunciation() {
    }

    public Pronunciation(String phonetic) {
        this.phonetic = phonetic;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }
}
