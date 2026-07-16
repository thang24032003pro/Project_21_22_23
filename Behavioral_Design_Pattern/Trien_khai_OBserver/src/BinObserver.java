public class BinObserver extends Observer {

    public BinObserver(Subject subject) {
        this.subject = subject;
        this.subject.add(this); // Tự động đăng ký chính mình vào danh sách của Subject
    }

    @Override
    public void update() {
        // Chuyển số nguyên sang hệ Nhị phân (Binary) và in ra
        System.out.print(" Bin: " + Integer.toBinaryString(subject.getState()));
    }
}