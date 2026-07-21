package com.dict.service;

import com.dict.entity.Definition;
import com.dict.entity.DictionaryEntry;
import com.dict.entity.PartOfSpeech;
import com.dict.entity.Pronunciation;
import com.dict.entity.Word;
import com.dict.factory.EntityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DictionaryServiceTest {

    private DictionaryService service;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        service = DictionaryService.getInstance(tempDir);
        service.clear();
    }

    @Test
    void lookupReturnsEntryWhenPresent() {
        DictionaryEntry entry = createEntry("positive", "pɒzətɪv", PartOfSpeech.ADJECTIVE, "tích cực");
        service.define(entry);

        DictionaryEntry result = service.lookup("positive");

        assertNotNull(result);
        assertEquals("positive", result.getWord().getKeyword());
    }

    @Test
    void lookupReturnsNullWhenMissing() {
        assertNull(service.lookup("missing"));
    }

    @Test
    void defineCreatesEntryWhenAbsent() {
        DictionaryEntry entry = createEntry("apple", "ˈæpəl", PartOfSpeech.NOUN, "quả táo");
        DictionaryEntry saved = service.define(entry);

        assertNotNull(saved);
        assertEquals("apple", saved.getWord().getKeyword());
        assertTrue(service.lookup("apple") != null);
    }

    @Test
    void defineUpdatesExistingEntry() {
        service.define(createEntry("hello", "həˈləʊ", PartOfSpeech.INTERJECTION, "xin chào"));
        DictionaryEntry updated = createEntry("hello", "həˈləʊ", PartOfSpeech.INTERJECTION, "chào");

        DictionaryEntry result = service.define(updated);

        assertEquals("chào", result.getDefinitions().getFirst().getMeaning());
    }

    @Test
    void dropRemovesEntryAndDeletesFile() {
        service.define(createEntry("house", "haʊs", PartOfSpeech.NOUN, "ngôi nhà"));

        boolean removed = service.drop("house");
        Path file = tempDir.resolve("dictionary").resolve("house.def");

        assertTrue(removed);
        assertFalse(Files.exists(file));
    }

    @Test
    void savePersistsEntryToDisk() {
        service.define(createEntry("dog", "dɔg", PartOfSpeech.NOUN, "con chó"));

        service.save();
        Path file = tempDir.resolve("dictionary").resolve("dog.def");

        assertTrue(Files.exists(file));
    }

    @Test
    void loadRestoresEntriesFromDisk() {
        service.define(createEntry("computer", "kəmˈpjuːtər", PartOfSpeech.NOUN, "máy tính"));
        service.save();

        DictionaryService freshService = DictionaryService.getInstance(tempDir);
        freshService.clear();
        freshService.load();

        assertNotNull(freshService.lookup("computer"));
    }

    @Test
    void exportWritesReadableFile() throws Exception {
        service.define(createEntry("positive", "pɒzətɪv", PartOfSpeech.ADJECTIVE, "tích cực"));
        Path exportFile = tempDir.resolve("export.txt");

        service.export(exportFile, progress -> { });

        assertTrue(Files.exists(exportFile));
        assertTrue(Files.readString(exportFile).contains("positive"));
    }

    @Test
    void exportWritesFullTableWithPronunciationAndMeaning() throws Exception {
        DictionaryEntry entry = createEntry("amazing", "/əˈmeɪzɪŋ/", PartOfSpeech.ADJECTIVE, "tuyệt vời");
        entry.getDefinitions().addLast(createDefinition(PartOfSpeech.ADJECTIVE, "đáng kinh ngạc"));
        entry.getExampleSentences().addLast(EntityFactory.getInstance().createSentence("This is amazing", "Đây thật tuyệt vời"));
        entry.getSynonyms().addLast(createSynonym("wonderful"));
        service.define(entry);
        Path exportFile = tempDir.resolve("full-export.txt");

        service.export(exportFile, progress -> { });

        String content = Files.readString(exportFile);
        assertTrue(content.contains("Word"));
        assertTrue(content.contains("Pronunciation"));
        assertTrue(content.contains("Meaning"));
        assertTrue(content.contains("amazing"));
        assertTrue(content.contains("tuyệt vời"));
        assertTrue(content.contains("wonderful"));
    }

    @Test
    void importLoadsEntriesFromTextFile() throws Exception {
        Path importFile = tempDir.resolve("import.txt");
        Files.writeString(importFile, "Word: apple\nPronunciation: /ˈæpəl/\nDefinition: NOUN|quả táo\nSentence: an apple|một quả táo\nSynonym: fruit\n---\n");

        int imported = service.importFromFile(importFile);

        assertEquals(1, imported);
        assertNotNull(service.lookup("apple"));
    }

    @Test
    void importLoadsEntriesFromNestedFolderAndIgnoresUnrelatedFiles() throws Exception {
        Path importFolder = tempDir.resolve("nested-import");
        Path nestedFolder = importFolder.resolve("nested");
        Files.createDirectories(nestedFolder);
        Files.writeString(importFolder.resolve("notes.txt"), "ignore me");
        Files.writeString(nestedFolder.resolve("apple.def"), "keyword=apple\npronunciation=/ˈæpəl/\ndefinition=NOUN|quả táo\n");
        Files.writeString(nestedFolder.resolve("readme.md"), "ignore me");

        int imported = service.importFromFile(importFolder);

        assertEquals(1, imported);
        assertNotNull(service.lookup("apple"));
    }

    @Test
    void findReturnsMatchingEntriesByPrefix() {
        service.define(createEntry("possible", "ˈpɒsəbl", PartOfSpeech.ADJECTIVE, "khả thi"));
        service.define(createEntry("policy", "ˈpɒləsi", PartOfSpeech.NOUN, "chính sách"));
        service.define(createEntry("position", "pəˈzɪʃən", PartOfSpeech.NOUN, "vị trí"));

        List<DictionaryEntry> results = service.find("po");

        assertEquals(3, results.size());
    }

    @Test
    void sortReturnsAlphabeticalOrder() {
        service.define(createEntry("zebra", "ˈziːbrə", PartOfSpeech.NOUN, "ngựa vằn"));
        service.define(createEntry("apple", "ˈæpəl", PartOfSpeech.NOUN, "quả táo"));
        service.define(createEntry("mango", "ˈmæŋɡəʊ", PartOfSpeech.NOUN, "quả xoài"));

        List<DictionaryEntry> sorted = service.sort();

        assertEquals("apple", sorted.get(0).getWord().getKeyword());
        assertEquals("mango", sorted.get(1).getWord().getKeyword());
        assertEquals("zebra", sorted.get(2).getWord().getKeyword());
    }

    @Test
    void clearRemovesAllEntries() {
        service.define(createEntry("book", "bʊk", PartOfSpeech.NOUN, "cuốn sách"));

        service.clear();

        assertTrue(service.getEntries().isEmpty());
    }

    @Test
    void getEntriesReturnsSnapshot() {
        service.define(createEntry("cat", "kæt", PartOfSpeech.NOUN, "con mèo"));

        assertEquals(1, service.getEntries().size());
    }

    @Test
    void saveAndLoadPreserveDefinitionsAndSynonyms() {
        DictionaryEntry entry = createEntry("positive", "pɒzətɪv", PartOfSpeech.ADJECTIVE, "tích cực");
        entry.getDefinitions().addLast(createDefinition(PartOfSpeech.ADJECTIVE, "tích cực"));
        entry.getSynonyms().addLast(createSynonym("sure"));
        service.define(entry);

        service.save();
        DictionaryService freshService = DictionaryService.getInstance(tempDir);
        freshService.clear();
        freshService.load();

        DictionaryEntry loaded = freshService.lookup("positive");
        assertTrue(loaded.getDefinitions().size() > 0);
        assertTrue(loaded.getSynonyms().size() > 0);
    }

    @Test
    void defineWithPronunciationCreatesPronunciationObject() {
        DictionaryEntry entry = service.define(createEntry("house", "haʊs", PartOfSpeech.NOUN, "ngôi nhà"));
        assertNotNull(entry.getPronunciation());
        assertEquals("haʊs", entry.getPronunciation().getPhonetic());
    }

    @Test
    void lookupWithDifferentCaseIsCaseInsensitive() {
        service.define(createEntry("Hello", "həˈləʊ", PartOfSpeech.INTERJECTION, "xin chào"));

        DictionaryEntry result = service.lookup("hello");
        assertNotNull(result);
    }

    @Test
    void dropReturnsFalseWhenWordMissing() {
        assertFalse(service.drop("unknown"));
    }

    @Test
    void importFromFileReturnsZeroWhenFileMissing() throws Exception {
        assertEquals(0, service.importFromFile(tempDir.resolve("missing.txt")));
    }

    @Test
    void exportCreatesDirectoryWhenNeeded() throws Exception {
        Path output = tempDir.resolve("nested").resolve("export.txt");
        service.export(output, progress -> { });
        assertTrue(Files.exists(output));
    }

    @Test
    void sortHandlesEmptyDictionary() {
        assertTrue(service.sort().isEmpty());
    }

    @Test
    void findHandlesEmptyPrefix() {
        List<DictionaryEntry> results = service.find("");
        assertTrue(results.isEmpty());
    }

    @Test
    void getEntriesReturnsSortedSnapshot() {
        service.define(createEntry("beta", "beɪtə", PartOfSpeech.NOUN, "beta"));
        service.define(createEntry("alpha", "ælfə", PartOfSpeech.NOUN, "alpha"));

        List<DictionaryEntry> entries = service.getEntries();
        assertEquals("alpha", entries.get(0).getWord().getKeyword());
    }

    @Test
    void defineKeepsLinkedListDefinitions() {
        DictionaryEntry entry = createEntry("example", "ɪɡˈzɑːmpl", PartOfSpeech.NOUN, "ví dụ");
        service.define(entry);

        assertTrue(entry.getDefinitions() instanceof LinkedList<?>);
    }

    @Test
    void defineKeepsLinkedListSynonyms() {
        DictionaryEntry entry = createEntry("example", "ɪɡˈzɑːmpl", PartOfSpeech.NOUN, "ví dụ");
        service.define(entry);

        assertTrue(entry.getSynonyms() instanceof LinkedList<?>);
    }

    @Test
    void serviceUsesSingletonInstance() {
        DictionaryService another = DictionaryService.getInstance(tempDir);
        assertSame(service, another);
    }

    private DictionaryEntry createEntry(String keyword, String phonetic, PartOfSpeech partOfSpeech, String meaning) {
        EntityFactory factory = EntityFactory.getInstance();
        Word word = factory.createWord(keyword);
        Pronunciation pronunciation = factory.createPronunciation(phonetic);
        Definition definition = factory.createDefinition(partOfSpeech, meaning);
        DictionaryEntry entry = factory.createDictionaryEntry(word, pronunciation);
        entry.getDefinitions().addLast(definition);
        return entry;
    }

    private Definition createDefinition(PartOfSpeech partOfSpeech, String meaning) {
        return EntityFactory.getInstance().createDefinition(partOfSpeech, meaning);
    }

    private com.dict.entity.Synonym createSynonym(String word) {
        return EntityFactory.getInstance().createSynonym(word);
    }
}
