package sobinda.moneybysobin.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Repository;
import sobinda.moneybysobin.exceptions.InvalidTransactionExceptions;
import sobinda.moneybysobin.entity.Amount;
import sobinda.moneybysobin.entity.Card;
import sobinda.moneybysobin.entity.Operation;
import sobinda.moneybysobin.log.LogBuilder;
import sobinda.moneybysobin.model.Verification;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class TransferRepository {
    //todo потом взять из БД и добавить как операцию
    private final AtomicInteger id = new AtomicInteger(0);
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private OperationRepository operationRepository;

//    public TransferRepository(CardRepository cardRepository, OperationRepository operationRepository) {
//        this.cardRepository = cardRepository;
//        this.operationRepository = operationRepository;
//    }

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

    public List<Operation> confirmOperation(Verification verification) throws InvalidTransactionExceptions {
        //todo убрать null и переделать из списка просто в операцию, когда сможем получать id c front
        var resultOperation = operationRepository.findByIdAndSecretCode(
                Integer.valueOf(verification.getOperationId())
                , verification.getCode());
        if (verification.getOperationId() == null) {
            System.out.println("Сработала заглушка");
            //todo заглушка, чтобы завершить все операции из-за бага на фронте
            return operationRepository.findAllByConfirm();
        } else if (resultOperation.isPresent()) {
            System.out.println("Найдена операция на очередь об оплате");
            return Collections.singletonList(resultOperation.get());
        }
        //выбросить ошибку в сервисе или репозитории и удалить временные данные
        throw new InvalidTransactionExceptions("Ошибочка, такого мы не предвидели!");
    }

    public Optional<BigDecimal> findByCardNumberAndAmountValue(String cardNumber) {
        return cardRepository.findByCardNumberAndAmountValue(cardNumber);
    }

    public void setBalanceCard(String cardNumber, BigDecimal bigDecimal) {
        cardRepository.setBalanceCard(cardNumber, bigDecimal);
    }

    public String saveOperationRepository(LogBuilder logBuilder) {
        Operation operation = Operation.builder()
                .cardFromNumber(logBuilder.getCardNumberFrom())
                .cardToNumber(logBuilder.getCardNumberTo())
                .commission(logBuilder.getCommission())
                .amount(logBuilder.getAmount())
                .secretCode("0000")
                .build();
        return String.valueOf(operationRepository.save(operation).getId());
    }
}
