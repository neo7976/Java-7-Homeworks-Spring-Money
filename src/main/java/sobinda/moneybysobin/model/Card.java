package sobinda.moneybysobin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "cards")
@Data
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Number", length = 16, unique = true)
    private String cardNumber;
    @Column(name = "ValidTill", length = 5)
    private String cardValidTill;
    @Column(name = "CVV", length = 3)
    private String cardCVV;
    //Можно подумать над созданием списка разных валют на 1 карте
    @Embedded
    private Amount amount;

    public Card(String cardNumber, String cardValidTill, String cardCVV, Amount amount) {
        this.cardNumber = cardNumber;
        this.cardValidTill = cardValidTill;
        this.cardCVV = cardCVV;
        this.amount = amount;
    }

    public Card(String cardNumber, String cardValidTill, String cardCVV) {
        this.cardNumber = cardNumber;
        this.cardValidTill = cardValidTill;
        this.cardCVV = cardCVV;
    }

    @Override
    public String toString() {
        return String.format("Карта:\nНомер карты:%s\nМЕС/ГОД:%s\nCVV:%s\nБаланс:%s\n",
                cardNumber,
                cardValidTill,
                cardCVV,
                amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(cardNumber, card.cardNumber) && Objects.equals(cardValidTill, card.cardValidTill) && Objects.equals(cardCVV, card.cardCVV);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardNumber, cardValidTill, cardCVV);
    }
}
