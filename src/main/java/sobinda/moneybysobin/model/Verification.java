package sobinda.moneybysobin.model;

import javax.validation.constraints.NotNull;

public class Verification {
    @NotNull
    private String code;

    public Verification() {

    }

    public Verification(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Секретный код: " + code;
    }
}
