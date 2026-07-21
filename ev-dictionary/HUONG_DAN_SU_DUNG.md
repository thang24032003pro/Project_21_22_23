# EV Dictionary Pro - Hướng Dẫn Sử Dụng JavaFX GUI

## 1. Mục Tiêu
EV Dictionary Pro là ứng dụng từ điển với giao diện JavaFX. Ứng dụng hỗ trợ tra từ, thêm định nghĩa, xóa mục từ và xuất dữ liệu ra tệp văn bản.

## 2. Yêu Cầu Chạy
- Java 17 hoặc mới hơn
- Maven 3.9+ để biên dịch và chạy
- Máy có Windows hoặc terminal tương đương

## 3. Cách Chạy
### Cách 1: Dùng script có sẵn
Chạy file `run.bat` ở thư mục gốc dự án.

### Cách 2: Dùng Maven
```bash
cd /d d:\Class_java\moder2\project_21_22_23\ev-dictionary
mvn -q -DskipTests compile
mvn javafx:run
```

> Lưu ý: Không dùng `mvn java:javarun` vì dự án này không dùng plugin `java`. Lệnh đúng để chạy GUI là `mvn javafx:run`.

Sau khi chạy, cửa sổ `EV Dictionary Pro` sẽ mở. Bạn nhập từ vào ô `Word:`, sau đó nhấn `Go` để tra từ.

## 4. Cách sử dụng GUI
- `Word:` nhập từ cần tra.
- Nhấn `Go` hoặc `Lookup` để hiển thị kết quả.
- `Pronunciation:` nhập phát âm nếu muốn lưu cùng từ.
- `Meaning:` nhập nghĩa của từ.
- `Sentence:` nhập câu ví dụ.
- `Save` lưu từ/cập nhật từ hiện tại vào dữ liệu.
- `Drop` xóa từ hiện tại khỏi cơ sở dữ liệu.
- `Export` xuất toàn bộ dữ liệu ra tệp `export.txt`.

## 5. Dữ Liệu Lưu Trữ
- Dữ liệu được lưu trong thư mục `storage/dictionary/`.
- Mỗi từ là một file `.def` riêng.
- Ứng dụng tự động đọc các file `.def` khi khởi động.
- Khi nhấn `Save`, ứng dụng sẽ ghi file `.def` mới nếu từ chưa tồn tại, hoặc cập nhật file hiện có nếu từ đã tồn tại.
- File `.def` chứa dữ liệu như:
  - `keyword=`
  - `pronunciation=`
  - `definition=`
  - `sentence=`
  - `synonym=`
- Dữ liệu mẫu hiện có gồm các từ: `positive`, `computer`, `beautiful`, `improve`, `friend`, `careful`, `believe`, `discover`, `education`, `solution`.

## 6. Cách lưu và xuất
- Nhấn `Save`: ghi dữ liệu vào `storage/dictionary/<tu>.def`.
- Nhấn `Export`: xuất toàn bộ dữ liệu hiện tại ra file `export.txt` ở thư mục gốc dự án.
- Nếu không thấy file `export.txt`, hãy kiểm tra quyền ghi và thư mục chạy.

## 7. Lưu ý khi chạy
- `run.bat` sử dụng Maven để biên dịch và chạy JavaFX.
- Nếu không tìm thấy Maven, cài đặt Maven hoặc sửa đường dẫn trong `run.bat`.
- Nếu GUI không mở được, kiểm tra Java 17 cài đặt và `mvn` chạy được.
- Các lệnh CLI như `define`, `lookup`, `drop`, `export` không còn dùng trong GUI.
