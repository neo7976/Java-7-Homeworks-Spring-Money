package sobinda.moneybysobin.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.bytebuddy.implementation.bind.annotation.Default;
import sobinda.moneybysobin.log.LogBuilder;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Objects;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Operation {
    @Id
//    @GeneratedValue(generator = "UUID")
//    @GenericGenerator(
//            name = "UUID",
//            strategy = "org.hibernate.id.UUIDGenerator"
//    )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Number_From_Card", length = 16)
    private String cardFromNumber;
    @Column(name = "Number_To_Card", length = 16)
    private String cardToNumber;
    @Embedded
    private Amount amount;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "commission_value")),
            @AttributeOverride(name = "currency", column = @Column(name = "commission_currency"))
    })
    private Amount commission;
    //Сделать генерацию кода, когда будет возможность отправить код и принять его через форму в front
    @Column(nullable = false)
    private String secretCode;
    private boolean confirm;

    public Operation(LogBuilder logBuilder) {
        this.cardFromNumber = logBuilder.getCardNumberFrom();
        this.cardToNumber = logBuilder.getCardNumberTo();
        this.amount = logBuilder.getAmount();
        this.commission = logBuilder.getCommission();
        //todo сейчас front создает только 0000, потом сделать генерацию
        this.secretCode = "0000";
        this.confirm = false;
    }


    @Override
    public String toString() {
        return "Operation{" +
                "id=" + id +
                ", cardFromNumber='" + cardFromNumber + '\'' +
                ", cardToNumber='" + cardToNumber + '\'' +
                ", amount=" + amount +
                ", commission=" + commission +
                ", secretCode='" + secretCode + '\'' +
                ", confirm=" + confirm +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operation operation = (Operation) o;
        return Objects.equals(cardFromNumber, operation.cardFromNumber) && Objects.equals(cardToNumber, operation.cardToNumber) && Objects.equals(amount, operation.amount) && Objects.equals(commission, operation.commission) && Objects.equals(secretCode, operation.secretCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardFromNumber, cardToNumber, amount, commission, secretCode);
    }
}
