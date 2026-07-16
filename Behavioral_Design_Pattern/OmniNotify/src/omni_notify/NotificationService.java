package omni_notify;

public class NotificationService {
    private MessageSender messageSender;

    public NotificationService(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void notify(String message, String recipient) {
        this.messageSender.send(message, recipient);
    }
}