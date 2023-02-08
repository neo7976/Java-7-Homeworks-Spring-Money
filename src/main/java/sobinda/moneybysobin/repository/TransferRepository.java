package sobinda.moneybysobin.repository;

import org.apache.catalina.valves.rewrite.InternalRewriteMap;
import org.springframework.stereotype.Repository;
import sobinda.moneybysobin.exceptions.InvalidTransactionExceptions;
import sobinda.moneybysobin.entity.Amount;
import sobinda.moneybysobin.entity.Card;
import sobinda.moneybysobin.entity.Operation;
import sobinda.moneybysobin.log.LogBuilder;
import sobinda.moneybysobin.model.Verification;

import java.math.BigDecimal;
import java.util.*;

@Repository
public class TransferRepository {

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
        return "Карты имеются в базе данных";
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

        if (verification.getOperationId() == null) {
            System.out.println("Сработала заглушка");
            //todo заглушка, чтобы завершить все операции из-за бага на фронте
            return operationRepository.findAllByConfirm();
        } else {
            var resultOperation = operationRepository.findByIdAndSecretCode(
                    Integer.valueOf(verification.getOperationId())
                    , verification.getCode());
            if (resultOperation.isPresent()) {
                System.out.println("Найдена операция на очередь об оплате");
                return Collections.singletonList(resultOperation.get());
            }
            //выбросить ошибку в сервисе или репозитории и удалить временные данные
            throw new InvalidTransactionExceptions("Ошибочка, такого мы не предвидели!");
        }
    }

    public Optional<BigDecimal> findByCardNumberAndAmountValue(String cardNumber) {
        return cardRepository.findByCardNumberAndAmountValue(cardNumber);
    }

    public boolean setBalanceCard(String cardNumber, BigDecimal bigDecimal) {
        if (bigDecimal.compareTo(BigDecimal.ZERO) >= 0) {
            cardRepository.setBalanceCard(bigDecimal, cardNumber);
            return findByCardNumberAndAmountValue(cardNumber).get().compareTo(bigDecimal) == 0;
        }
        return false;
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

    public boolean setOperationConfirm(int id, int confirm) {
        operationRepository.setConfirmTrue(id, confirm);
        System.out.println("РЕЗУЛЬТАТ: " + operationRepository.findById(id));
        return operationRepository.findById(id).get().getConfirm() == 1;
    }
}
