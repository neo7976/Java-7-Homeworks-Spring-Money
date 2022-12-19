package sobinda.moneybysobin.repository;

import org.springframework.stereotype.Repository;
import sobinda.moneybysobin.exceptions.InvalidTransactionExceptions;
import sobinda.moneybysobin.log.LogBuilder;
import sobinda.moneybysobin.log.TransferLog;
import sobinda.moneybysobin.model.Amount;
import sobinda.moneybysobin.model.Card;
import sobinda.moneybysobin.model.Operation;
import sobinda.moneybysobin.model.Verification;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class TransferRepository {
    TransferLog transferLog;
    Map<String, Card> mapStorage;
    private final ConcurrentHashMap<String, Operation> cardTransactionsWaitConfirmOperation;

    //1%
    private final int COMMISSION = 100;
    private final String SECRET_CODE = "0000";

    Map<String, Card> map = Stream.of(
                    new AbstractMap.SimpleEntry<>(
                            "4558445885584747",
                            new Card(
                                    "4558445885584747",
                                    "08/23",
                                    "351",
                                    new Amount(50_000_00, "RUR"))),
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
        cardTransactionsWaitConfirmOperation = new ConcurrentHashMap<>();
    }

    public String transferMoneyCardToCard(Card cardFrom, String cardNumberTo, Amount amount) throws InvalidTransactionExceptions {
        //проверяем на наличие карт в базе, валюты на карте
        validCardToBase(cardFrom, cardNumberTo);
        validCurrencyCardTo(cardNumberTo, amount);

        int balanceFrom = mapStorage.get(cardFrom.getCardNumber()).getAmount().getValue();
        Amount commission = new Amount(amount.getValue() / COMMISSION, amount.getCurrency());
        int sumResult = commission.getValue() + amount.getValue();

        // пишем проверку баланса и перевод денег
        LogBuilder logBuilder;
        if (balanceFrom >= sumResult) {
//            mapStorage.get(cardFrom.getCardNumber()).getAmount().setValue(balanceFrom - sumResult);
//            int balanceTo = mapStorage.get(cardNumberTo).getAmount().getValue();
//            mapStorage.get(cardNumberTo).getAmount().setValue(balanceTo + amount.getValue());
            logBuilder = new LogBuilder()
                    .setCardNumberFrom(cardFrom.getCardNumber())
                    .setCardNumberTo(cardNumberTo)
                    .setAmount(amount)
                    .setCommission(commission)
                    .setResult("ЗАПРОС НА ПЕРЕВОД");
            String operationId = transferLog.log(logBuilder);
            cardTransactionsWaitConfirmOperation.put(operationId,
                    new Operation(
                            logBuilder.getCardNumberFrom(),
                            logBuilder.getCardNumberTo(),
                            logBuilder.getAmount(),
                            logBuilder.getCommission()));
            return "Ожидаем подтверждение на перевод операции №" + operationId;
        } else {
            logBuilder = new LogBuilder()
                    .setCardNumberFrom(cardFrom.getCardNumber())
                    .setCardNumberTo(cardNumberTo)
                    .setAmount(amount)
                    .setCommission(commission)
                    .setResult("НЕДОСТАТОЧНО СРЕДСТВ ДЛЯ ОПЕРАЦИИ");
            transferLog.log(logBuilder);
            throw new InvalidTransactionExceptions(logBuilder.getResult());
        }
        //c front получаем х100 значения (копейки)
        //todo требуется произвести оплату только после подтверждения операции. Создать отдельное поле для перезаписи, когда код совпал по id
//        return String.format("Статус перевод с карты \"%s\" на карту \"%s\"  в размере %s - [УСПЕХ]\n" +
//                        "Баланс Вашей карты: %d [%s]",
//                cardFrom.getCardNumber(),
//                cardNumberTo,
//                logBuilder.getAmount(),
//                mapStorage.get(cardFrom.getCardNumber()).getAmount().getValue() / 100,
//                amount.getCurrency());
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

    public String confirmOperation(Verification verification) throws InvalidTransactionExceptions {
        //todo убрать null, когда сможем получать id c front
        if (cardTransactionsWaitConfirmOperation.containsKey(verification.getOperationId()) || verification.getOperationId() == null) {
            Operation operation = cardTransactionsWaitConfirmOperation.get(verification.getOperationId());
            System.out.println("Найдена операция на очередь об оплате");
            if (verification.getCode().equals(cardTransactionsWaitConfirmOperation.get(verification.getOperationId()).getSecretCode())) {
                System.out.println("СЕКРЕТНЫЙ КОД СОВПАДАЕТ");
                //пишем логику, как вытаскиваем данные по id и перезаписываем в хранилище
                //заглушка, надо вернуть лог успеха
                //todo дописать по аналогии
                int balanceFrom = mapStorage.get(operation.getCardFromNumber()).getAmount().getValue();
                mapStorage.get(operation.getCardNumber()).getAmount().setValue(balanceFrom - sumResult);
                int balanceTo = mapStorage.get(cardNumberTo).getAmount().getValue();
                mapStorage.get(cardNumberTo).getAmount().setValue(balanceTo + amount.getValue());
                return "Успех";
            }
        } else {
            throw new InvalidTransactionExceptions("Такой операции нет");
        }
        //выбросить ошибку в сервисе или репозитории и удалить временные данные
        return "Попробуй ещё раз";
    }
}
