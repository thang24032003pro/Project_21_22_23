public class XMLStorage implements UserStorage {
    @Override
    public void store(User user) {
        System.out.println("--- XML Storage ---");
        System.out.println("Đang chuyển đổi dữ liệu " + user.getUsername() + " sang cấu trúc XML...");
        System.out.println("Ghi tệp tin: <user><username>" + user.getUsername() + "</username><email>" + user.getEmail()
                + "</email></user>");
        System.out.println("Lưu file XML thành công!");
    }
}