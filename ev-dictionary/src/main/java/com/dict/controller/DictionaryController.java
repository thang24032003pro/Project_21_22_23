package com.dict.controller;

import com.dict.command.Command;
import com.dict.command.DefineCommand;
import com.dict.command.DropCommand;
import com.dict.command.ExportCommand;
import com.dict.command.ImportCommand;
import com.dict.command.LookupCommand;
import com.dict.service.DictionaryService;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DictionaryController {
    private enum DefineMode {
        PRONUNCIATION,
        NOUN,
        ADJECTIVE,
        VERB,
        SYNONYM
    }

    private static final Pattern TOKEN_PATTERN = Pattern.compile("\"([^\"]*)\"|'([^']*)'|\\S+");

    private final DictionaryService service;
    private final Scanner scanner;
    private final PrintStream out;
    private final Map<String, Command> commands;
    private final Map<String, Function<List<String>, Request>> requestBuilders;
    private boolean running = true;

    public DictionaryController() {
        this(DictionaryService.getInstance(Path.of("storage")), new Scanner(System.in), System.out);
    }

    public DictionaryController(Path storageRoot, Scanner scanner, PrintStream out) {
        this(DictionaryService.getInstance(storageRoot), scanner, out);
    }

    public DictionaryController(DictionaryService service, Scanner scanner, PrintStream out) {
        this.service = service;
        this.scanner = scanner;
        this.out = out;
        this.commands = new LinkedHashMap<>();
        this.requestBuilders = new LinkedHashMap<>();
        initializeCommands();
    }

    public void run() {
        printBanner();
        printHelp();
        while (running) {
            out.print("> ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String line = scanner.nextLine().trim();
            if (line.isBlank()) {
                continue;
            }

            List<String> tokens = tokenize(line);
            if (tokens.isEmpty()) {
                continue;
            }

            String action = tokens.get(0).toLowerCase(Locale.ROOT);
            Function<List<String>, Request> requestBuilder = requestBuilders.get(action);
            if (requestBuilder == null) {
                out.println("Unknown action: " + action);
                continue;
            }

            Request request = requestBuilder.apply(tokens.subList(1, tokens.size()));
            if (request == null) {
                out.println("Invalid syntax for: " + action);
                continue;
            }

            Command command = commands.get(action);
            if (command != null) {
                command.execute(request);
            }
        }
    }

    private void initializeCommands() {
        commands.put("lookup", new LookupCommand(service, out));
        commands.put("define", new DefineCommand(service, out));
        commands.put("drop", new DropCommand(service, out));
        commands.put("export", new ExportCommand(service, out));
        commands.put("import", new ImportCommand(service, out));
        commands.put("help", request -> printHelp());
        commands.put("exit", request -> {
            running = false;
            out.println("Goodbye!");
        });

        requestBuilders.put("lookup", args -> buildKeywordRequest("lookup", args));
        requestBuilders.put("drop", args -> buildKeywordRequest("drop", args));
        requestBuilders.put("export", args -> new Request("export", joinArgs(args, "export.txt"), List.of()));
        requestBuilders.put("import", args -> new Request("import", joinArgs(args, "import.txt"), List.of()));
        requestBuilders.put("define", this::buildDefineRequest);
        requestBuilders.put("help", args -> new Request("help", "", List.of()));
        requestBuilders.put("exit", args -> new Request("exit", "", List.of()));
    }

    private Request buildKeywordRequest(String action, List<String> args) {
        if (args.isEmpty()) {
            return null;
        }
        return new Request(action, String.join(" ", args), List.of());
    }

    private Request buildDefineRequest(List<String> args) {
        if (args.size() < 2) {
            return null;
        }

        String modeToken = args.get(0).toLowerCase(Locale.ROOT);
        String keyword = args.get(1).trim();
        if (keyword.isBlank()) {
            return null;
        }

        DefineMode mode = resolveDefineMode(modeToken);
        if (mode == null) {
            return null;
        }

        if (service.lookup(keyword) == null) {
            out.println("@" + keyword + " is not existed in database, created new one!");
        }

        return switch (mode) {
            case PRONUNCIATION -> new Request("define", keyword, prompt("Pronunciation: "), "", List.of(modeToken));
            case SYNONYM -> new Request("define", keyword, "", "", List.of(modeToken, prompt("Synonyms (comma separated): ")));
            case NOUN, ADJECTIVE, VERB -> {
                String label = labelFor(mode);
                String meaning = prompt(label + " definition: ");
                String sentence = prompt("Sentence: ");
                String sentenceMeaning = prompt("Sentence's meaning: ");
                yield new Request("define", keyword, "", meaning, List.of(modeToken, sentence, sentenceMeaning));
            }
        };
    }

    private DefineMode resolveDefineMode(String modeToken) {
        return switch (modeToken) {
            case "--pronoun", "-p" -> DefineMode.PRONUNCIATION;
            case "--noun", "-n" -> DefineMode.NOUN;
            case "--adjective", "-a" -> DefineMode.ADJECTIVE;
            case "--verb", "-v" -> DefineMode.VERB;
            case "--synonymous", "-s" -> DefineMode.SYNONYM;
            default -> null;
        };
    }

    private String labelFor(DefineMode mode) {
        return switch (mode) {
            case NOUN -> "Noun";
            case ADJECTIVE -> "Adjective";
            case VERB -> "Verb";
            default -> "Definition";
        };
    }

    private String prompt(String message) {
        out.print(message);
        out.flush();
        if (!scanner.hasNextLine()) {
            return "";
        }
        return scanner.nextLine().trim();
    }

    private String joinArgs(List<String> args, String defaultValue) {
        if (args.isEmpty()) {
            return defaultValue;
        }
        String joined = String.join(" ", args).trim();
        return joined.isBlank() ? defaultValue : joined;
    }

    private List<String> tokenize(String line) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(line);
        while (matcher.find()) {
            String quoted = matcher.group(1);
            if (quoted != null) {
                tokens.add(quoted);
                continue;
            }
            String singleQuoted = matcher.group(2);
            if (singleQuoted != null) {
                tokens.add(singleQuoted);
                continue;
            }
            tokens.add(matcher.group());
        }
        return tokens;
    }

    private void printBanner() {
        out.println("EV Dictionary Pro - CLI");
        out.println("Type 'help' to see commands.");
    }

    private void printHelp() {
        out.println("Commands:");
        out.println("  lookup <keyword>");
        out.println("  define --noun|--adjective|--verb|--pronoun|--synonymous <keyword>");
        out.println("  drop <keyword>");
        out.println("  export <file_path>");
        out.println("  import <file_path>");
        out.println("  help");
        out.println("  exit");
    }
}
