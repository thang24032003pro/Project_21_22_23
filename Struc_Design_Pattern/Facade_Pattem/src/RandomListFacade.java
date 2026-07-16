import java.util.List;

public class RandomListFacade {
    private RandomList randomList;
    private ListFilter listFilter;
    private ListPrinter listPrinter;

    // Khởi tạo các hệ thống con bên trong Facade
    public RandomListFacade() {
        this.randomList = new RandomList();
        this.listFilter = new ListFilter();
        this.listPrinter = new ListPrinter();
    }

    // Phương thức đóng gói toàn bộ quy trình phức tạp
    public void printRandomEvenList(int size, int min, int max) {
        // 1. Tạo danh sách ngẫu nhiên
        List<Integer> rawList = randomList.generateList(size, min, max);
        System.out.println("--- Danh sách ban đầu ---");
        listPrinter.printList(rawList);

        // 2. Loại bỏ các phần tử lẻ (chỉ giữ lại số chẵn)
        List<Integer> evenList = listFilter.filterOdd(rawList);

        // 3. Hiển thị danh sách số chẵn còn lại
        System.out.println("--- Danh sách số chẵn sau khi lọc ---");
        listPrinter.printList(evenList);
    }
}