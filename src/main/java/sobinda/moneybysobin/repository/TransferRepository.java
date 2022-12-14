package sobinda.moneybysobin.repository;

import org.springframework.stereotype.Repository;
import sobinda.moneybysobin.log.LogBuilder;
import sobinda.moneybysobin.log.TransferLog;
import sobinda.moneybysobin.model.Amount;
import sobinda.moneybysobin.model.Card;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class TransferRepository {
    TransferLog transferLog;
    Map<String, Card> mapStorage;

    Map<String, Card> map = Stream.of(
                    new AbstractMap.SimpleEntry<>(
                            "4558 4458 8558 4747",
                            new Card(
                                    "4558 4458 8558 4747",
                                    "08/23",
                                    "351",
                                    new Amount(50_000, "RUR"))),
                    new AbstractMap.SimpleEntry<>(
                            "4558 4458 8558 5555",
                            new Card(
                                    "4558 4458 8558 5555",
                                    "08/23",
                                    "352",
                                    new Amount(25_000, "RUR")))
            )
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


    public TransferRepository() {
        this.mapStorage = new ConcurrentHashMap<>(map);
        this.transferLog = TransferLog.getInstance();
    }

    public void transferMoneyCardToCard(Card cardFrom, String cardNumberTo, Amount amount) {
        // написать сверку данных по карте из базы, проверка баланса и существование карты приема денег
        // выбрать, что в итоге возвращаем

        //проверяем наличии карт в базе
        if (mapStorage.containsKey(cardFrom.getCardNumber()) && mapStorage.containsKey(cardNumberTo)) {
            //проверяем на совпадаение валюты перевода и валюты на карте
            if (mapStorage.get(cardNumberTo).getAmount().getCurrency().equals(amount.getCurrency())) {
                if (mapStorage.get(cardFrom.getCardNumber()).equals(cardFrom)) {
                    //Скорее всего нужно переместить локику в Класс карт на перевод с одной на вторую
                    int balanceFrom = mapStorage.get(cardFrom.getCardNumber()).getAmount().getValue();
                    if (balanceFrom >= amount.getValue()) {
                        mapStorage.get(cardFrom.getCardNumber()).getAmount().setValue(balanceFrom - amount.getValue());
                        int balanceTo = mapStorage.get(cardNumberTo).getAmount().getValue();
                        mapStorage.get(cardNumberTo).getAmount().setValue(balanceTo + amount.getValue());
                        LogBuilder logBuilder = new LogBuilder()
                                .setCardNumberFrom(cardFrom.getCardNumber())
                                .setCardNumberTo(cardNumberTo)
                                .setAmount(amount)
                                .setCommission(new Amount(amount.getValue() / 10, "RUR"))
                                .setResult("УСПЕХ");
                        transferLog.log(logBuilder);
                    }
                }
            }
            // пишем проверку баланса и перевод денег
            // возможно после удачной обработки требуется перекинуть на путь подтверждения операции
            // (или создание цепочки для этого условия)
        }

        //todo потом удалить
        for (Map.Entry<String, Card> entry : mapStorage.entrySet()) {
            System.out.println(entry);
        }
        // или возвращаем 0 или выполняем условие для выброса ошибки

    }

}
