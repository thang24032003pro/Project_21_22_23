package com.dict.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Request {
    private final String action;
    private final String keyword;
    private final String pronunciation;
    private final String meaning;
    private final List<String> params;

    public Request(String action, String keyword, List<String> params) {
        this(action, keyword, "", "", params);
    }

    public Request(String action, String keyword, String pronunciation, String meaning, List<String> params) {
        this.action = action;
        this.keyword = keyword;
        this.pronunciation = pronunciation == null ? "" : pronunciation;
        this.meaning = meaning == null ? "" : meaning;
        this.params = params == null ? new ArrayList<>() : new ArrayList<>(params);
    }

    public String getAction() {
        return action;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public String getMeaning() {
        return meaning;
    }

    public List<String> getParams() {
        return Collections.unmodifiableList(params);
    }
}
