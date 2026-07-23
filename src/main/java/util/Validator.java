package util;

import java.util.Scanner;

public class Validator {
    private static final String PHONE_REGEX = "^0[0-9]{9}$";
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

    public static String requireNonEmpty(Scanner scanner, String fieldName, String promptText) {
        while (true) {
            System.out.print(promptText);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Truong '" + fieldName + "' khong duoc de trong.");
                continue;
            }
            return input;
        }
    }

    public static String inputPhoneNumber(Scanner scanner, String promptText) {
        while (true) {
            System.out.print(promptText);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("So dien thoai khong duoc de trong.");
                continue;
            }
            if (!input.matches(PHONE_REGEX)) {
                System.out.println("Sai dinh dang so dien thoai. Vui long nhap 10 chu so bat dau bang 0.");
                continue;
            }
            return input;
        }
    }

    public static String inputEmail(Scanner scanner, String promptText) {
        while (true) {
            System.out.print(promptText);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Email khong duoc de trong.");
                continue;
            }
            if (!input.matches(EMAIL_REGEX)) {
                System.out.println("Sai dinh dang email. Vui long nhap lai.");
                continue;
            }
            return input;
        }
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches(PHONE_REGEX);
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches(EMAIL_REGEX);
    }
}
