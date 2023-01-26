package sobinda.moneybysobin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sobinda.moneybysobin.model.Card;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Integer> {

    Optional<Card> findByCardNumber(String cardNumber);

    @Query(value = "select c.amount.currency from Card c where c.cardNumber = :card and c.amount.currency = :currency")
    Optional<Card> findByCardNumberAndCurrency(@Param("card") String card, @Param("currency") String currency);
}
