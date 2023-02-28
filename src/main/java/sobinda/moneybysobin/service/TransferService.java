package sobinda.moneybysobin.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sobinda.moneybysobin.entity.Amount;
import sobinda.moneybysobin.entity.Card;
import sobinda.moneybysobin.entity.Operation;
import sobinda.moneybysobin.exceptions.InvalidTransactionExceptions;
import sobinda.moneybysobin.model.*;
import sobinda.moneybysobin.repository.TransferRepository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransferService {
    private final TransferRepository transferRepository;
    //1%
    private final int COMMISSION = 100;
    private final String SECRET_CODE = "0000";


    @Transactional
    public String transferMoneyCardToCard(CardTransfer cardTransfer) throws InvalidTransactionExceptions {
        Card cardFrom = new Card(
                cardTransfer.getCardFromNumber(),
                cardTransfer.getCardFromValidTill(),
                cardTransfer.getCardFromCVV()
        );

        String cardToNumber = cardTransfer.getCardToNumber();
        Amount amount = new Amount(cardTransfer.getAmount().getValue(),
                cardTransfer.getAmount().getCurrency());
        if (cardFrom.getCardNumber().equals(cardToNumber)) {
            throw new InvalidTransactionExceptions("Карта для перевода и получения совпадает!\n" +
                    "Проверьте входные данные ещё раз");
        }
        System.out.println(transferRepository.transferMoneyCardToCard(cardFrom, cardToNumber, amount));

        var balanceFrom = transferRepository.findByCardNumberAndAmountValue(cardFrom.getCardNumber()).get();
        Amount commission = new Amount(amount.getValue().divide(BigDecimal.valueOf(COMMISSION)), amount.getCurrency());
        BigDecimal sumResult = commission.getValue().add(amount.getValue());

        Operation operation = Operation.builder()
                .cardFromNumber(cardFrom.getCardNumber())
                .cardToNumber(cardToNumber)
                .amount(amount)
                .commission(commission)
                .secretCode(SECRET_CODE)
                .build();
        if (balanceFrom.compareTo(sumResult) >= 0) {
            String operationId = transferRepository.saveOperationRepository(operation);
            log.info("Запрос на перевод с карты: {}", operation.getCardFromNumber());
            return operationId;
        } else {
            log.info("Перевод отклонен с карты: {}", operation.getCardFromNumber());
            throw new InvalidTransactionExceptions("Недостаточно средств для списания денежных средств");
        }
    }

    @Transactional
    public String confirmOperation(Verification verification) throws InvalidTransactionExceptions {
        List<Operation> operations = transferRepository.confirmOperation(verification);
        for (Operation operation : operations) {
            return operationWithMoney(verification, operation);
        }
        throw new InvalidTransactionExceptions("Ошибочка, такого мы не предвидели!");
    }


    private String operationWithMoney(Verification verification, Operation operation) throws InvalidTransactionExceptions {
        if (verification.getCode().equals(operation.getSecretCode())) {
            System.out.println("СЕКРЕТНЫЙ КОД СОВПАДАЕТ");
            BigDecimal balanceFrom = transferRepository.findByCardNumberAndAmountValue(operation.getCardFromNumber()).get();
            BigDecimal sumResult = operation.getCommission().getValue().add(operation.getAmount().getValue());
            LogBuilder logBuilder = new LogBuilder()
                    .setOperationId(String.valueOf(operation.getId()))
                    .setCardNumberFrom(operation.getCardFromNumber())
                    .setCardNumberTo(operation.getCardToNumber())
                    .setAmount(operation.getAmount())
                    .setCommission(operation.getCommission());

            if (balanceFrom.compareTo(sumResult) >= 0) {
                //устанавливаем новый баланс на нашу исходную карту
                transferRepository.setBalanceCard(operation.getCardFromNumber(), balanceFrom.subtract(sumResult));
                BigDecimal balanceTo = transferRepository.findByCardNumberAndAmountValue(operation.getCardToNumber()).get();

                //устанавливаем новый баланс на карту перевода
                transferRepository.setBalanceCard(operation.getCardToNumber(), balanceTo.add(operation.getAmount().getValue()));
                logBuilder.setResult(String.format("ТРАНЗАКЦИЯ ПРОШЛА УСПЕШНО! ВАШ БАЛАНС СОСТАВЛЯЕТ: %.2f %s",
                        transferRepository.findByCardNumberAndAmountValue(operation.getCardFromNumber()).get().divide(new BigDecimal(100)),
                        operation.getAmount().getCurrency()));

                transferLog.log(logBuilder);
                //todo удалить заглушку и сделать для всех операций удаление, когда будем получать с front id операции
//                if (verification.getOperationId() != null) {
//                    transferRepository.deleteWaitOperation(verification.getOperationId());
//                }

                //изменение операции на true
//                if (!transferRepository.setOperationConfirm(operation.getId()))
//                    throw new InvalidTransactionExceptions("Не получилось изменить статус операции");
                transferRepository.setOperationConfirm(operation.getId());
            } else {
                logBuilder.setResult("НЕДОСТАТОЧНО СРЕДСТВ ДЛЯ ОПЕРАЦИИ");
                transferLog.log(logBuilder);
                throw new InvalidTransactionExceptions(logBuilder.getResult());
            }
        } else {
            throw new InvalidTransactionExceptions("Такой операции нет");
        }
        return "Успешная транзакция №" + verification.getOperationId();
    }
}
