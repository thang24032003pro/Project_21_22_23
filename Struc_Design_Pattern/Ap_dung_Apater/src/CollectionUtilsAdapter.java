import com.codegym.CollectionUtils; // Import class từ thư viện .jar đã tích hợp
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CollectionUtilsAdapter {

    public int findMax(Set<Integer> numbers) {
        List<Integer> numberList = new ArrayList<>(numbers);

        CollectionUtils collectionUtils = new CollectionUtils();

        return collectionUtils.findMax(numberList);
    }
}