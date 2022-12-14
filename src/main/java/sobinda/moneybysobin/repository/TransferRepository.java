package sobinda.moneybysobin.repository;

import org.springframework.stereotype.Repository;
import sobinda.moneybysobin.model.Card;
import sobinda.moneybysobin.model.Currency;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class TransferRepository {
    Map<String, Card> mapStorage;

    Map<String, Card> map = Stream.of(
                    new AbstractMap.SimpleEntry<>(
                            "4558 4458 8558 4747",
                            new Card(
                                    "4558 4458 8558 4747",
                                    "08/23",
                                    "351",
                                    new Currency("Рубль", 50_000))),
                    new AbstractMap.SimpleEntry<>(
                            "4558 4458 8558 5555",
                            new Card(
                                    "4558 4458 8558 5555",
                                    "08/23",
                                    "352",
                                    new Currency("Рубль", 25_000)))
            )
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


    public TransferRepository() {
        this.mapStorage = new ConcurrentHashMap<>(map);
    }

    public void transferMoneyCardToCard(Card cardFrom, String cardNumberTo, Currency currency) {
        // написать сверку данных по карте из базы, проверка баланса и существование карты приема денег
        // выбрать, что в итоге возвращаем
        if (mapStorage.containsKey(cardFrom.getCardNumber()) && mapStorage.containsKey(cardNumberTo)) {
            //проверяем на совпадаение валюты перевода и валюты на карте
            if (mapStorage.get(cardNumberTo).getCurrency().getName().equals(currency.getName())) {

            }
            // пишем проверку баланса и перевод денег
            // возможно после удачной обработки требуется перекинуть на путь подтверждения операции
            // (или создание цепочки для этого условия)
        }
        // или возвращаем 0 или выполняем условие для выброса ошибки

    }
    //Хранение данных по картам и переводы сумм
}
