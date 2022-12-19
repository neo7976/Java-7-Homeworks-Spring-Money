package sobinda.moneybysobin.model;

import javax.validation.constraints.NotNull;

public class Operation {
    private String cardFromNumber;
    private String cardToNumber;
    private Amount amount;
    private Amount commission;
    //Сделать генерацию кода, когда будет возможность отправить код и принять его через форму в front
    private final String secretCode;

    public Operation(String cardFromNumber, String cardToNumber, Amount amount, Amount commission) {
        this.cardFromNumber = cardFromNumber;
        this.cardToNumber = cardToNumber;
        this.amount = amount;
        this.commission = commission;
        this.secretCode = "0000";
    }

    public String getCardFromNumber() {
        return cardFromNumber;
    }

    public void setCardFromNumber(String cardFromNumber) {
        this.cardFromNumber = cardFromNumber;
    }

    public String getCardToNumber() {
        return cardToNumber;
    }

    public void setCardToNumber(String cardToNumber) {
        this.cardToNumber = cardToNumber;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public Amount getCommission() {
        return commission;
    }

    public void setCommission(Amount commission) {
        this.commission = commission;
    }

    public String getSecretCode() {
        return secretCode;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "cardFromNumber='" + cardFromNumber + '\'' +
                ", cardToNumber='" + cardToNumber + '\'' +
                ", amount=" + amount +
                ", commission=" + commission +
                '}';
    }
}
