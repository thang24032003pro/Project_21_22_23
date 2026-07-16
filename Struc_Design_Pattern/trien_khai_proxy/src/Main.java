// File: Main.java
public class Main {
    public static void main(String[] args) {
        Calculator proxy = new MathCalculatorProxy();

        // Trường hợp 1: Phép tính hợp lệ trong khoảng cho phép
        try {
            double result = proxy.add(1, 2);
            System.out.println("1 + 2 = " + result);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }

        // Trường hợp 2: Gây ra lỗi tràn số (Ngoại lệ sẽ được tung ra từ Proxy)
        try {
            System.out.println("Đang thực hiện phép tính vượt biên: 2 + Double.MAX_VALUE...");
            double result = proxy.add(2, Double.MAX_VALUE);
            System.out.println("Kết quả: " + result);
        } catch (RuntimeException e) {
            System.out.println("Bắt được ngoại lệ từ Proxy: " + e.getMessage()); // Sẽ nhảy vào đây
        }

        // Trường hợp 3: Thử nghiệm phép chia cho 0
        try {
            System.out.println("Đang thực hiện phép chia cho 0...");
            double result = proxy.div(10, 0);
            System.out.println("Kết quả: " + result);
        } catch (RuntimeException e) {
            System.out.println("Bắt được ngoại lệ từ Proxy: " + e.getMessage()); // Sẽ nhảy vào đây
        }
    }
}