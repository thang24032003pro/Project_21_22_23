public class Main {
    public static void main(String[] args) {
        User user = new User("nguyen_van_a", "vana@gmail.com");

        System.out.println("====== THỬ NGHIỆM STRATEGY PATTERN CHO LƯU TRỮ ======\n");

        System.out.println("[Kịch bản 1]: Sử dụng XMLStorage");
        UserController controllerWithXML = new UserController(new XMLStorage());
        controllerWithXML.store(user);
        System.out.println();

        System.out.println("[Kịch bản 2]: Sử dụng MySQLStorage");
        UserController controllerWithMySQL = new UserController(new MySQLStorage());
        controllerWithMySQL.store(user);
        System.out.println();

        System.out.println("[Kịch bản 3]: Thay đổi chiến lược lưu trữ lúc Runtime bằng Setter");
        controllerWithXML.setUserStorage(new MySQLStorage());
        controllerWithXML.store(user);
    }
}