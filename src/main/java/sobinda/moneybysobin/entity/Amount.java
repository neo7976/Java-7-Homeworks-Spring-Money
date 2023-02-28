package sobinda.moneybysobin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Data
public class Amount {
    @Min(0)
    private BigDecimal value;
    @Column(length = 10)
    private String currency;

    @Override
    public String toString() {
        return String.format("[%.2f %s]", value, currency);
    }

    public static Amount addCommission(Amount amount, BigDecimal commissionPercentage) {
        if (commissionPercentage.compareTo(BigDecimal.ZERO) == 0) {
            return new Amount(amount.getValue(), amount.getCurrency());
        }
        return new Amount(
                amount.getValue().divide(commissionPercentage),
                amount.getCurrency());
    }
}



