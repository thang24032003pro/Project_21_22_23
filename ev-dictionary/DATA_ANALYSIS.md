# EV Dictionary Pro - Phân Tích Dữ Liệu

## 1. Định dạng dữ liệu
Dự án lưu từ điển dưới dạng file `.def`, mỗi file tương ứng một từ.

Mỗi file `.def` có cấu trúc:
- `keyword=`: từ khóa chính
- `pronunciation=`: phiên âm
- `definition=`: định nghĩa, theo dạng `PART_OF_SPEECH|meaning`
- `definition-example=`: ví dụ cho từ theo định nghĩa
- `sentence=`: câu ví dụ kèm nghĩa tiếng Việt
- `synonym=`: từ đồng nghĩa

Định dạng file sử dụng UTF-8.

## 2. Cách hoạt động của hệ thống
1. Khi khởi động, `DictionaryService.load()` đọc tất cả file `.def` trong thư mục `storage/dictionary/`.
2. Mỗi file được parse thành `DictionaryEntry`, bao gồm `Word`, `Pronunciation`, `Definition`, `ExampleSentence`, `Synonym`.
3. Dữ liệu được lưu trong bộ nhớ dưới dạng `LinkedHashMap<String, DictionaryEntry>` để tra cứu nhanh theo từ đã chuẩn hóa.
4. Khi người dùng nhập từ trong GUI, `DictionaryService.lookup(keyword)` tìm nhanh trong `Map`.
5. Nếu lưu từ mới, `DictionaryService.define(entry)` sẽ ghi file `.def` incremental bằng `FileDictionaryStorage.saveEntry(entry)`.
6. Khi export, `DictionaryService.export()` tạo file `export.txt` chứa toàn bộ dữ liệu đang có.

## 3. Dữ liệu đang dùng
Hiện tại hệ thống đã có dữ liệu mẫu sau:
- `positive`, `computer`, `beautiful`, `improve`, `friend`, `careful`, `believe`, `discover`, `education`, `solution`
- `ability`, `achievement`, `application`, `balance`, `challenge`, `context`, `detail`, `dictionary`, `effort`, `feature`, `grammar`, `example`

## 4. Đã hoàn thiện
- Đã thêm trường `partOfSpeech` rõ ràng trên GUI bằng ComboBox, cho phép chọn `NOUN`, `VERB`, `ADJECTIVE`, vv.
- Đã hỗ trợ nhập nhiều nghĩa cho 1 từ, mỗi nghĩa có thể tách bằng dấu `;` khi lưu vào dữ liệu.
- Đã hiển thị câu ví dụ và nghĩa tiếng Việt rõ ràng hơn trong phần kết quả tra cứu.

## 5. Phân tích dữ liệu
- `keyword`: dùng làm chỉ mục tra cứu.
- `pronunciation`: phục vụ hiển thị phát âm.
- `definition`: chứa từ loại + nghĩa; phần mềm công nhận `PartOfSpeech` qua `PartOfSpeech.fromString()`.
- `sentence`: dùng để tạo ví dụ trực quan.
- `synonym`: giúp mở rộng tra cứu tương đồng.

## 6. Luồng dữ liệu tìm kiếm và gợi ý
1. Người dùng gõ chữ vào `TextField` (`wordFieldUser` hoặc `wordFieldAdmin`).
2. Mỗi ký tự nhập vào kích hoạt listener trong `DictionaryGuiController.initialize()`.
3. Listener gọi `updateSuggestions(prefix, suggestionsView)` và gửi chuỗi hiện tại tới `DictionaryService.find(prefix)`.
4. `DictionaryService.find(prefix)` chuẩn hóa chuỗi với `normalize()` và duyệt toàn bộ `entries` trong bộ nhớ.
5. Nếu `keyword.startsWith(prefix)` thì từ đó được thêm vào danh sách gợi ý.
6. Danh sách gợi ý được hiển thị trong `ListView` tương ứng (`suggestionListUser` hoặc `suggestionListAdmin`).
7. Người dùng có thể nhấp đôi vào một gợi ý để chọn từ đó; controller sẽ đặt giá trị vào `TextField` và gọi `lookupWord()`.
8. `lookupWord()` gọi `DictionaryService.lookup(keyword)` để truy xuất chính xác, hoặc hiển thị danh sách gợi ý nếu không tìm thấy.

## 7. Đã hoàn thiện
- Đã thêm trường `partOfSpeech` rõ ràng trên GUI bằng ComboBox, cho phép chọn `NOUN`, `VERB`, `ADJECTIVE`, vv.
- Đã hỗ trợ nhập nhiều nghĩa cho 1 từ, mỗi nghĩa có thể tách bằng dấu `;` khi lưu vào dữ liệu.
- Đã hiển thị câu ví dụ và nghĩa tiếng Việt rõ ràng hơn trong phần kết quả tra cứu.

## 8. Khuyến nghị mở rộng
- Thêm file dữ liệu chuẩn hơn với 100-200 từ tiếng Anh cơ bản.
- Thêm import từ CSV/JSON để dễ nạp dữ liệu lớn.
- Thêm tìm kiếm theo từ đồng nghĩa, tự động gợi ý khi nhập.
