package sobinda.moneybysobin.model;

import javax.validation.constraints.NotNull;

public class Verification {
    @NotNull
    private String code;
    private String operationId;

    public Verification() {

    }

    public Verification(String code, String operationId) {
        this.code = code;
        this.operationId = operationId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    @Override
    public String toString() {
        return "Секретный код: " + code +
                "Операция: " + operationId;
    }
}
