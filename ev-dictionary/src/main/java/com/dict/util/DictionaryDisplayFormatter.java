package com.dict.util;

import com.dict.entity.Definition;
import com.dict.entity.DictionaryEntry;
import com.dict.entity.ExampleSentence;
import com.dict.entity.PartOfSpeech;
import com.dict.entity.Synonym;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public final class DictionaryDisplayFormatter {
    private DictionaryDisplayFormatter() {
    }

    public static String formatLookup(DictionaryEntry entry, String keyword) {
        if (entry == null) {
            return "@" + normalize(keyword) + " is not existed in database!";
        }

        StringBuilder builder = new StringBuilder();
        String word = entry.getWord() != null ? entry.getWord().getKeyword() : normalize(keyword);
        String pronunciation = entry.getPronunciation() != null ? safeText(entry.getPronunciation().getPhonetic()) : "";

        builder.append("@").append(word);
        if (!pronunciation.isBlank()) {
            builder.append(" ").append(renderPronunciation(pronunciation));
        }
        builder.append(System.lineSeparator());

        Map<PartOfSpeech, List<Definition>> grouped = entry.getDefinitions().stream()
                .collect(Collectors.groupingBy(Definition::getPartOfSpeech, LinkedHashMap::new, Collectors.toList()));

        for (Map.Entry<PartOfSpeech, List<Definition>> group : grouped.entrySet()) {
            builder.append("* ").append(labelFor(group.getKey())).append(System.lineSeparator());
            for (Definition definition : group.getValue()) {
                builder.append("- ").append(safeText(definition.getMeaning())).append(System.lineSeparator());
                for (ExampleSentence exampleSentence : definition.getExampleSentences()) {
                    builder.append("= ").append(safeText(exampleSentence.getEnglish())).append(System.lineSeparator());
                    if (exampleSentence.getVietnamese() != null && !exampleSentence.getVietnamese().isBlank()) {
                        builder.append("+ ").append(safeText(exampleSentence.getVietnamese())).append(System.lineSeparator());
                    }
                }
            }
        }

        if (!entry.getSynonyms().isEmpty()) {
            builder.append("* Tương đồng").append(System.lineSeparator());
            builder.append("- ").append(entry.getSynonyms().stream()
                    .map(Synonym::getWord)
                    .map(DictionaryDisplayFormatter::safeText)
                    .collect(Collectors.joining(", "))).append(System.lineSeparator());
        }

        return builder.toString().stripTrailing();
    }

    private static String labelFor(PartOfSpeech partOfSpeech) {
        if (partOfSpeech == null) {
            return "Từ loại";
        }
        return switch (partOfSpeech) {
            case NOUN -> "Danh từ";
            case VERB -> "Động từ";
            case ADJECTIVE -> "Tính từ";
            case INTERJECTION -> "Thán từ";
            case SYNONYM -> "Tương đồng";
            case PRONUNCIATION -> "Phát âm";
        };
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static String safeText(String value) {
        return value == null ? "" : value;
    }

    private static String renderPronunciation(String value) {
        String trimmed = safeText(value).trim();
        if (trimmed.isBlank()) {
            return "";
        }
        if (trimmed.startsWith("/") && trimmed.endsWith("/")) {
            return trimmed;
        }
        return "/" + trimmed + "/";
    }
}