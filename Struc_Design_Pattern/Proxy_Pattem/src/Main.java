// File: Main.java
public class Main {
    public static void main(String[] args) {
        // Khởi tạo đối tượng Proxy đóng vai trò Downloader
        Downloader proxyDownloader = new FileDownloaderProxy();
        Client client = new Client(proxyDownloader);

        // URL của file cần tải và đường dẫn lưu trữ cục bộ (local) mong muốn
        String fileUrl = "https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png";
        String savePath = "google_logo.png";

        System.out.println("Bắt đầu thực hiện tải file...");
        client.DownloadFile(fileUrl, savePath);
    }
}