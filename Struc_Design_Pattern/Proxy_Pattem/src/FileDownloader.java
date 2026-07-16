
// File: FileDownloader.java
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class FileDownloader implements Downloader {
    private String userAgent;

    // Constructor nhận User-Agent từ ngoài vào
    public FileDownloader(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public void download(String urlString, String destinationPath) {
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            // Thiết lập User-Agent cho kết nối
            connection.setRequestProperty("User-Agent", this.userAgent);

            // Tiến hành đọc dữ liệu từ luồng mạng và ghi vào file local
            try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                    FileOutputStream fileOutputStream = new FileOutputStream(destinationPath)) {

                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
                System.out.println("Tải file thành công! Lưu tại: " + destinationPath);
            }
        } catch (IOException e) {
            System.err.println("Lỗi trong quá trình tải tệp: " + e.getMessage());
        }
    }
}