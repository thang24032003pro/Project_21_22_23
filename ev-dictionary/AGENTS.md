# EV Dictionary Pro - AI Agent Instructions

## Project Overview

**EV Dictionary Pro** is a JavaFX desktop application for managing dictionary entries with full CRUD operations, search, import/export, and theme support. It follows classic design patterns and clean architecture principles.

- **Language**: Java 17 with JPMS (Java Platform Module System)
- **Build Tool**: Maven
- **GUI Framework**: JavaFX 17 with FXML
- **Testing**: JUnit 5
- **Architecture**: MVC + Command + Factory + Service + Repository patterns

## Project Structure

```
src/main/java/com/dict/
├── App.java                    # JavaFX Application entry point
├── Main.java                   # Launcher class
├── controller/                 # JavaFX FXML controllers (MVC View-Controller layer)
│   ├── DictionaryController.java  # Main UI controller handling user interactions
│   └── Request.java            # Command request DTO
├── command/                    # Command pattern implementation
│   ├── Command.java           # Command interface
│   ├── DefineCommand.java     # Add/update word definition
│   ├── LookupCommand.java     # Search word in dictionary
│   ├── DropCommand.java       # Delete word
│   ├── ImportCommand.java     # Import from CSV/JSON
│   └── ExportCommand.java     # Export to CSV/JSON
├── service/                    # Business logic layer (Singleton)
│   └── DictionaryService.java  # Service orchestrator with storage and factory
├── entity/                     # Domain model
│   ├── Word.java
│   ├── DictionaryEntry.java
│   ├── Definition.java
│   ├── PartOfSpeech.java
│   ├── Pronunciation.java
│   ├── Synonym.java
│   └── ExampleSentence.java
├── factory/                    # Factory pattern for entity creation
│   └── EntityFactory.java
├── storage/                    # Persistence layer
│   └── FileDictionaryStorage.java
└── util/                       # Utility classes
```

### Resources
- `src/main/resources/com/dict/view/main-view.fxml` - Main UI definition
- `src/main/resources/com/dict/css/` - Theming (dark.css, light.css)

## Key Architectural Patterns

### 1. **Command Pattern** (Controller → Commands)
Commands encapsulate dictionary operations. Each command handles a specific operation:
- `LookupCommand`: Search functionality
- `DefineCommand`: Add/update entries
- `DropCommand`: Delete entries
- `ImportCommand`/`ExportCommand`: Data interchange

**Adding a new command**:
1. Create `NewCommand.java` implementing `Command` interface
2. Register in `DictionaryController.constructor()` in the commands map
3. Call via `commands.get("commandName").execute(request)`

### 2. **Service Layer** (Singleton Pattern)
`DictionaryService` is a singleton managing:
- Dictionary entry storage (in-memory Map)
- Business logic operations
- Delegation to `FileDictionaryStorage` and `EntityFactory`

**Important**: Use `DictionaryService.getInstance(Path)` to get the singleton instance.

### 3. **Factory Pattern**
`EntityFactory` creates domain entities consistently. Use it when constructing Word, Definition, or related objects.

### 4. **MVC with JavaFX**
- **Model**: Entity classes (Word, Definition, etc.)
- **View**: FXML in `main-view.fxml` + CSS theming
- **Controller**: `DictionaryController` with `@FXML` annotations

## Common Development Tasks

### Build & Run
```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Package application
mvn clean package

# Run application (after compilation)
java -m com.dict/com.dict.App
```

### Add a New Command
1. Create command class in `src/main/java/com/dict/command/`
2. Implement `Command` interface with `execute(Request request)` method
3. Register in `DictionaryController` constructor
4. Add UI button/menu item in FXML if needed

### Add a New Entity Field
1. Add field to entity class in `src/main/java/com/dict/entity/`
2. Update constructor, getters, setters
3. Update `EntityFactory` if factory construction is affected
4. Update `FileDictionaryStorage` serialization/deserialization
5. Add unit tests in `src/test/java/`

### Modify Storage Format
- `FileDictionaryStorage` handles all file I/O
- Modify load/save methods to support new format
- Update import/export commands accordingly
- Keep backward compatibility in mind

### Update UI Theme
- Edit `src/main/resources/com/dict/css/dark.css` or `light.css`
- Controller switches themes via stylesheet methods in `DictionaryController`

## Module System (JPMS)

The project uses Java modules (`module-info.java`):
```java
module com.dict {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    
    opens com.dict to javafx.fxml;
    opens com.dict.controller to javafx.fxml;
    exports com.dict;
    exports com.dict.controller;
}
```

**Important**: When adding new controller classes, ensure they're opened to `javafx.fxml` in module-info.java, or FXML injection will fail.

## Testing

Tests are located in `src/test/java/com/dict/`. Currently:
- `DictionaryServiceTest.java` - Service layer unit tests

**Guidelines**:
- Use JUnit 5 (`@Test`, `@BeforeEach`, assertions from `org.junit.jupiter.api`)
- Test command execution via `DictionaryService`
- Mock file storage for unit tests where possible
- Include integration tests for storage operations

## Code Style Conventions

- **Package structure**: Follows functional layers (controller, command, service, entity, storage, factory, util)
- **Naming**: CamelCase for classes/methods, lowercase with underscores for constants
- **JavaFX**: Use `@FXML` annotations for field injection from FXML
- **Singleton pattern**: `DictionaryService` uses static instance check
- **Collections**: Prefer `LinkedHashMap` for maintaining insertion order

## Dependencies & Versions

- **Java**: 17 (LTS)
- **JavaFX**: 17.0.10
- **JUnit**: 5.10.1
- **Maven Compiler**: 3.13.0
- **Maven Surefire**: 3.2.5

## Potential Pitfalls

1. **Module system issues**: Forgetting to open controller classes to `javafx.fxml` causes FXML injection failures
2. **Singleton state**: `DictionaryService` may retain state between test runs; use `getInstance()` carefully in tests
3. **Path handling**: Storage path defaults to `Path.of("storage")` in controller; ensure directory exists
4. **UI thread**: JavaFX operations must run on the UI thread; use `Platform.runLater()` for async work
5. **File encoding**: Ensure UTF-8 encoding in pom.xml (`project.build.sourceEncoding`)

## Next Steps for Agents

- When implementing new features, maintain the command pattern for operations
- Keep entities immutable or use defensive copying
- Ensure new components are properly integrated into the service layer
- Add unit tests for any new business logic
- Update FXML and CSS if UI changes are needed
