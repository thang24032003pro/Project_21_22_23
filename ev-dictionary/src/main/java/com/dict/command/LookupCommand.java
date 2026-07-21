package com.dict.command;

import com.dict.controller.Request;
import com.dict.service.DictionaryService;
import com.dict.util.DictionaryDisplayFormatter;

import java.io.PrintStream;

public class LookupCommand implements Command {
    private final DictionaryService service;
    private final PrintStream out;

    public LookupCommand(DictionaryService service, PrintStream out) {
        this.service = service;
        this.out = out;
    }

    @Override
    public void execute(Request request) {
        out.println(DictionaryDisplayFormatter.formatLookup(service.lookup(request.getKeyword()), request.getKeyword()));
    }
}
