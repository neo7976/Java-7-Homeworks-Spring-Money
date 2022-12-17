package sobinda.moneybysobin.repository;

import org.springframework.stereotype.Repository;
import sobinda.moneybysobin.exceptions.InvalidTransactionExceptions;
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
                            "4558445885584747",
                            new Card(
                                    "4558445885584747",
                                    "08/23",
                                    "351",
                                    new Amount(5_000_00, "RUR"))),
                    new AbstractMap.SimpleEntry<>(
                            "4558445885585555",
                            new Card(
                                    "4558445885585555",
                                    "08/23",
                                    "352",
                                    new Amount(25_000_00, "RUR")))
            )
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    public TransferRepository() {
        this.mapStorage = new ConcurrentHashMap<>(map);
        this.transferLog = TransferLog.getInstance();
    }

    public String transferMoneyCardToCard(Card cardFrom, String cardNumberTo, Amount amount) throws InvalidTransactionExceptions {
        //проверяем на наличие карт в базе, валюты на карте
        validCardToBase(cardFrom, cardNumberTo);
        validCurrencyCardTo(cardNumberTo, amount);

        int balanceFrom = mapStorage.get(cardFrom.getCardNumber()).getAmount().getValue();
        Amount commission = new Amount(amount.getValue() / 10, amount.getCurrency());
        int sumResult = commission.getValue() + amount.getValue();

        // пишем проверку баланса и перевод денег
        if (balanceFrom >= sumResult) {
            mapStorage.get(cardFrom.getCardNumber()).getAmount().setValue(balanceFrom - sumResult);
            int balanceTo = mapStorage.get(cardNumberTo).getAmount().getValue();
            mapStorage.get(cardNumberTo).getAmount().setValue(balanceTo + amount.getValue());
            LogBuilder logBuilder = new LogBuilder()
                    .setCardNumberFrom(cardFrom.getCardNumber())
                    .setCardNumberTo(cardNumberTo)
                    .setAmount(amount)
                    .setCommission(commission)
                    .setResult("УСПЕХ");
            transferLog.log(logBuilder);
        } else {
            LogBuilder logBuilder = new LogBuilder()
                    .setCardNumberFrom(cardFrom.getCardNumber())
                    .setCardNumberTo(cardNumberTo)
                    .setAmount(amount)
                    .setCommission(commission)
                    .setResult("НЕДОСТАТОЧНО СРЕДСТВ ДЛЯ ОПЕРАЦИИ");
            throw new InvalidTransactionExceptions(transferLog.log(logBuilder));
        }
        return String.format("Статус перевод с карты \"%s\" на карту \"%s\"  в размере %d [%s] - [УСПЕХ]\n" +
                        "Баланс Вашей карты: %d [%s]",
                cardFrom.getCardNumber(),
                cardNumberTo,
                amount.getValue()/100,
                amount.getCurrency(),
                mapStorage.get(cardFrom.getCardNumber()).getAmount().getValue()/100,
                amount.getCurrency());
    }

    public void validCardToBase(Card cardFrom, String cardNumberTo) throws InvalidTransactionExceptions {
        if (!mapStorage.containsKey(cardFrom.getCardNumber()) || !mapStorage.containsKey(cardNumberTo)) {
            throw new InvalidTransactionExceptions("Одной из карт нет в базе данных");
        }
        if (!mapStorage.get(cardFrom.getCardNumber()).equals(cardFrom)) {
            throw new InvalidTransactionExceptions("Ошибка в доступе к карте списания");
        }
    }

    public void validCurrencyCardTo(String cardNumberTo, Amount amount) throws InvalidTransactionExceptions {
        if (!mapStorage.get(cardNumberTo).getAmount().getCurrency().equals(amount.getCurrency())) {
            throw new InvalidTransactionExceptions(String.format("Карта %s не имеет валютный счёт [%s] для перевода\n",
                    cardNumberTo,
                    amount.getCurrency()));
        }
    }
}
