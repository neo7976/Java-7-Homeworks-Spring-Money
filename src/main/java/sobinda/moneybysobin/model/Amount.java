package sobinda.moneybysobin.model;

import java.math.BigDecimal;

public class Amount {
    private BigDecimal value;
    private String currency;

    public Amount(BigDecimal value, String currency) {
        this.value = value;
        this.currency = currency;
    }

    public Amount() {
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("[%.2f %s]", value, currency);
    }
}



