import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomList {
    public List<Integer> generateList(int size, int min, int max) {
        List<Integer> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            list.add(random.nextInt((max - min) + 1) + min);
        }
        return list;
    }
}