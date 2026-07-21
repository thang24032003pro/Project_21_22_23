package com.dict.entity;

public enum PartOfSpeech {
    PRONUNCIATION,
    NOUN,
    VERB,
    ADJECTIVE,
    SYNONYM,
    INTERJECTION;

    public static PartOfSpeech fromString(String value) {
        if (value == null) {
            return NOUN;
        }
        return switch (value.trim().toUpperCase()) {
            case "PRONUNCIATION" -> PRONUNCIATION;
            case "NOUN" -> NOUN;
            case "VERB" -> VERB;
            case "ADJECTIVE" -> ADJECTIVE;
            case "SYNONYM" -> SYNONYM;
            case "INTERJECTION" -> INTERJECTION;
            default -> NOUN;
        };
    }
}
