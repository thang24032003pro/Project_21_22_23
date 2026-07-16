public class OctObserver extends Observer {

    public OctObserver(Subject subject) {
        this.subject = subject;
        this.subject.add(this); // Tự động đăng ký chính mình vào danh sách của Subject
    }

    @Override
    public void update() {
        // Chuyển số nguyên sang hệ Bát phân (Octal) và in ra
        System.out.print(" Oct: " + Integer.toOctalString(subject.getState()));
    }
}