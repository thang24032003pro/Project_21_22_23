public class UserController {
    private UserStorage userStorage;

    // Nhận chiến lược lưu trữ (UserStorage) thông qua Constructor
    public UserController(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    // Cho phép thay đổi chiến lược lưu trữ linh hoạt lúc runtime
    public void setUserStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void store(User user) {
        if (this.userStorage == null) {
            throw new IllegalStateException("Chưa thiết lập phương thức lưu trữ (UserStorage)!");
        }
        // Gọi phương thức store của chiến lược hiện tại
        this.userStorage.store(user);
    }
}