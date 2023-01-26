package sobinda.moneybysobin.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sobinda.moneybysobin.model.Card;

import java.math.BigDecimal;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Integer> {

    Optional<Card> findByCardNumber(String cardNumber);

    @Query(value = "select c.amount.currency from Card c where c.cardNumber = :card and c.amount.currency = :currency")
    Optional<Card> findByCardNumberAndCurrency(@Param("card") String card, @Param("currency") String currency);

    @Query(value = "select c.amount.value from Card c where c.cardNumber=:card")
    Optional<BigDecimal> findByCardNumberAndAmountValue(@Param("card") String card);

    @Query(value = "select c.amount.value from Card c where c.cardNumber= :card")
    Optional<BigDecimal> findByCardNumberBalance();

    @Query(value = "update money_transfer.cards c set c.value=:value where c.number=:number", nativeQuery = true)
    Optional<BigDecimal> setBalanceCard(@Param("number") String number,
                                        @Param("value") BigDecimal bigDecimal);

}
