
public class TemplateMethodDemo {
    public static void main(String[] args) {
        System.out.println("=== PREPARING HAMBURGER MEAL ===");
        Meal meal1 = new HamburgerMeal();
        meal1.doMeal();

        System.out.println(); // Dòng trống ngăn cách giữa 2 bữa ăn

        System.out.println("=== PREPARING TACO MEAL ===");
        Meal meal2 = new TacoMeal();
        meal2.doMeal();
    }
}