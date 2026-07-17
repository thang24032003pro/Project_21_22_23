import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        // 1. Tạo một tập hợp Set chứa các số nguyên ngẫu nhiên
        Set<Integer> numbers = new HashSet<>();
        numbers.add(15);
        numbers.add(42);
        numbers.add(7);
        numbers.add(89);
        numbers.add(23);
        numbers.add(89); // Phần tử trùng lặp này sẽ tự động bị Set loại bỏ

        System.out.println("Tập hợp số nguyên đầu vào: " + numbers);

        // 2. Khởi tạo Adapter
        CollectionUtilsAdapter adapter = new CollectionUtilsAdapter();

        // 3. Tìm giá trị lớn nhất thông qua Adapter
        try {
            int maxValue = adapter.findMax(numbers);
            System.out.println("Giá trị lớn nhất tìm thấy: " + maxValue);
        } catch (Exception e) {
            System.out.println("Có lỗi xảy ra: " + e.getMessage());
        }
    }
}