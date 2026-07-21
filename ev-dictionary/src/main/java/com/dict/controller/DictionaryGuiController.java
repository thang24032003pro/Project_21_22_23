package com.dict.controller;

import com.dict.entity.Definition;
import com.dict.entity.DictionaryEntry;
import com.dict.entity.ExampleSentence;
import com.dict.service.DictionaryService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DictionaryGuiController {

    @FXML private TextField wordFieldUser;
    @FXML private TextField wordFieldAdmin;
    @FXML private TextField pronField;
    @FXML private TextField sentenceField;
    @FXML private TextField sentenceViField;
    @FXML private TextField synonymField;
    @FXML private ComboBox<String> posCombo;
    @FXML private TextArea meaningField;
    @FXML private TextField importPathField;
    @FXML private TextField exportWordsField;
    @FXML private ListView<String> suggestionListUser;
    @FXML private ListView<String> suggestionListAdmin;
    @FXML private TextArea resultAreaUser;
    @FXML private TextArea resultAreaAdmin;
    @FXML private Label statusLabel;

    private final DictionaryService service = DictionaryService.getInstance();

    @FXML
    public void initialize() {
        posCombo.setItems(FXCollections.observableArrayList("NOUN", "VERB", "ADJECTIVE", "INTERJECTION", "SYNONYM", "PRONUNCIATION"));
        posCombo.getSelectionModel().select("NOUN");

        wordFieldUser.textProperty().addListener((obs, oldValue, newValue) -> updateSuggestions(newValue, suggestionListUser));
        wordFieldAdmin.textProperty().addListener((obs, oldValue, newValue) -> updateSuggestions(newValue, suggestionListAdmin));

        suggestionListUser.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                chooseSuggestion(suggestionListUser, wordFieldUser, resultAreaUser);
            }
        });
        suggestionListAdmin.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selected = suggestionListAdmin.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    wordFieldAdmin.setText(selected);
                    onLookupAdmin();
                }
            }
        });
    }

    @FXML
    public void onLookupUser() {
        String word = wordFieldUser.getText().trim();
        lookupWord(word, resultAreaUser);
    }

    @FXML
    public void onLookupAdmin() {
        String word = wordFieldAdmin.getText().trim();
        if (word.isEmpty()) {
            setStatus("Vui lòng nhập từ để tra cứu.");
            resultAreaAdmin.clear();
            return;
        }
        DictionaryEntry e = service.lookup(word);
        if (e == null) {
            var suggestions = service.find(word);
            if (!suggestions.isEmpty()) {
                StringBuilder suggestionText = new StringBuilder("Không tìm thấy từ chính xác: " + word + "\nGợi ý:\n");
                suggestions.forEach(entry -> suggestionText.append(" - ").append(entry.getWord().getKeyword()).append("\n"));
                resultAreaAdmin.setText(suggestionText.toString());
                setStatus("Không có từ chính xác, hiển thị gợi ý.");
            } else {
                resultAreaAdmin.setText("Không tìm thấy từ: " + word);
                setStatus("Không tìm thấy từ.");
            }
            return;
        }
        populateAdminForm(e);
        displayEntry(e, resultAreaAdmin);
        setStatus("Đã tải dữ liệu để sửa: " + e.getWord().getKeyword());
    }

    private void lookupWord(String word, TextArea outputArea) {
        if (word.isEmpty()) {
            setStatus("Vui lòng nhập từ để tra cứu.");
            outputArea.clear();
            return;
        }
        DictionaryEntry e = service.lookup(word);
        if (e == null) {
            var suggestions = service.find(word);
            if (!suggestions.isEmpty()) {
                StringBuilder suggestionText = new StringBuilder("Không tìm thấy từ chính xác: " + word + "\nGợi ý:\n");
                suggestions.forEach(entry -> suggestionText.append(" - ").append(entry.getWord().getKeyword()).append("\n"));
                outputArea.setText(suggestionText.toString());
                setStatus("Không có từ chính xác, hiển thị gợi ý.");
                return;
            }
            outputArea.setText("Không tìm thấy từ: " + word);
            setStatus("Không tìm thấy từ.");
            return;
        }
        displayEntry(e, outputArea);
        setStatus("Tìm thấy từ: " + e.getWord().getKeyword());
    }

    @FXML
    public void onSaveDefine() {
        String word = wordFieldAdmin.getText().trim();
        if (word.isEmpty()) {
            setStatus("Vui lòng nhập từ để lưu.");
            return;
        }
        String pron = pronField.getText().trim();
        String meaning = meaningField.getText().trim();
        String sentence = sentenceField.getText().trim();
        String sentenceVi = sentenceViField.getText().trim();
        String pos = posCombo.getValue();
        String synonyms = synonymField.getText().trim();
        service.define(word, pron, pos, meaning, sentence, sentenceVi, synonyms);
        resultAreaAdmin.setText("Đã lưu từ: " + word);
        setStatus("Lưu thành công: " + word);
        Platform.runLater(this::onLookupAdmin);
    }

    @FXML
    public void onDrop() {
        String word = wordFieldAdmin.getText().trim();
        if (word.isEmpty()) {
            setStatus("Vui lòng nhập từ để xóa.");
            return;
        }
        boolean removed = service.drop(word);
        if (removed) {
            resultAreaAdmin.setText("Đã xóa từ: " + word);
            setStatus("Xóa thành công: " + word);
        } else {
            resultAreaAdmin.setText("Không tìm thấy từ để xóa: " + word);
            setStatus("Xóa thất bại.");
        }
    }

    @FXML
    public void onExport() {
        Path outputPath = promptForSavePath("export.txt");
        if (outputPath == null) {
            setStatus("Đã hủy chọn đường dẫn xuất.");
            return;
        }
        String path = service.exportAll(outputPath);
        resultAreaAdmin.setText("Đã xuất tất cả dữ liệu ra: " + path);
        setStatus("Xuất tất cả thành công.");
    }

    @FXML
    public void onExportSelected() {
        String raw = exportWordsField.getText().trim();
        if (raw.isEmpty()) {
            setStatus("Nhập các từ cần xuất hoặc dùng 'Xuất tất cả'.");
            resultAreaAdmin.setText("Vui lòng nhập danh sách từ cần xuất.");
            return;
        }
        List<String> keywords = Arrays.stream(raw.split("[,;\\n]"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
        if (keywords.isEmpty()) {
            setStatus("Danh sách từ xuất không hợp lệ.");
            resultAreaAdmin.setText("Không có từ hợp lệ để xuất.");
            return;
        }
        Path outputPath = promptForSavePath("export-selected.txt");
        if (outputPath == null) {
            setStatus("Đã hủy chọn đường dẫn xuất.");
            return;
        }
        String path = service.exportSelected(keywords, outputPath);
        resultAreaAdmin.setText("Đã xuất các từ chọn ra: " + path);
        setStatus("Xuất từ đã chọn thành công.");
    }

    @FXML
    public void onImportFromFile() {
        String raw = importPathField.getText().trim();
        if (raw.isEmpty()) {
            setStatus("Nhập đường dẫn file hoặc folder để import.");
            resultAreaAdmin.setText("Vui lòng nhập đường dẫn import.");
            return;
        }
        Path path = Path.of(raw);
        int imported = service.importFromFile(path);
        if (imported > 0) {
            resultAreaAdmin.setText("Đã import " + imported + " mục từ: " + raw);
            setStatus("Import dữ liệu thành công.");
        } else {
            resultAreaAdmin.setText("Không tìm thấy dữ liệu để import từ: " + raw);
            setStatus("Import không thành công.");
        }
    }

    @FXML
    public void onClearAdmin() {
        wordFieldAdmin.clear();
        pronField.clear();
        meaningField.clear();
        sentenceField.clear();
        sentenceViField.clear();
        synonymField.clear();
        resultAreaAdmin.clear();
        posCombo.getSelectionModel().select("NOUN");
        setStatus("Đã làm mới form quản trị.");
    }

    private void populateAdminForm(DictionaryEntry entry) {
        wordFieldAdmin.setText(entry.getWord().getKeyword());
        pronField.setText(entry.getPronunciation() != null ? entry.getPronunciation().getPhonetic() : "");
        if (!entry.getDefinitions().isEmpty()) {
            Definition def = entry.getDefinitions().getFirst();
            meaningField.setText(String.join("; ", entry.getDefinitions().stream().map(Definition::getMeaning).toList()));
            posCombo.setValue(def.getPartOfSpeech().name());
            if (!def.getExampleSentences().isEmpty()) {
                sentenceField.setText(def.getExampleSentences().getFirst().getEnglish());
                sentenceViField.setText(def.getExampleSentences().getFirst().getVietnamese());
            } else {
                sentenceField.clear();
                sentenceViField.clear();
            }
        } else {
            meaningField.clear();
            sentenceField.clear();
            sentenceViField.clear();
            posCombo.setValue("NOUN");
        }
        synonymField.setText(entry.getSynonyms().stream().map(s -> s.getWord()).reduce((a, b) -> a + ", " + b).orElse(""));
    }

    private void displayEntry(DictionaryEntry e, TextArea outputArea) {
        StringBuilder sb = new StringBuilder();
        sb.append("Từ: ").append(e.getWord().getKeyword()).append("\n");
        if (e.getPronunciation() != null && e.getPronunciation().getPhonetic() != null && !e.getPronunciation().getPhonetic().isBlank()) {
            sb.append("Phiên âm: ").append(e.getPronunciation().getPhonetic()).append("\n");
        }
        if (!e.getDefinitions().isEmpty()) {
            sb.append("Định nghĩa:\n");
            for (Definition d : e.getDefinitions()) {
                sb.append(" - [").append(d.getPartOfSpeech()).append("] ").append(d.getMeaning()).append("\n");
                for (var example : d.getExampleSentences()) {
                    sb.append("     Ví dụ: ").append(example.getEnglish()).append("\n");
                    if (example.getVietnamese() != null && !example.getVietnamese().isBlank()) {
                        sb.append("     Nghĩa: ").append(example.getVietnamese()).append("\n");
                    }
                }
            }
        }
        if (!e.getExampleSentences().isEmpty()) {
            sb.append("Câu ví dụ chung:\n");
            for (ExampleSentence s : e.getExampleSentences()) {
                sb.append(" - ").append(s.getEnglish()).append("\n");
                if (s.getVietnamese() != null && !s.getVietnamese().isBlank()) {
                    sb.append("   ").append(s.getVietnamese()).append("\n");
                }
            }
        }
        if (!e.getSynonyms().isEmpty()) {
            sb.append("Đồng nghĩa: ");
            e.getSynonyms().forEach(s -> sb.append(s.getWord()).append(", "));
            sb.setLength(sb.length() - 2);
            sb.append("\n");
        }
        outputArea.setText(sb.toString());
    }

    private void updateSuggestions(String prefix, ListView<String> suggestionsView) {
        if (prefix == null || prefix.isBlank()) {
            suggestionsView.getItems().clear();
            setStatus("Nhập từ để nhận gợi ý.");
            return;
        }
        var entries = service.find(prefix);
        suggestionsView.getItems().setAll(entries.stream().map(entry -> entry.getWord().getKeyword()).toList());
        if (entries.isEmpty()) {
            setStatus("Không có gợi ý phù hợp cho: " + prefix);
        } else {
            setStatus("Gợi ý " + entries.size() + " từ cho: " + prefix);
        }
    }

    private void chooseSuggestion(ListView<String> suggestions, TextField field, TextArea outputArea) {
        String selected = suggestions.getSelectionModel().getSelectedItem();
        if (selected != null) {
            field.setText(selected);
            lookupWord(selected, outputArea);
        }
    }

    private Path promptForSavePath(String defaultFileName) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Chọn nơi lưu file xuất");
        chooser.setInitialFileName(defaultFileName);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));

        Window window = statusLabel != null && statusLabel.getScene() != null ? statusLabel.getScene().getWindow() : null;
        java.io.File file = chooser.showSaveDialog(window);
        return file == null ? null : file.toPath();
    }

    private void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
}
