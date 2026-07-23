package util;

import model.Contact;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    public static final Path DEFAULT_FILE_PATH = Paths.get("data", "contacts.csv");
    private static final String HEADER = "phoneNumber,group,fullName,gender,address,dob,email";

    public List<Contact> readContactsFromFile(Path path) throws IOException {
        List<Contact> contacts = new ArrayList<>();
        if (!Files.exists(path)) {
            throw new IOException("File khong ton tai: " + path.toAbsolutePath());
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            boolean isHeaderSkipped = false;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                if (!isHeaderSkipped && line.equalsIgnoreCase(HEADER)) {
                    isHeaderSkipped = true;
                    continue;
                }
                Contact contact = Contact.fromCSV(line);
                if (contact != null) {
                    contacts.add(contact);
                }
            }
        }

        return contacts;
    }

    public void writeContactsToFile(Path path, List<Contact> contacts) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(HEADER);
            writer.newLine();
            for (Contact contact : contacts) {
                writer.write(contact.toCSV());
                writer.newLine();
            }
        }
    }
}
