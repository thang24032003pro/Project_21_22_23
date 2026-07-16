package omni_notify;

public class EmailSender implements MessageSender {
    @Override
    public void send(String message, String recipient) {
        System.out.println("Gửi EMAIL tới " + recipient + ": " + message);
    }
}