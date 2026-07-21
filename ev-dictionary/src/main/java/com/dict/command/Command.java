package com.dict.command;

import com.dict.controller.Request;

public interface Command {
    void execute(Request request);
}
