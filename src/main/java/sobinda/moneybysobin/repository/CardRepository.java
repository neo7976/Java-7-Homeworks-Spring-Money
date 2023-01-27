package sobinda.moneybysobin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sobinda.moneybysobin.entity.Card;
import sobinda.moneybysobin.entity.Operation;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;
@Transactional
public interface CardRepository extends JpaRepository<Card, Integer> {

    Optional<Card> findByCardNumber(String cardNumber);

    @Query(value = "select c.amount.currency from Card c where c.cardNumber = :card and c.amount.currency = :currency")
    Optional<Card> findByCardNumberAndCurrency(@Param("card") String card, @Param("currency") String currency);

    @Query(value = "select c.amount.value from Card c where c.cardNumber=:card")
    Optional<BigDecimal> findByCardNumberAndAmountValue(@Param("card") String card);

    @Query(value = "select c.amount.value from Card c where c.cardNumber= :card")
    Optional<BigDecimal> findByCardNumberBalance();


    @Modifying
    @Query(value = "update money_transfer.cards c set c.value=:value where c.number=:number", nativeQuery = true)
    void setBalanceCard(@Param("value") BigDecimal bigDecimal,
                                       @Param("number") String number);


}
