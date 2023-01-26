package sobinda.moneybysobin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;


@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Data
public class Amount {
    private BigDecimal value;
    private String currency;

    @Override
    public String toString() {
        return String.format("[%.2f %s]", value, currency);
    }
}



