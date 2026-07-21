package com.dict.command;

import com.dict.controller.Request;
import com.dict.service.DictionaryService;

import java.io.PrintStream;

public class DropCommand implements Command {
    private final DictionaryService service;
    private final PrintStream out;

    public DropCommand(DictionaryService service, PrintStream out) {
        this.service = service;
        this.out = out;
    }

    @Override
    public void execute(Request request) {
        boolean removed = service.drop(request.getKeyword());
        if (removed) {
            out.println("@" + request.getKeyword() + " dropped!");
        } else {
            out.println("@" + request.getKeyword() + " is not existed in database!");
        }
    }
}
