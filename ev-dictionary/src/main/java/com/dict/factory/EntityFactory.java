package com.dict.factory;

import com.dict.entity.Definition;
import com.dict.entity.DictionaryEntry;
import com.dict.entity.ExampleSentence;
import com.dict.entity.PartOfSpeech;
import com.dict.entity.Pronunciation;
import com.dict.entity.Synonym;
import com.dict.entity.Word;

public class EntityFactory {
    private static final EntityFactory INSTANCE = new EntityFactory();

    private EntityFactory() {
    }

    public static EntityFactory getInstance() {
        return INSTANCE;
    }

    public Word createWord(String keyword) {
        return new Word(keyword);
    }

    public Pronunciation createPronunciation(String phonetic) {
        return new Pronunciation(phonetic);
    }

    public Definition createDefinition(PartOfSpeech partOfSpeech, String meaning) {
        return new Definition(partOfSpeech, meaning);
    }

    public ExampleSentence createSentence(String english, String vietnamese) {
        return new ExampleSentence(english, vietnamese);
    }

    public Synonym createSynonym(String word) {
        return new Synonym(word);
    }

    public DictionaryEntry createDictionaryEntry(Word word, Pronunciation pronunciation) {
        return new DictionaryEntry(word, pronunciation);
    }
}
