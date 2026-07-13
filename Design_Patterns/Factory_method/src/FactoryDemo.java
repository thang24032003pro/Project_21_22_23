// Lớp trừu tượng Animal
abstract class Animal {
    public abstract String makeSound();
}

// Lớp Dog kế thừa từ Animal
class Dog extends Animal {
    @Override
    public String makeSound() {
        return "Woof";
    }
}

// Lớp Cat kế thừa từ Animal
class Cat extends Animal {
    @Override
    public String makeSound() {
        return "Meow";
    }
}

// Lớp Factory quản lý việc tạo đối tượng
class AnimalFactory {
    public Animal getAnimal(String type) {
        if ("canine".equals(type)) {
            return new Dog();
        } else {
            return new Cat();
        }
    }
}

// Lớp chính để chạy chương trình (Bắt buộc trùng tên với file)
public class FactoryDemo {
    public static void main(String[] args) {
        AnimalFactory animalFactory = new AnimalFactory();

        Animal a1 = animalFactory.getAnimal("feline");
        System.out.println("a1 sound: " + a1.makeSound());

        Animal a2 = animalFactory.getAnimal("canine");
        System.out.println("a2 sound: " + a2.makeSound());
    }
}