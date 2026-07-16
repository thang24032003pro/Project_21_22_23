package omni_notify;

public class WhatsAppSender implements MessageSender {
    @Override
    public void send(String message, String recipient) {
        System.out.println("--- [WhatsApp Channel] ---");
        System.out.println("Kết nối API Facebook Business (WhatsApp Cloud API)...");
        System.out.println("Gửi WhatsApp tới số +" + recipient + ": " + message);
    }
}