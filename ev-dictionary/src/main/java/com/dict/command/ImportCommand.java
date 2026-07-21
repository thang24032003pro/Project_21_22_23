package com.dict.command;

import com.dict.controller.Request;
import com.dict.service.DictionaryService;

import java.io.PrintStream;
import java.nio.file.Path;

public class ImportCommand implements Command {
    private final DictionaryService service;
    private final PrintStream out;

    public ImportCommand(DictionaryService service, PrintStream out) {
        this.service = service;
        this.out = out;
    }

    @Override
    public void execute(Request request) {
        Path inputPath = request.getKeyword() == null || request.getKeyword().isBlank()
                ? Path.of("import.txt")
                : Path.of(request.getKeyword());
        int imported = service.importFromFile(inputPath);
        out.println("Imported " + imported + " entries!");
    }
}
