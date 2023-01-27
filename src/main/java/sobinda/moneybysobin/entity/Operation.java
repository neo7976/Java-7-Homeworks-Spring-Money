package sobinda.moneybysobin.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import sobinda.moneybysobin.log.LogBuilder;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

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
    @Column(name = "confirm")
    private boolean isConfirm;

    public Operation(LogBuilder logBuilder) {
        this.cardFromNumber = logBuilder.getCardNumberFrom();
        this.cardToNumber = logBuilder.getCardNumberTo();
        this.amount = logBuilder.getAmount();
        this.commission = logBuilder.getCommission();
        //todo сейчас front создает только 0000, потом сделать генерацию
        this.secretCode = "0000";
    }


    @Override
    public String toString() {
        return "cardFromNumber='" + cardFromNumber + '\'' +
                ", cardToNumber='" + cardToNumber + '\'' +
                ", amount=" + amount +
                ", commission=" + commission +
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
