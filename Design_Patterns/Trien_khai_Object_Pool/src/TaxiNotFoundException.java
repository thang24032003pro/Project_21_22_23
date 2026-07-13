
public class TaxiNotFoundException extends RuntimeException {
    public TaxiNotFoundException(String message) {
        super(message);
        System.out.println(message);
    }
}