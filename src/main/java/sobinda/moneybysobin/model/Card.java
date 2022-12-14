package sobinda.moneybysobin.model;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class Card{
    @NotNull
    private String cardNumber;
    @NotNull
    private String cardValidTill;
    @NotNull
    private String cardCVV;
    //Можно подумать над созданием списка разных валют на 1 карте
    private Currency currency;

    public Card(String cardNumber, String cardValidTill, String cardCVV, Currency currency) {
        this.cardNumber = cardNumber;
        this.cardValidTill = cardValidTill;
        this.cardCVV = cardCVV;
        this.currency = currency;
    }

    public Card(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Card() {
    }
    //    public Card(String cardNumber) {
//        this.cardNumber = cardNumber;
//    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardValidTill() {
        return cardValidTill;
    }

    public void setCardValidTill(String cardValidTill) {
        this.cardValidTill = cardValidTill;
    }

    public String getCardCVV() {
        return cardCVV;
    }

    public void setCardCVV(String cardCVV) {
        this.cardCVV = cardCVV;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }


}
