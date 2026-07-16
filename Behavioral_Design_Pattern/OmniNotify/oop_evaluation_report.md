# BÁO CÁO CHẨN ĐOÁN & ĐÁNH GIÁ THIẾT KẾ OOP - OMNINOTIFY

## 1. Chẩn đoán Legacy Code
Thiết kế cũ vi phạm nghiêm trọng hai nguyên lý SOLID cơ bản:
*   **Single Responsibility Principle (SRP):** Lớp `NotificationService` gánh vác quá nhiều trách nhiệm, vừa điều phối vừa trực tiếp nắm giữ logic chi tiết của các kênh gửi (SMTP, API mạng).
*   **Open/Closed Principle (OCP):** Khi thêm kênh mới (như Telegram, Zalo), lập trình viên bắt buộc phải sửa đổi câu lệnh `switch-case` trực tiếp bên trong file dịch vụ lõi. Điều này dễ gây ra lỗi dây chuyền (regression bugs) lên các kênh cũ đang chạy ổn định.

## 2. Giải pháp và Lợi ích của Interface (Strategy Pattern)
*   **Phân tách trách nhiệm:** Chuyển toàn bộ logic thực thi cụ thể về các lớp độc lập (`EmailSender`, `SmsSender`, `ZaloSender`). 
*   **Đóng cho việc sửa đổi, Mở cho việc mở rộng (OCP):** Interface `MessageSender` thiết lập "hợp đồng" chung. Khi tích hợp thêm kênh như `WhatsApp`, ta chỉ cần tạo một file mới triển khai Interface này mà **không cần can thiệp hay chỉnh sửa** bất kỳ dòng mã nguồn nào của `NotificationService`.
*   **Bảo trì dài hạn:** Giúp cô lập các lỗi phát sinh. Lỗi ở thư viện kết nối Email chỉ cần sửa ở `EmailSender`, loại trừ hoàn toàn nguy cơ gây tê liệt dịch vụ SMS hay Telegram, tăng độ ổn định hệ thống lên tối đa.