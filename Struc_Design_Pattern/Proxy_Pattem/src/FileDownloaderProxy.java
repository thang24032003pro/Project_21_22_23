// File: FileDownloaderProxy.java
public class FileDownloaderProxy implements Downloader {
    private FileDownloader fileDownloader;

    public FileDownloaderProxy() {
        // Khởi tạo FileDownloader với User-Agent mặc định của trình duyệt Firefox
        String firefoxUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/115.0";
        this.fileDownloader = new FileDownloader(firefoxUserAgent);
    }

    @Override
    public void download(String urlString, String destinationPath) {
        // Chuyển tiếp lời gọi tải file tới dịch vụ thực thi thật
        this.fileDownloader.download(urlString, destinationPath);
    }
}