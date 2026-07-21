package com.dict.command;

import com.dict.controller.Request;
import com.dict.service.DictionaryService;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

public class ExportCommand implements Command {
    private final DictionaryService service;
    private final PrintStream out;

    public ExportCommand(DictionaryService service, PrintStream out) {
        this.service = service;
        this.out = out;
    }

    @Override
    public void execute(Request request) {
        Path exportPath = request.getKeyword() == null || request.getKeyword().isBlank()
                ? Path.of("export.txt")
                : Path.of(request.getKeyword());
        AtomicInteger lastPrinted = new AtomicInteger(0);
        out.print("Exporting ");
        service.export(exportPath, progress -> {
            int rounded = (int) (Math.floor(progress / 10.0) * 10);
            if (rounded >= 10 && rounded < 100 && rounded > lastPrinted.get()) {
                if (lastPrinted.get() > 0) {
                    out.print("..");
                }
                out.print(rounded + "%");
                lastPrinted.set(rounded);
            }
        });
        out.println("..Done!");
    }
}
