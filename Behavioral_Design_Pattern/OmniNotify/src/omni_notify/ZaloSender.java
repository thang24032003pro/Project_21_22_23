package omni_notify;

public class ZaloSender implements MessageSender {
    @Override
    public void send(String message, String recipient) {
        System.out.println("Khởi tạo Zalo SDK Client...");
        System.out.println("Gửi tin nhắn Zalo tới sđt " + recipient + ": " + message);
    }
}