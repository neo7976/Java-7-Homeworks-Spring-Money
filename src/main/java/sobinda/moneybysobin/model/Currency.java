package sobinda.moneybysobin.model;

public class Currency {
    private String name;
    private int balance;

    public Currency(String name, int balance) {
        this.name = name;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return balance;
    }

    public void setAmount(int amount) {
        this.balance = amount;
    }
}



