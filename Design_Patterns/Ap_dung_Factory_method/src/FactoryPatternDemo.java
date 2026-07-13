// --- Bước 1: Tạo Interface Shape ---
interface Shape {
    void draw();
}

// --- Bước 2: Tạo các lớp cụ thể cài đặt (implement) interface Shape ---
class Circle implements Shape {
    @Override
    public void draw() {
        System.out.println("Inside Circle::draw() method.");
    }
}

class Rectangle implements Shape {
    @Override
    public void draw() {
        System.out.println("Inside Rectangle::draw() method.");
    }
}

class Square implements Shape {
    @Override
    public void draw() {
        System.out.println("Inside Square::draw() method.");
    }
}

// --- Bước 3: Tạo lớp ShapeFactory để khởi tạo đối tượng dựa trên thông tin đầu vào ---
class ShapeFactory {
    // Phương thức factory để lấy đối tượng có kiểu Shape
    public Shape getShape(String shapeType) {
        if (shapeType == null) {
            return null;
        }
        
        // So sánh không phân biệt chữ hoa chữ thường (IgnoreCase)
        if (shapeType.equalsIgnoreCase("CIRCLE")) {
            return new Circle();
        } else if (shapeType.equalsIgnoreCase("RECTANGLE")) {
            return new Rectangle();
        } else if (shapeType.equalsIgnoreCase("SQUARE")) {
            return new Square();
        }
        
        return null;
    }
}

// --- Bước 4: Tạo lớp chứa hàm main() để chạy và kiểm tra chương trình ---
public class FactoryPatternDemo {
    public static void main(String[] args) {
        ShapeFactory shapeFactory = new ShapeFactory();

        // 1. Yêu cầu Factory tạo đối tượng Hình tròn (Circle)
        Shape shape1 = shapeFactory.getShape("CIRCLE");
        // Gọi phương thức draw của Circle
        if (shape1 != null) {
            shape1.draw();
        }

        // 2. Yêu cầu Factory tạo đối tượng Hình chữ nhật (Rectangle)
        Shape shape2 = shapeFactory.getShape("RECTANGLE");
        // Gọi phương thức draw của Rectangle
        if (shape2 != null) {
            shape2.draw();
        }

        // 3. Yêu cầu Factory tạo đối tượng Hình vuông (Square)
        Shape shape3 = shapeFactory.getShape("SQUARE");
        // Gọi phương thức draw của Square
        if (shape3 != null) {
            shape3.draw();
        }
    }
}