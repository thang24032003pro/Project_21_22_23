package com.dict.command;

import com.dict.controller.Request;
import com.dict.entity.Definition;
import com.dict.entity.DictionaryEntry;
import com.dict.entity.PartOfSpeech;
import com.dict.entity.Pronunciation;
import com.dict.factory.EntityFactory;
import com.dict.service.DictionaryService;

import java.io.PrintStream;
import java.util.List;
import java.util.Locale;

public class DefineCommand implements Command {
    private final DictionaryService service;
    private final EntityFactory factory;
    private final PrintStream out;

    public DefineCommand(DictionaryService service, PrintStream out) {
        this.service = service;
        this.factory = EntityFactory.getInstance();
        this.out = out;
    }

    @Override
    public void execute(Request request) {
        String keyword = normalize(request.getKeyword());
        DictionaryEntry entry = service.lookup(keyword);
        if (entry == null) {
            entry = factory.createDictionaryEntry(factory.createWord(keyword), factory.createPronunciation(request.getPronunciation()));
        }
        String modeToken = request.getParams().isEmpty() ? "" : request.getParams().get(0);
        if (isPronunciationMode(modeToken)) {
            entry.setPronunciation(factory.createPronunciation(request.getPronunciation()));
        } else if (isSynonymMode(modeToken)) {
            addSynonyms(entry, request.getParams().subList(1, request.getParams().size()));
        } else {
            PartOfSpeech partOfSpeech = resolvePartOfSpeech(modeToken);
            Definition definition = factory.createDefinition(partOfSpeech, request.getMeaning());
            List<String> params = request.getParams();
            if (params.size() > 1) {
                String sentence = params.get(1);
                String sentenceMeaning = params.size() > 2 ? params.get(2) : "";
                if (!isBlank(sentence) || !isBlank(sentenceMeaning)) {
                    definition.getExampleSentences().add(factory.createSentence(sentence, sentenceMeaning));
                }
            }
            entry.getDefinitions().addLast(definition);
        }
        service.define(entry);
        out.println("Saved!");
    }

    private void addSynonyms(DictionaryEntry entry, List<String> params) {
        for (String token : params) {
            for (String synonym : token.split(",")) {
                String trimmed = synonym.trim();
                if (!trimmed.isEmpty()) {
                    entry.getSynonyms().addLast(factory.createSynonym(trimmed));
                }
            }
        }
    }

    private PartOfSpeech resolvePartOfSpeech(String modeToken) {
        String normalized = modeToken == null ? "" : modeToken.trim().toLowerCase(Locale.ROOT);
        if ("--adjective".equals(normalized) || "-a".equals(normalized)) {
            return PartOfSpeech.ADJECTIVE;
        }
        if ("--verb".equals(normalized) || "-v".equals(normalized)) {
            return PartOfSpeech.VERB;
        }
        if ("--noun".equals(normalized) || "-n".equals(normalized)) {
            return PartOfSpeech.NOUN;
        }
        return PartOfSpeech.NOUN;
    }

    private boolean isPronunciationMode(String modeToken) {
        String normalized = modeToken == null ? "" : modeToken.trim().toLowerCase(Locale.ROOT);
        return "--pronoun".equals(normalized) || "-p".equals(normalized);
    }

    private boolean isSynonymMode(String modeToken) {
        String normalized = modeToken == null ? "" : modeToken.trim().toLowerCase(Locale.ROOT);
        return "--synonymous".equals(normalized) || "-s".equals(normalized);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
