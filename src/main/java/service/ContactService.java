package service;

import model.Contact;
import util.FileHandler;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ContactService {
    private final List<Contact> contacts;
    private final FileHandler fileHandler;

    public ContactService() {
        this.contacts = new ArrayList<>();
        this.fileHandler = new FileHandler();
    }

    public List<Contact> getAllContacts() {
        return contacts;
    }

    public boolean addContact(Contact contact) {
        if (contact == null) {
            return false;
        }
        return contacts.add(contact);
    }

    public Contact findByPhoneNumber(String phoneNumber) {
        for (Contact contact : contacts) {
            if (contact.getPhoneNumber().equalsIgnoreCase(phoneNumber)) {
                return contact;
            }
        }
        return null;
    }

    public List<Contact> searchByKeyword(String keyword) {
        List<Contact> result = new ArrayList<>();
        String key = keyword == null ? "" : keyword.trim().toLowerCase();
        for (Contact contact : contacts) {
            if (contact.getPhoneNumber().toLowerCase().contains(key)
                    || contact.getFullName().toLowerCase().contains(key)) {
                result.add(contact);
            }
        }
        return result;
    }

    public boolean updateContact(String phoneNumber, Contact updatedContact) {
        Contact existingContact = findByPhoneNumber(phoneNumber);
        if (existingContact == null) {
            return false;
        }

        existingContact.setGroup(updatedContact.getGroup());
        existingContact.setFullName(updatedContact.getFullName());
        existingContact.setGender(updatedContact.getGender());
        existingContact.setAddress(updatedContact.getAddress());
        existingContact.setDob(updatedContact.getDob());
        existingContact.setEmail(updatedContact.getEmail());
        return true;
    }

    public boolean deleteContact(String phoneNumber) {
        Contact contact = findByPhoneNumber(phoneNumber);
        if (contact == null) {
            return false;
        }
        return contacts.remove(contact);
    }

    public void clearAll() {
        contacts.clear();
    }

    public boolean loadFromFile(Path path) {
        try {
            List<Contact> loadedContacts = fileHandler.readContactsFromFile(path);
            contacts.clear();
            contacts.addAll(loadedContacts);
            return true;
        } catch (IOException e) {
            System.out.println("Lỗi đọc file: " + e.getMessage());
            return false;
        }
    }

    public boolean saveToFile(Path path) {
        try {
            fileHandler.writeContactsToFile(path, contacts);
            return true;
        } catch (IOException e) {
            System.out.println("Lỗi ghi file: " + e.getMessage());
            return false;
        }
    }
}
