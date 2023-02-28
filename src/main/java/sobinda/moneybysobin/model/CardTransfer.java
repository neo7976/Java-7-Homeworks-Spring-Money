package sobinda.moneybysobin.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sobinda.moneybysobin.entity.Amount;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardTransfer {
    @NotNull
    private String cardFromNumber;
    @NotNull
    private String cardFromValidTill;
    @NotNull
    private String cardFromCVV;
    @NotNull
    private String cardToNumber;
    private Amount amount;
}
