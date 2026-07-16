import java.util.Scanner;

public class ObserverDemo {
    public static void main(String[] args) {
        Subject sub = new Subject();

        new HexObserver(sub);
        new OctObserver(sub);
        new BinObserver(sub);

        Scanner scan = new Scanner(System.in);
        System.out.println("--- Chương trình chuyển đổi hệ số sử dụng Observer ---");

        for (int i = 0; i < 5; i++) {
            System.out.print("\nEnter a number: ");
            if (scan.hasNextInt()) {
                int number = scan.nextInt();
                System.out.print("Kết quả chuyển đổi của " + number + " ->");
                sub.setState(number); // Thay đổi trạng thái kích hoạt hành vi update() đồng loạt
                System.out.println(); // Xuống dòng cho lượt nhập tiếp theo
            } else {
                System.out.println("Vui lòng chỉ nhập số nguyên hợp lệ!");
                scan.next(); // Đọc và bỏ qua dữ liệu lỗi để tránh lặp vô hạn
                i--; // Trả lại lượt nhập nếu nhập sai định dạng
            }
        }
        scan.close();
    }
}