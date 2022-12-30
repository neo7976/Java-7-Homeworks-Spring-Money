package sobinda.moneybysobin.log;

import sobinda.moneybysobin.model.Amount;

import javax.validation.constraints.NotBlank;

public class LogBuilder {
    private String operationId;
    private String cardNumberFrom;
    private String cardNumberTo;
    private Amount amount;
    private Amount commission;
    private String result;

    public String getOperationId() {
        return operationId;
    }

    public LogBuilder setOperationId(String operationId) {
        this.operationId = operationId;
        return this;
    }

    public LogBuilder setCardNumberFrom(String cardNumberFrom) {
        this.cardNumberFrom = cardNumberFrom;
        return this;
    }

    public LogBuilder setCardNumberTo(String cardNumberTo) {
        this.cardNumberTo = cardNumberTo;
        return this;
    }

    public LogBuilder setAmount(Amount amount) {
        this.amount = amount;
        return this;
    }

    public LogBuilder setCommission(Amount commission) {
        this.commission = commission;
        return this;
    }

    public LogBuilder setResult(String result) {
        this.result = result;
        return this;
    }

    public String getCardNumberFrom() {
        return cardNumberFrom;
    }

    public String getCardNumberTo() {
        return cardNumberTo;
    }

    public Amount getAmount() {
        return amount;
    }

    public Amount getCommission() {
        return commission;
    }

    public String getResult() {
        return result;
    }

}
