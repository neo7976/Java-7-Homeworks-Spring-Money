package sobinda.moneybysobin.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sobinda.moneybysobin.entity.Amount;
import sobinda.moneybysobin.entity.Card;
import sobinda.moneybysobin.entity.Operation;
import sobinda.moneybysobin.exceptions.InvalidTransactionExceptions;
import sobinda.moneybysobin.model.Verification;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class TransferRepository {
    private final CardRepository cardRepository;
    private final OperationRepository operationRepository;

    public TransferRepository(CardRepository cardRepository, OperationRepository operationRepository) {
        this.cardRepository = cardRepository;
        this.operationRepository = operationRepository;
    }

    public boolean transferMoneyCardToCard(Card cardFrom, String cardNumberTo, Amount amount) throws InvalidTransactionExceptions {
        //проверяем на наличие карт в базе, валюты на карте
        boolean validFirst = validCardToBase(cardFrom, cardNumberTo);
        boolean validSecond = validCurrencyCardTo(cardNumberTo, amount);
        return validFirst && validSecond;
    }

    public boolean validCardToBase(Card cardFrom, String cardNumberTo) throws InvalidTransactionExceptions {

        if (cardRepository.findByCardNumber(cardNumberTo).isEmpty() || cardRepository.findByCardNumber(cardFrom.getCardNumber()).isEmpty()) {
            throw new InvalidTransactionExceptions("Одной из карт нет в базе данных");
        }
        if (!cardRepository.findByCardNumber(cardFrom.getCardNumber()).get().equals(cardFrom)) {
            throw new InvalidTransactionExceptions("Ошибка в доступе к карте списания");
        }
        return true;
    }

    public boolean validCurrencyCardTo(String cardNumberTo, Amount amount) throws InvalidTransactionExceptions {
        if (cardRepository.findByCardNumberAndCurrency(cardNumberTo, amount.getCurrency()).isEmpty()) {
            throw new InvalidTransactionExceptions(String.format("Карта %s не имеет валютный счёт [%s] для перевода\n",
                    cardNumberTo,
                    amount.getCurrency()));
        }
        return true;
    }

    public List<Operation> confirmOperation(Verification verification) throws InvalidTransactionExceptions {
        //todo убрать null и переделать из списка просто в операцию, когда сможем получать id c front
        if (verification.getOperationId() == null) {
            log.info("Сработала заглушка на null, возвращаем список всех операций");
            return operationRepository.findAllByConfirm();

        } else {
            var resultOperation = operationRepository.findByIdAndSecretCode(
                    Integer.valueOf(verification.getOperationId())
                    , verification.getCode());
            if (resultOperation.isPresent()) {
                log.info("Найдена операция '{}' на оплату", resultOperation.get().getId());
                return Collections.singletonList(resultOperation.get());
            }
            //todo выбросить ошибку в сервисе или репозитории и удалить временные данные
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

    public String saveOperationRepository(Operation operation) {
        return String.valueOf(operationRepository.save(operation).getId());
    }

    public void setOperationConfirm(int id) {
        operationRepository.setConfirmTrue(id);
//        System.out.println("РЕЗУЛЬТАТ: " + operationRepository.findById(id));
//        return operationRepository.findById(id).get().isConfirm();
    }
}
