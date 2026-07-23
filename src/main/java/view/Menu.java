package view;

import model.Contact;
import service.ContactService;
import util.FileHandler;
import util.Validator;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private final Scanner scanner;
    private final ContactService contactService;
    private final Path defaultCsvPath = FileHandler.DEFAULT_FILE_PATH;

    private void printText(String text) {
        System.out.println(text);
    }

    public Menu(Scanner scanner, ContactService contactService) {
        this.scanner = scanner;
        this.contactService = contactService;
        contactService.loadFromFile(defaultCsvPath);
    }

    public void run() {
        printWelcomeBanner();
        while (true) {
            printMainMenu();
            System.out.print("\nChon chuc nang: ");

            if (!scanner.hasNextLine()) {
                printText("\nKet thuc chuong trinh.");
                return;
            }

            String choiceInput = scanner.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(choiceInput);
            } catch (NumberFormatException e) {
                printText("Vui long nhap so tu 1 den 8.");
                continue;
            }

            switch (choice) {
                case 1:
                    viewContacts();
                    break;
                case 2:
                    addContact();
                    break;
                case 3:
                    updateContact();
                    break;
                case 4:
                    deleteContact();
                    break;
                case 5:
                    searchContacts();
                    break;
                case 6:
                    loadFromFile();
                    break;
                case 7:
                    saveToFile();
                    break;
                case 8:
                    printText("Tam biet!");
                    return;
                default:
                    printText("Lua chon khong hop le. Vui long chon lai.");
                    break;
            }
        }
    }

    private void printWelcomeBanner() {
        System.out.println("\n+-----------------------------------------+");
        System.out.println("|        QUAN LY DANH BA CONSOLE          |");
        System.out.println("+-----------------------------------------+");
        System.out.println("Chao mung ban den voi he thong quan ly danh ba.");
    }

    private void printMainMenu() {
        System.out.println("\n+-----------------------------------------+");
        System.out.println("| 1. Xem danh sach                       |");
        System.out.println("| 2. Them moi                            |");
        System.out.println("| 3. Cap nhat                            |");
        System.out.println("| 4. Xoa                                 |");
        System.out.println("| 5. Tim kiem                            |");
        System.out.println("| 6. Doc tu file                         |");
        System.out.println("| 7. Luu vao file                        |");
        System.out.println("| 8. Thoat                               |");
        System.out.println("+-----------------------------------------+");
    }

    private void printDivider() {
        System.out.println("------------------------------------------");
    }

    private void printSectionTitle(String title) {
        System.out.println("\n+--------------------------------------+");
        System.out.println("| " + String.format("%-36s", title) + "|");
        System.out.println("+--------------------------------------+");
    }

    private void viewContacts() {
        List<Contact> contacts = contactService.getAllContacts();
        if (contacts.isEmpty()) {
            System.out.println("Danh ba hien dang trong.");
            return;
        }

        printSectionTitle("XEM DANH SACH DANH BA");
        int pageSize = 5;
        for (int start = 0; start < contacts.size(); start += pageSize) {
            int end = Math.min(start + pageSize, contacts.size());
            System.out.println("\n--- Trang " + ((start / pageSize) + 1) + " ---");
            System.out.printf("%-15s %-12s %-20s %-10s %-25s%n",
                    "SDT", "Nhom", "Ho ten", "Gioi tinh", "Dia chi");
            printDivider();

            for (int i = start; i < end; i++) {
                Contact contact = contacts.get(i);
                System.out.printf("%-15s %-12s %-20s %-10s %-25s%n",
                        contact.getPhoneNumber(),
                        contact.getGroup(),
                        contact.getFullName(),
                        contact.getGender(),
                        contact.getAddress());
            }

            if (end < contacts.size()) {
                System.out.println("\nNhan Enter de xem tiep...");
                if (!scanner.hasNextLine()) {
                    return;
                }
                scanner.nextLine();
            }
        }
    }

    private void addContact() {
        printSectionTitle("THEM DANH BA MOI");

        String phoneNumber = Validator.inputPhoneNumber(scanner, "Nhap SDT: ");
        String group = Validator.requireNonEmpty(scanner, "Nhom", "Nhap Nhom: ");
        String fullName = Validator.requireNonEmpty(scanner, "Ho ten", "Nhap Ho ten: ");
        String gender = Validator.requireNonEmpty(scanner, "Gioi tinh", "Nhap Gioi tinh: ");
        String address = Validator.requireNonEmpty(scanner, "Dia chi", "Nhap Dia chi: ");
        String dob = Validator.requireNonEmpty(scanner, "Ngay sinh", "Nhap Ngay sinh: ");
        String email = Validator.inputEmail(scanner, "Nhap Email: ");

        Contact contact = new Contact(phoneNumber, group, fullName, gender, address, dob, email);
        contactService.addContact(contact);
        System.out.println("Them moi thanh cong!");
    }

    private void updateContact() {
        while (true) {
            printSectionTitle("CAP NHAT DANH BA");
            System.out.print("Nhap SDT can sua (bo trong de thoat): ");
            String phoneNumber = scanner.nextLine().trim();

            if (phoneNumber.isEmpty()) {
                return;
            }

            Contact existingContact = contactService.findByPhoneNumber(phoneNumber);
            if (existingContact == null) {
                System.out.println("Khong tim duoc danh ba voi so dien thoai tren.");
                continue;
            }

            String group = Validator.requireNonEmpty(scanner, "Nhom", "Nhap Nhom moi: ");
            String fullName = Validator.requireNonEmpty(scanner, "Ho ten", "Nhap Ho ten moi: ");
            String gender = Validator.requireNonEmpty(scanner, "Gioi tinh", "Nhap Gioi tinh moi: ");
            String address = Validator.requireNonEmpty(scanner, "Dia chi", "Nhap Dia chi moi: ");
            String dob = Validator.requireNonEmpty(scanner, "Ngay sinh", "Nhap Ngay sinh moi: ");
            String email = Validator.inputEmail(scanner, "Nhap Email moi: ");

            Contact updatedContact = new Contact(existingContact.getPhoneNumber(), group, fullName, gender, address, dob, email);
            if (contactService.updateContact(phoneNumber, updatedContact)) {
                System.out.println("Cap nhat thanh cong!");
            } else {
                System.out.println("Cap nhat that bai.");
            }
            return;
        }
    }

    private void deleteContact() {
        while (true) {
            printSectionTitle("XOA DANH BA");
            System.out.print("Nhap SDT muon xoa (bo trong de thoat): ");
            String phoneNumber = scanner.nextLine().trim();

            if (phoneNumber.isEmpty()) {
                return;
            }

            Contact contact = contactService.findByPhoneNumber(phoneNumber);
            if (contact == null) {
                System.out.println("Khong tim duoc danh ba voi so dien thoai tren.");
                continue;
            }

            System.out.println("Ban co chac chan muon xoa danh ba nay? (Nhap 'Y' de xac nhan): ");
            String confirm = scanner.nextLine().trim();
            if (confirm.equalsIgnoreCase("Y")) {
                if (contactService.deleteContact(phoneNumber)) {
                    System.out.println("Xoa thanh cong!");
                } else {
                    System.out.println("Xoa that bai.");
                }
            } else {
                System.out.println("Da huy thao tac xoa.");
            }
            return;
        }
    }

    private void searchContacts() {
        printSectionTitle("TIM KIEM DANH BA");
        System.out.print("Nhap tu khoa: ");
        String keyword = scanner.nextLine().trim();
        if (keyword.isEmpty()) {
            System.out.println("Tu khoa khong duoc de trong.");
            return;
        }

        List<Contact> results = contactService.searchByKeyword(keyword);
        if (results.isEmpty()) {
            System.out.println("Khong tim thay ket qua nao.");
            return;
        }

        printDivider();
        System.out.printf("%-15s %-20s%n", "SDT", "Ho ten");
        printDivider();
        for (Contact contact : results) {
            System.out.printf("%-15s %-20s%n", contact.getPhoneNumber(), contact.getFullName());
        }
    }

    private void loadFromFile() {
        System.out.println("\nHanh dong nay se XOA TOAN BO danh ba trong bo nho. Ban co muon tiep tuc? (Y/N)");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Da huy thao tac doc file.");
            return;
        }

        boolean loaded = contactService.loadFromFile(defaultCsvPath);
        if (loaded) {
            System.out.println("Doc du lieu tu file thanh cong.");
        }
    }

    private void saveToFile() {
        System.out.println("\nHanh dong nay se GHI DE du lieu vao file contacts.csv. Ban co muon tiep tuc? (Y/N)");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Da huy thao tac luu file.");
            return;
        }

        boolean saved = contactService.saveToFile(defaultCsvPath);
        if (saved) {
            System.out.println("Luu du lieu vao file thanh cong.");
        }
    }
}
