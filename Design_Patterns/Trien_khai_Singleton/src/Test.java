// --- BƯỚC 1, 2, 3, 4, 5: Lớp Quản Lý Sách Duy Nhất (Singleton) ---
class BookSingleton {
    private String author;
    private String title;
    private static BookSingleton book;
    private static boolean isLoanedOut;

    // Constructor private để ngăn việc tạo đối tượng bằng từ khóa "new" từ bên
    // ngoài
    private BookSingleton() {
        author = "Gamma, Helm, Johnson, and Vlissides";
        title = "Design Patterns";
        book = null;
        isLoanedOut = false;
    }

    // Phương thức mượn sách (Nếu chưa ai mượn thì trả về instance duy nhất)
    public static BookSingleton borrowBook() {
        if (!isLoanedOut) {
            if (book == null) {
                book = new BookSingleton();
            }
            isLoanedOut = true;
            return book;
        }
        return null; // Đã có người mượn thì trả về null
    }

    // Phương thức trả sách
    public void returnBook(BookSingleton bookReturned) {
        isLoanedOut = false;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthorAndTitle() {
        return getTitle() + " by " + getAuthor();
    }
}

// --- BƯỚC 6, 7, 8, 9: Lớp Người Mượn Sách ---
class BookBorrower {
    private BookSingleton borrowedBook;
    private boolean haveBook = false;

    // Thực hiện hành động mượn sách
    public void borrowBook() {
        borrowedBook = BookSingleton.borrowBook();

        if (borrowedBook == null) {
            haveBook = false;
        } else {
            haveBook = true;
        }
    }

    // Lấy thông tin sách đã mượn
    public String getAuthorAndTitle() {
        if (haveBook) {
            return borrowedBook.getAuthorAndTitle();
        }
        return "I don't have the book";
    }

    // Thực hiện hành động trả sách
    public void returnBook() {
        if (haveBook) {
            borrowedBook.returnBook(borrowedBook);
            haveBook = false; // Cập nhật lại trạng thái cá nhân sau khi trả
        }
    }
}

// --- BƯỚC 10: Lớp Test chứa hàm main() chạy chương trình (Bắt buộc trùng tên
// file) ---
public class Test {
    public static void main(String[] args) {
        System.out.println("BEGIN TESTING SINGLETON PATTERN");

        // Khởi tạo 2 người mượn sách
        BookBorrower bookBorrower1 = new BookBorrower();
        BookBorrower bookBorrower2 = new BookBorrower();

        // Người 1 mượn sách -> Thành công
        bookBorrower1.borrowBook();
        System.out.println("BookBorrower1 asked to borrow the book");
        System.out.println("BookBorrower1 Author and Title: ");
        System.out.println(bookBorrower1.getAuthorAndTitle());

        // Người 2 mượn sách -> Thất bại (Do người 1 chưa trả)
        bookBorrower2.borrowBook();
        System.out.println("BookBorrower2 asked to borrow the book");
        System.out.println("BookBorrower2 Author and Title: ");
        System.out.println(bookBorrower2.getAuthorAndTitle());

        // Người 1 trả sách
        bookBorrower1.returnBook();
        System.out.println("BookBorrower1 returned the book");

        // Người 2 thử mượn lại -> Thành công
        bookBorrower2.borrowBook();
        System.out.println("BookBorrower2 Author and Title: ");
        System.out.println(bookBorrower2.getAuthorAndTitle());

        System.out.println("END TESTING SINGLETON PATTERN");
    }
}