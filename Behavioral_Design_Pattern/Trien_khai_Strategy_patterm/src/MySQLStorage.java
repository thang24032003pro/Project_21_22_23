public class MySQLStorage implements UserStorage {
    @Override
    public void store(User user) {
        System.out.println("--- MySQL Database Storage ---");
        System.out.println("Thiết lập kết nối tới cơ sở dữ liệu MySQL...");
        System.out.println("Thực thi truy vấn: INSERT INTO users (username, email) VALUES ('" + user.getUsername()
                + "', '" + user.getEmail() + "');");
        System.out.println("Lưu dữ liệu vào bảng 'users' thành công!");
    }
}