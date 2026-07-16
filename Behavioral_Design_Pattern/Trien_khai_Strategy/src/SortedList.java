import java.util.ArrayList;
import java.util.List;

public class SortedList {

    private SortStrategy strategy;
    private List<String> items = new ArrayList<>();

    public void setSortStrategy(SortStrategy strategy) {
        this.strategy = strategy;
    }

    public void add(String name) {
        items.add(name);
    }

    public void sort() {
        if (strategy == null) {
            throw new IllegalStateException("Chưa thiết lập thuật toán sắp xếp!");
        }
        // Gọi phương thức Generic của strategy, trình biên dịch tự khớp T là String
        strategy.sort(items);
    }
}