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
            logBuilder = new LogBuilder()
                    .setCardNumberFrom(cardFrom.getCardNumber())
                    .setCardNumberTo(cardNumberTo)
                    .setAmount(amount)
                    .setCommission(commission)
                    .setResult("ЗАПРОС НА ПЕРЕВОД");
            String operationId = transferLog.log(logBuilder);
            cardTransactionsWaitConfirmOperation.put(operationId, new Operation(logBuilder));
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
        Operation operation;
        if (verification.getOperationId() == null) {
            System.out.println("Сработала заглушка");
            for (Map.Entry<String, Operation> entry : cardTransactionsWaitConfirmOperation.entrySet()) {
                operation = entry.getValue();
                return operationWithMoney(verification, operation);
            }
        } else if (cardTransactionsWaitConfirmOperation.containsKey(verification.getOperationId())) {
            System.out.println("Найдена операция на очередь об оплате");
            operation = cardTransactionsWaitConfirmOperation.get(verification.getOperationId());
            return operationWithMoney(verification, operation);
        }
        //выбросить ошибку в сервисе или репозитории и удалить временные данные
        throw new InvalidTransactionExceptions("Ошибочка, такого мы не предвидели!");
    }

    private String operationWithMoney(Verification verification, Operation operation) throws InvalidTransactionExceptions {
        if (verification.getCode().equals(operation.getSecretCode())) {
            System.out.println("СЕКРЕТНЫЙ КОД СОВПАДАЕТ");
            int balanceFrom = mapStorage.get(operation.getCardFromNumber()).getAmount().getValue();
            mapStorage.get(operation.getCardFromNumber()).getAmount().setValue(balanceFrom - operation.getAmount().getValue() - operation.getCommission().getValue());
            int balanceTo = mapStorage.get(operation.getCardToNumber()).getAmount().getValue();
            mapStorage.get(operation.getCardToNumber()).getAmount().setValue(balanceTo + operation.getAmount().getValue());
            LogBuilder logBuilder = new LogBuilder()
                    .setCardNumberFrom(operation.getCardFromNumber())
                    .setCardNumberTo(operation.getCardToNumber())
                    .setAmount(operation.getAmount())
                    .setCommission(operation.getCommission())
                    .setResult(String.format("ТРАНЗАКЦИЯ ПРОШЛА УСПЕШНО! ВАШ БАЛАНС СОСТАВЛЯЕТ: %.2f %s",
                            (double) mapStorage.get(operation.getCardFromNumber()).getAmount().getValue() / 100,
                            operation.getAmount().getCurrency()));
            transferLog.log(logBuilder);
            return "Успешная транзакция №" + verification.getOperationId();
        } else {
            throw new InvalidTransactionExceptions("Такой операции нет");
        }
    }
}
