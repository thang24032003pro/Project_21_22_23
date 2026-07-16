public class Client {
    private RandomListFacade facade;

    public Client() {
        this.facade = new RandomListFacade();
    }

    public void displayResult() {
        // Yêu cầu in danh sách gồm 10 số ngẫu nhiên trong khoảng từ 1 đến 50
        facade.printRandomEvenList(10, 1, 50);
    }
}