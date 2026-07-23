import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

// --- Bước 1: Tạo lớp Bullet đại diện cho viên đạn ---
class Bullet {
    public static int count = 0;
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Bullet() {
        count++; // Đếm tổng số lượng đối tượng Bullet thực tế được tạo bằng từ khóa new
    }

    public void move() {
        position++;
    }
}

// --- Bước 2: Tạo lớp abstract MemoryPool ---
abstract class MemoryPool<T> {
    private LinkedList<T> free_items = new LinkedList<>();

    public void freeItem(T item) {
        free_items.add(item);
    }

    protected abstract T allocate();

    public T newItem() {
        T out = null;
        if (free_items.size() == 0) {
            out = allocate();
        } else {
            out = free_items.getFirst();
            free_items.removeFirst();
        }
        return out;
    }
}

// --- Bước 3: Tạo lớp BulletPool kế thừa từ MemoryPool ---
class BulletPool extends MemoryPool<Bullet> {
    @Override
    protected Bullet allocate() {
        return new Bullet();
    }
}

// --- Bước 4: Tạo lớp Gun mô phỏng khẩu súng ---
class Gun {
    private int bulletCount = 1000;

    // Bắn súng sử dụng các viên đạn từ trong Pool (Có tái sử dụng đạn)
    public void fireInPool() {
        BulletPool pool = new BulletPool();
        List<Bullet> plist = new ArrayList<>();
        for (int i = 0; i < bulletCount; i++) {
            Bullet p = pool.newItem();
            p.setPosition(0);
            plist.add(p);
            for (int j = 0; j < plist.size(); j++) {
                Bullet pp = plist.get(j);
                pp.move();
                System.out.print("-" + pp.getPosition());
                if (pp.getPosition() == 10) {
                    pool.freeItem(pp);
                    plist.remove(pp);
                    j--; // Đảm bảo chỉ mục vòng lặp không bị lỗi khi xóa phần tử khỏi ArrayList
                }
            }
            System.out.println();
        }
    }

    // Bắn súng thông thường KHÔNG sử dụng Pool (Cứ bắn là tạo mới đạn)
    public void fire() {
        List<Bullet> plist = new ArrayList<>();
        for (int i = 0; i < bulletCount; i++) {
            Bullet p = new Bullet();
            p.setPosition(0);
            plist.add(p);
            for (int j = 0; j < plist.size(); j++) {
                Bullet pp = plist.get(j);
                pp.move();
                System.out.print("-" + pp.getPosition());
                if (pp.getPosition() == 10) {
                    plist.remove(pp);
                    j--; // Đảm bảo chỉ mục vòng lặp không bị lỗi khi xóa phần tử khỏi ArrayList
                }
            }
            System.out.println();
        }
    }
}

// --- Bước 5 & 6: Khởi chạy và kiểm tra ứng dụng ---
public class Main {
    public static void main(String[] args) {
        Gun gun = new Gun();
        System.out.println("Start");
        gun.fireInPool(); 
        // -------------------------------------------------------------

        System.out.println("Game over");
        System.out.println("Total bullet created: " + Bullet.count);
    }
}