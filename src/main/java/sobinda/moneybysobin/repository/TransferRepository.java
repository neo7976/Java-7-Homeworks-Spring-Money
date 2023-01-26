package sobinda.moneybysobin.repository;

import org.springframework.stereotype.Repository;
import sobinda.moneybysobin.exceptions.InvalidTransactionExceptions;
import sobinda.moneybysobin.model.Amount;
import sobinda.moneybysobin.model.Card;
import sobinda.moneybysobin.model.Operation;
import sobinda.moneybysobin.model.Verification;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class TransferRepository {
    private final AtomicInteger id = new AtomicInteger(0);
    private final CardRepository cardRepository;
    //    private Map<String, Card> mapStorage;
    private ConcurrentHashMap<String, Operation> cardTransactionsWaitConfirmOperation;


//    Map<String, Card> map = Stream.of(
//                    new AbstractMap.SimpleEntry<>(
//                            "4558445885584747",
//                            new Card(
//                                    "4558445885584747",
//                                    "08/23",
//                                    "351",
//                                    new Amount(new BigDecimal(50_000_00), "RUR"))),
//                    new AbstractMap.SimpleEntry<>(
//                            "4558445885585555",
//                            new Card(
//                                    "4558445885585555",
//                                    "08/23",
//                                    "352",
//                                    new Amount(new BigDecimal(25_000_00), "RUR")))
//            )
//            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    public TransferRepository(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
//        this.mapStorage = new ConcurrentHashMap<>(map);
        cardTransactionsWaitConfirmOperation = new ConcurrentHashMap<>();
    }

    public String transferMoneyCardToCard(Card cardFrom, String cardNumberTo, Amount amount) throws InvalidTransactionExceptions {
        //проверяем на наличие карт в базе, валюты на карте
        validCardToBase(cardFrom, cardNumberTo);
        validCurrencyCardTo(cardNumberTo, amount);

        return String.valueOf(id.incrementAndGet());
    }

    public void validCardToBase(Card cardFrom, String cardNumberTo) throws InvalidTransactionExceptions {
//        if (!mapStorage.containsKey(cardFrom.getCardNumber()) || !mapStorage.containsKey(cardNumberTo)) {
//            throw new InvalidTransactionExceptions("Одной из карт нет в базе данных");
//        }
//        if (!mapStorage.get(cardFrom.getCardNumber()).equals(cardFrom)) {
//            throw new InvalidTransactionExceptions("Ошибка в доступе к карте списания");
//        }
//    }
        if (cardRepository.findByCardNumber(cardNumberTo).isEmpty() || cardRepository.findByCardNumber(cardFrom.getCardNumber()).isEmpty()) {
            throw new InvalidTransactionExceptions("Одной из карт нет в базе данных");
        }
        if (!cardRepository.findByCardNumber(cardFrom.getCardNumber()).equals(cardFrom)) {
            throw new InvalidTransactionExceptions("Ошибка в доступе к карте списания");
        }
    }

    public void validCurrencyCardTo(String cardNumberTo, Amount amount) throws InvalidTransactionExceptions {
//        if (!mapStorage.get(cardNumberTo).getAmount().getCurrency().equals(amount.getCurrency())) {
//            throw new InvalidTransactionExceptions(String.format("Карта %s не имеет валютный счёт [%s] для перевода\n",
//                    cardNumberTo,
//                    amount.getCurrency()));
//        }
        if (cardRepository.findByCardNumberAndCurrency(cardNumberTo, amount.getCurrency()).isEmpty()) {
            throw new InvalidTransactionExceptions(String.format("Карта %s не имеет валютный счёт [%s] для перевода\n",
                    cardNumberTo,
                    amount.getCurrency()));
        }
    }

    public List<Operation> confirmOperation(Verification verification) throws InvalidTransactionExceptions {
        //todo убрать null и переделать из списка просто в операцию, когда сможем получать id c front
        if (verification.getOperationId() == null) {
            System.out.println("Сработала заглушка");
            return new ArrayList<>(cardTransactionsWaitConfirmOperation.values());
        } else if (cardTransactionsWaitConfirmOperation.containsKey(verification.getOperationId())) {
            System.out.println("Найдена операция на очередь об оплате");
            return Collections.singletonList(cardTransactionsWaitConfirmOperation.get(verification.getOperationId()));
        }
        //выбросить ошибку в сервисе или репозитории и удалить временные данные
        throw new InvalidTransactionExceptions("Ошибочка, такого мы не предвидели!");
    }

//    public Map<String, Card> getMapStorage() {
//        return mapStorage;
//    }

//    public void setMapStorage(String cardNumber, Card card) {
//        mapStorage.put(cardNumber, card);
//    }

    public void setCardTransactionsWaitConfirmOperation(String id, Operation operation) {
        cardTransactionsWaitConfirmOperation.put(id, operation);
    }

    public void deleteWaitOperation(String operationId) {
        cardTransactionsWaitConfirmOperation.remove(operationId);
    }
}
