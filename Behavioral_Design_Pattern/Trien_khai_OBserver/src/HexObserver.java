public class HexObserver extends Observer {

    public HexObserver(Subject subject) {
        this.subject = subject;
        this.subject.add(this); // Tự động đăng ký chính mình vào danh sách của Subject
    }

    @Override
    public void update() {
        // Chuyển số nguyên sang hệ Thập lục phân (Hexadecimal) và in ra
        System.out.print(" Hex: " + Integer.toHexString(subject.getState()).toUpperCase());
    }
}