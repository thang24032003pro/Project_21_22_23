public class Main {

    public static void main(String[] args) {
        SortedList sortedList = new SortedList();
        sortedList.add("Java");
        sortedList.add("PHP");
        sortedList.add("C#");
        sortedList.add("Python");

        // Gán chiến lược QuickSort
        sortedList.setSortStrategy(new QuickSort());
        sortedList.sort();

        // Đổi sang chiến lược MergeSort lúc runtime
        sortedList.setSortStrategy(new MergeSort());
        sortedList.sort();
    }
}