// File: Client.java
public class Client {
    private Downloader downloader;

    public Client(Downloader downloader) {
        this.downloader = downloader;
    }

    public void downloadFile(String url, String destination) {
        downloader.download(url, destination);
    }
}