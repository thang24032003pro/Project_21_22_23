package com.dict.service;

import com.dict.entity.Definition;
import com.dict.entity.DictionaryEntry;
import com.dict.entity.ExampleSentence;
import com.dict.entity.PartOfSpeech;
import com.dict.entity.Pronunciation;
import com.dict.entity.Synonym;
import com.dict.factory.EntityFactory;
import com.dict.storage.FileDictionaryStorage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DictionaryService {
    private static DictionaryService instance;
    private static Path storageRoot;
    private final FileDictionaryStorage storage;
    private final Map<String, DictionaryEntry> entries;
    private final EntityFactory entityFactory;

    private DictionaryService(Path storageRoot) {
        this.storage = new FileDictionaryStorage(storageRoot);
        this.entries = new LinkedHashMap<>();
        this.entityFactory = EntityFactory.getInstance();
        this.load();
    }

    public static DictionaryService getInstance(Path storageRoot) {
        if (instance == null || !storageRoot.equals(DictionaryService.storageRoot)) {
            DictionaryService.storageRoot = storageRoot;
            instance = new DictionaryService(storageRoot);
        }
        return instance;
    }

    public static DictionaryService getInstance() {
        return getInstance(Path.of("storage"));
    }

    public DictionaryEntry define(String keyword, String pronunciationText, String meaning, String sentenceText) {
        return define(keyword, pronunciationText, "NOUN", meaning, sentenceText, "", "");
    }

    public DictionaryEntry define(
            String keyword,
            String pronunciationText,
            String posText,
            String meaning,
            String sentenceText,
            String sentenceVietnamese,
            String synonymsText) {
        if (keyword == null || keyword.isBlank()) return null;
        var word = entityFactory.createWord(keyword.trim());
        var pron = pronunciationText == null || pronunciationText.isBlank() ? null : entityFactory.createPronunciation(pronunciationText.trim());
        var entry = entityFactory.createDictionaryEntry(word, pron);
        if (meaning != null && !meaning.isBlank()) {
            String[] meanings = meaning.split(";");
            for (String item : meanings) {
                String trimmed = item == null ? "" : item.trim();
                if (trimmed.isBlank()) {
                    continue;
                }
                var pos = PartOfSpeech.fromString(posText);
                var def = entityFactory.createDefinition(pos, trimmed);
                if (sentenceText != null && !sentenceText.isBlank()) {
                    def.getExampleSentences().addLast(entityFactory.createSentence(sentenceText.trim(), sentenceVietnamese == null ? "" : sentenceVietnamese.trim()));
                }
                entry.getDefinitions().addLast(def);
            }
        }
        if (sentenceText != null && !sentenceText.isBlank()) {
            entry.getExampleSentences().addLast(entityFactory.createSentence(sentenceText.trim(), sentenceVietnamese == null ? "" : sentenceVietnamese.trim()));
        }
        if (synonymsText != null && !synonymsText.isBlank()) {
            for (String synonym : synonymsText.split("[;,\\n]") ) {
                if (synonym != null && !synonym.isBlank()) {
                    entry.getSynonyms().addLast(entityFactory.createSynonym(synonym.trim()));
                }
            }
        }
        return define(entry);
    }

    public String exportAll() {
        return exportAll(null);
    }

    public String exportAll(Path outputPath) {
        Path output = outputPath != null ? outputPath : (storageRoot == null ? Path.of("export.txt") : storageRoot.resolve("export.txt"));
        export(output, null);
        return output.toString();
    }

    public DictionaryEntry lookup(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return entries.get(normalize(keyword));
    }

    public DictionaryEntry define(DictionaryEntry entry) {
        if (entry == null || entry.getWord() == null || entry.getWord().getKeyword() == null || entry.getWord().getKeyword().isBlank()) {
            return null;
        }
        String normalized = normalize(entry.getWord().getKeyword());
        entry.getWord().setKeyword(entry.getWord().getKeyword().trim());
        entries.put(normalized, entry);
        storage.saveEntry(entry);
        return entry;
    }

    public boolean drop(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return false;
        }
        String normalized = normalize(keyword);
        if (!entries.containsKey(normalized)) {
            return false;
        }
        entries.remove(normalized);
        storage.deleteEntry(keyword);
        return true;
    }

    public void export(Path outputPath, Consumer<Double> progressConsumer) {
        try {
            if (outputPath == null) {
                return;
            }
            Path parent = outputPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            List<String> lines = new ArrayList<>();
            List<DictionaryEntry> sortedEntries = sort();
            lines.add("Word | Pronunciation | Meaning | Examples | Synonyms");
            lines.add("-----|-------------|---------|----------|----------");
            for (int i = 0; i < sortedEntries.size(); i++) {
                DictionaryEntry entry = sortedEntries.get(i);
                String pronunciation = entry.getPronunciation() != null && entry.getPronunciation().getPhonetic() != null
                        ? entry.getPronunciation().getPhonetic()
                        : "";
                String meanings = entry.getDefinitions().isEmpty() ? ""
                    : entry.getDefinitions().stream()
                    .map(definition -> definition.getPartOfSpeech() + ": " + definition.getMeaning())
                    .collect(Collectors.toList()).toString();
                String examples = entry.getDefinitions().isEmpty() ? ""
                    : entry.getDefinitions().stream()
                    .flatMap(definition -> definition.getExampleSentences().stream())
                    .map(sentence -> sentence.getEnglish() + " | " + sentence.getVietnamese())
                    .collect(Collectors.toList()).toString();
                if (!entry.getExampleSentences().isEmpty()) {
                    String legacyExamples = entry.getExampleSentences().stream()
                        .map(sentence -> sentence.getEnglish() + " | " + sentence.getVietnamese())
                        .collect(Collectors.toList()).toString();
                    examples = examples.isBlank() ? legacyExamples : examples + " " + legacyExamples;
                }
                String synonyms = entry.getSynonyms().isEmpty() ? ""
                        : entry.getSynonyms().stream().map(Synonym::getWord).toList().toString();
                lines.add(escapeCell(entry.getWord().getKeyword()) + " | "
                        + escapeCell(pronunciation) + " | "
                        + escapeCell(meanings) + " | "
                        + escapeCell(examples) + " | "
                        + escapeCell(synonyms));
                if (progressConsumer != null) {
                    progressConsumer.accept(((double) (i + 1) / Math.max(1, sortedEntries.size())) * 100.0);
                }
            }
            Files.write(outputPath, lines, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to export dictionary", ex);
        }
    }

    public int importFromFile(Path inputPath) {
        if (inputPath == null || !Files.exists(inputPath)) {
            return 0;
        }
        try {
            int imported = 0;
            if (Files.isDirectory(inputPath)) {
                try (java.util.stream.Stream<Path> stream = Files.walk(inputPath)) {
                    for (Path path : stream.filter(Files::isRegularFile)
                            .filter(path -> path.toString().endsWith(".def"))
                            .toList()) {
                        DictionaryEntry entry = storage.readEntry(path);
                        if (entry != null) {
                            define(entry);
                            imported++;
                        }
                    }
                }
            } else if (inputPath.toString().endsWith(".def")) {
                DictionaryEntry entry = storage.readEntry(inputPath);
                if (entry != null) {
                    define(entry);
                    imported = 1;
                }
            } else {
                List<String> lines = Files.readAllLines(inputPath);
                for (String line : lines) {
                    if (line.startsWith("Word:")) {
                        String keyword = line.substring("Word:".length()).trim();
                        DictionaryEntry entry = entityFactory.createDictionaryEntry(entityFactory.createWord(keyword), entityFactory.createPronunciation(""));
                        entry.getDefinitions().addLast(entityFactory.createDefinition(PartOfSpeech.NOUN, ""));
                        define(entry);
                        imported++;
                    }
                }
            }
            return imported;
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to import dictionary", ex);
        }
    }

    public String exportSelected(List<String> keywords) {
        return exportSelected(keywords, null);
    }

    public String exportSelected(List<String> keywords, Path outputPath) {
        if (keywords == null || keywords.isEmpty()) {
            return exportAll(outputPath);
        }
        Path output = outputPath != null ? outputPath : (storageRoot == null ? Path.of("export-selected.txt") : storageRoot.resolve("export-selected.txt"));
        try {
            Path parent = output.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            List<String> lines = new ArrayList<>();
            lines.add("Word | Pronunciation | Meaning | Examples | Synonyms");
            lines.add("-----|-------------|---------|----------|----------");
            List<DictionaryEntry> selected = keywords.stream()
                    .map(this::lookup)
                    .filter(entry -> entry != null)
                    .toList();
            for (DictionaryEntry entry : selected) {
                String pronunciation = entry.getPronunciation() != null && entry.getPronunciation().getPhonetic() != null
                        ? entry.getPronunciation().getPhonetic()
                        : "";
                String meanings = entry.getDefinitions().isEmpty() ? ""
                    : entry.getDefinitions().stream()
                    .map(definition -> definition.getPartOfSpeech() + ": " + definition.getMeaning())
                    .collect(Collectors.toList()).toString();
                String examples = entry.getDefinitions().isEmpty() ? ""
                    : entry.getDefinitions().stream()
                    .flatMap(definition -> definition.getExampleSentences().stream())
                    .map(sentence -> sentence.getEnglish() + " | " + sentence.getVietnamese())
                    .collect(Collectors.toList()).toString();
                if (!entry.getExampleSentences().isEmpty()) {
                    String legacyExamples = entry.getExampleSentences().stream()
                        .map(sentence -> sentence.getEnglish() + " | " + sentence.getVietnamese())
                        .collect(Collectors.toList()).toString();
                    examples = examples.isBlank() ? legacyExamples : examples + " " + legacyExamples;
                }
                String synonyms = entry.getSynonyms().isEmpty() ? ""
                        : entry.getSynonyms().stream().map(Synonym::getWord).toList().toString();
                lines.add(escapeCell(entry.getWord().getKeyword()) + " | "
                        + escapeCell(pronunciation) + " | "
                        + escapeCell(meanings) + " | "
                        + escapeCell(examples) + " | "
                        + escapeCell(synonyms));
            }
            Files.write(output, lines, StandardCharsets.UTF_8);
            return output.toString();
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to export selected entries", ex);
        }
    }

    public List<DictionaryEntry> find(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            return new ArrayList<>();
        }
        String normalized = normalize(prefix);
        List<DictionaryEntry> results = new ArrayList<>();
        for (DictionaryEntry entry : entries.values()) {
            String keyword = entry.getWord().getKeyword().toLowerCase();
            if (keyword.startsWith(normalized)) {
                results.add(entry);
            }
        }
        results.sort(Comparator.comparing(entry -> entry.getWord().getKeyword().toLowerCase()));
        return results;
    }

    public List<DictionaryEntry> sort() {
        List<DictionaryEntry> sorted = new ArrayList<>(entries.values());
        sorted.sort(Comparator.comparing(entry -> entry.getWord().getKeyword().toLowerCase()));
        return sorted;
    }

    public void save() {
        storage.save(entries);
    }

    public void load() {
        storage.load(entries);
    }

    public void clear() {
        entries.clear();
    }

    public List<DictionaryEntry> getEntries() {
        return sort();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private String escapeCell(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\n", " ").replace("\r", " ");
    }
}
