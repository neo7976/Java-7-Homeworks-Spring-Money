package sobinda.moneybysobin.repository;

import org.springframework.stereotype.Repository;
import sobinda.moneybysobin.exceptions.InvalidTransactionExceptions;
import sobinda.moneybysobin.entity.Amount;
import sobinda.moneybysobin.entity.Card;
import sobinda.moneybysobin.entity.Operation;
import sobinda.moneybysobin.model.Verification;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class TransferRepository {
    //todo потом взять из БД и добавить как операцию
    private final AtomicInteger id = new AtomicInteger(0);
    private final CardRepository cardRepository;
    private final OperationRepository operationRepository;

    public TransferRepository(CardRepository cardRepository, OperationRepository operationRepository) {
        this.cardRepository = cardRepository;
        this.operationRepository = operationRepository;
    }

    public String transferMoneyCardToCard(Card cardFrom, String cardNumberTo, Amount amount) throws InvalidTransactionExceptions {
        //проверяем на наличие карт в базе, валюты на карте
        validCardToBase(cardFrom, cardNumberTo);
        validCurrencyCardTo(cardNumberTo, amount);

        return String.valueOf(id.incrementAndGet());
    }

    public void validCardToBase(Card cardFrom, String cardNumberTo) throws InvalidTransactionExceptions {

        if (cardRepository.findByCardNumber(cardNumberTo).isEmpty() || cardRepository.findByCardNumber(cardFrom.getCardNumber()).isEmpty()) {
            throw new InvalidTransactionExceptions("Одной из карт нет в базе данных");
        }
        if (!cardRepository.findByCardNumber(cardFrom.getCardNumber()).get().equals(cardFrom)) {
            throw new InvalidTransactionExceptions("Ошибка в доступе к карте списания");
        }
    }

    public void validCurrencyCardTo(String cardNumberTo, Amount amount) throws InvalidTransactionExceptions {

        if (cardRepository.findByCardNumberAndCurrency(cardNumberTo, amount.getCurrency()).isEmpty()) {
            throw new InvalidTransactionExceptions(String.format("Карта %s не имеет валютный счёт [%s] для перевода\n",
                    cardNumberTo,
                    amount.getCurrency()));
        }
    }

//    public List<Operation> confirmOperation(Verification verification) throws InvalidTransactionExceptions {
//        //todo убрать null и переделать из списка просто в операцию, когда сможем получать id c front
//        if (verification.getOperationId() == null) {
//            System.out.println("Сработала заглушка");
//            return new ArrayList<>(cardTransactionsWaitConfirmOperation.values());
//        } else if (cardTransactionsWaitConfirmOperation.containsKey(verification.getOperationId())) {
//            System.out.println("Найдена операция на очередь об оплате");
//            return Collections.singletonList(cardTransactionsWaitConfirmOperation.get(verification.getOperationId()));
//        }
//        //выбросить ошибку в сервисе или репозитории и удалить временные данные
//        throw new InvalidTransactionExceptions("Ошибочка, такого мы не предвидели!");
//    }

//    public void setCardTransactionsWaitConfirmOperation(String id, Operation operation) {
//        cardTransactionsWaitConfirmOperation.put(id, operation);
//    }
//
//    public void deleteWaitOperation(String operationId) {
//        cardTransactionsWaitConfirmOperation.remove(operationId);
//    }

    public Optional<BigDecimal> findByCardNumberAndAmountValue(String cardNumber) {
        return cardRepository.findByCardNumberAndAmountValue(cardNumber);
    }

    public void setBalanceCard(String cardNumber, BigDecimal bigDecimal) {
        cardRepository.setBalanceCard(cardNumber, bigDecimal);
    }
}
