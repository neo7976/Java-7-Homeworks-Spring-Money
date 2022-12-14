package sobinda.moneybysobin.repository;

import org.springframework.stereotype.Repository;
import sobinda.moneybysobin.model.Card;
import sobinda.moneybysobin.model.Currency;

@Repository
public class TransferRepository {
    public void transferMoneyCardToCard(Card cardFrom, String cardNumber, Currency currency) {
        // написать сверку данных по карте из базы, проверка баланса и существование карты приема денег
        // выбрать, что в итоге возвращаем
    }
    //Хранение данных по картам и переводы сумм
}
