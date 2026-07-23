import service.ContactService;
import view.Menu;

import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.out.println("Khong the cau hinh encoding UTF-8 cho output.");
        }

        Scanner scanner = new Scanner(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        ContactService contactService = new ContactService();
        Menu menu = new Menu(scanner, contactService);
        menu.run();
    }
}
