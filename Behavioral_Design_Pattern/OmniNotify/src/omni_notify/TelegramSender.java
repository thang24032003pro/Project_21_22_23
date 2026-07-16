package omni_notify;

public class TelegramSender implements MessageSender {
    @Override
    public void send(String message, String recipient) {
        System.out.println("Gửi Telegram tới @" + recipient + ": " + message);
    }
}