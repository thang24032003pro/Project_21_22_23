package com.dict.storage;

import com.dict.entity.Definition;
import com.dict.entity.DictionaryEntry;
import com.dict.entity.ExampleSentence;
import com.dict.entity.PartOfSpeech;
import com.dict.entity.Pronunciation;
import com.dict.entity.Synonym;
import com.dict.factory.EntityFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

public class FileDictionaryStorage {
    private final Path storageDirectory;

    public FileDictionaryStorage(Path storageDirectory) {
        this.storageDirectory = storageDirectory;
        initialize();
    }

    public void initialize() {
        try {
            Files.createDirectories(storageDirectory);
            Path dictionaryFolder = storageDirectory.resolve("dictionary");
            Files.createDirectories(dictionaryFolder);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to initialize storage", ex);
        }
    }

    public void save(Map<String, DictionaryEntry> entries) {
        try {
            Path dictionaryFolder = storageDirectory.resolve("dictionary");
            Files.createDirectories(dictionaryFolder);
            try (java.util.stream.Stream<Path> stream = Files.list(dictionaryFolder)) {
                for (Path path : stream.toList()) {
                    if (Files.isRegularFile(path) && path.toString().endsWith(".def")) {
                        Files.deleteIfExists(path);
                    }
                }
            }
            for (Map.Entry<String, DictionaryEntry> entry : entries.entrySet()) {
                writeEntry(entry.getValue(), dictionaryFolder.resolve(normalize(entry.getKey()) + ".def"));
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to save dictionary", ex);
        }
    }

    public void saveEntry(DictionaryEntry entry) {
        if (entry == null || entry.getWord() == null || entry.getWord().getKeyword() == null || entry.getWord().getKeyword().isBlank()) {
            return;
        }
        try {
            Path dictionaryFolder = storageDirectory.resolve("dictionary");
            Files.createDirectories(dictionaryFolder);
            writeEntry(entry, dictionaryFolder.resolve(normalize(entry.getWord().getKeyword()) + ".def"));
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to save dictionary entry", ex);
        }
    }

    public void load(Map<String, DictionaryEntry> entries) {
        try {
            Path dictionaryFolder = storageDirectory.resolve("dictionary");
            if (!Files.exists(dictionaryFolder)) {
                return;
            }
            entries.clear();
            try (java.util.stream.Stream<Path> stream = Files.list(dictionaryFolder)) {
                for (Path path : stream.filter(Files::isRegularFile).toList()) {
                    if (path.toString().endsWith(".def")) {
                        DictionaryEntry entry = readEntry(path);
                        if (entry != null) {
                            entries.put(entry.getWord().getKeyword().toLowerCase(), entry);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load dictionary", ex);
        }
    }

    public void deleteEntry(String keyword) {
        try {
            Path dictionaryFolder = storageDirectory.resolve("dictionary");
            Path file = dictionaryFolder.resolve(normalize(keyword) + ".def");
            Files.deleteIfExists(file);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to delete dictionary entry", ex);
        }
    }

    private void writeEntry(DictionaryEntry entry, Path path) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("keyword=").append(entry.getWord().getKeyword()).append(System.lineSeparator());
        builder.append("pronunciation=").append(entry.getPronunciation() != null ? entry.getPronunciation().getPhonetic() : "").append(System.lineSeparator());
        for (Definition definition : entry.getDefinitions()) {
            builder.append("definition=").append(definition.getPartOfSpeech()).append('|').append(definition.getMeaning()).append(System.lineSeparator());
            for (ExampleSentence exampleSentence : definition.getExampleSentences()) {
                builder.append("definition-example=")
                        .append(exampleSentence.getEnglish() == null ? "" : exampleSentence.getEnglish())
                        .append('|')
                        .append(exampleSentence.getVietnamese() == null ? "" : exampleSentence.getVietnamese())
                        .append(System.lineSeparator());
            }
        }
        for (ExampleSentence sentence : entry.getExampleSentences()) {
            builder.append("sentence=").append(sentence.getEnglish()).append('|').append(sentence.getVietnamese()).append(System.lineSeparator());
        }
        for (Synonym synonym : entry.getSynonyms()) {
            builder.append("synonym=").append(synonym.getWord()).append(System.lineSeparator());
        }
        Files.createDirectories(path.getParent());
        Files.writeString(path, builder.toString(), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public DictionaryEntry readEntry(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        EntityFactory factory = EntityFactory.getInstance();
        String keyword = "";
        String pronunciation = "";
        DictionaryEntry entry = null;
        Definition currentDefinition = null;
        for (String line : lines) {
            line = stripBom(line);
            if (line.startsWith("keyword=")) {
                keyword = line.substring("keyword=".length());
                entry = factory.createDictionaryEntry(factory.createWord(keyword), factory.createPronunciation(pronunciation));
            } else if (line.startsWith("pronunciation=")) {
                pronunciation = line.substring("pronunciation=".length());
                if (entry == null) {
                    entry = factory.createDictionaryEntry(factory.createWord(keyword), factory.createPronunciation(pronunciation));
                } else {
                    entry.setPronunciation(factory.createPronunciation(pronunciation));
                }
            } else if (line.startsWith("definition=")) {
                String[] parts = line.substring("definition=".length()).split("\\|", 2);
                if (parts.length == 2) {
                    currentDefinition = factory.createDefinition(PartOfSpeech.fromString(parts[0]), parts[1]);
                    entry.getDefinitions().addLast(currentDefinition);
                }
            } else if (line.startsWith("definition-example=")) {
                String[] parts = line.substring("definition-example=".length()).split("\\|", 2);
                if (parts.length == 2 && currentDefinition != null) {
                    currentDefinition.getExampleSentences().addLast(factory.createSentence(parts[0], parts[1]));
                }
            } else if (line.startsWith("sentence=")) {
                String[] parts = line.substring("sentence=".length()).split("\\|", 2);
                if (parts.length == 2) {
                    entry.getExampleSentences().addLast(factory.createSentence(parts[0], parts[1]));
                }
            } else if (line.startsWith("synonym=")) {
                entry.getSynonyms().addLast(factory.createSynonym(line.substring("synonym=".length())));
            }
        }
        return entry;
    }

    private String stripBom(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return value.charAt(0) == '\uFEFF' ? value.substring(1) : value;
    }

    private String normalize(String keyword) {
        return keyword == null ? "" : keyword.trim().toLowerCase();
    }
}
