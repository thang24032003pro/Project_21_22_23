public abstract class Meal {

    // Template method - được đánh dấu final để các lớp con không thể thay đổi thứ
    // tự quy trình
    public final void doMeal() {
        prepareIngredients();
        cook();
        eat();
        cleanUp();
    }

    public abstract void prepareIngredients();

    public abstract void cook();

    // Bước mặc định chung, lớp con có thể ghi đè (override) hoặc giữ nguyên
    public void eat() {
        System.out.println("Mmm, that's good");
    }

    public abstract void cleanUp();
}