package sobinda.moneybysobin.service;

import org.springframework.stereotype.Service;
import sobinda.moneybysobin.exceptions.InvalidTransactionExceptions;
import sobinda.moneybysobin.log.LogBuilder;
import sobinda.moneybysobin.log.TransferLog;
import sobinda.moneybysobin.model.*;
import sobinda.moneybysobin.repository.TransferRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransferService {
    TransferRepository transferRepository;
    //1%
    private final int COMMISSION = 100;
    private final String SECRET_CODE = "0000";
    private final TransferLog transferLog;

    public TransferService(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
        this.transferLog = TransferLog.getInstance();
    }

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
        String operationId = transferRepository.transferMoneyCardToCard(cardFrom, cardToNumber, amount);

        BigDecimal balanceFrom = transferRepository.getMapStorage().get(cardFrom.getCardNumber()).getAmount().getValue();
        Amount commission = new Amount(amount.getValue().divide(BigDecimal.valueOf(COMMISSION)), amount.getCurrency());
        BigDecimal sumResult = commission.getValue().add(amount.getValue());

        // пишем проверку баланса и перевод денег
        LogBuilder logBuilder = new LogBuilder()
                .setOperationId(operationId)
                .setCardNumberFrom(cardFrom.getCardNumber())
                .setCardNumberTo(cardToNumber)
                .setAmount(amount)
                .setCommission(commission);
        if (balanceFrom.compareTo(sumResult) >= 0) {
            logBuilder.setResult("ЗАПРОС НА ПЕРЕВОД");
            transferRepository.setCardTransactionsWaitConfirmOperation(operationId, new Operation(logBuilder));
            transferLog.log(logBuilder);
            return "Ожидаем подтверждение на перевод операции №" + operationId;
        } else {
            logBuilder.setResult("НЕДОСТАТОЧНО СРЕДСТВ ДЛЯ ОПЕРАЦИИ");
            transferLog.log(logBuilder);
            throw new InvalidTransactionExceptions(logBuilder.getResult());
        }
    }


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
            BigDecimal balanceFrom = transferRepository.getMapStorage().get(operation.getCardFromNumber()).getAmount().getValue();
            BigDecimal sumResult = operation.getCommission().getValue().add(operation.getAmount().getValue());
            LogBuilder logBuilder = new LogBuilder()
                    .setOperationId(verification.getOperationId())
                    .setCardNumberFrom(operation.getCardFromNumber())
                    .setCardNumberTo(operation.getCardToNumber())
                    .setAmount(operation.getAmount())
                    .setCommission(operation.getCommission());
            if (balanceFrom.compareTo(sumResult) >= 0) {
                //устанавливаем новый баланс на нашу исходную карту
                transferRepository.getMapStorage().get(operation.getCardFromNumber()).getAmount().setValue(balanceFrom.subtract(sumResult));
                BigDecimal balanceTo = transferRepository.getMapStorage().get(operation.getCardToNumber()).getAmount().getValue();

                //устанавливаем новый баланс на карту перевода
                transferRepository.getMapStorage().get(operation.getCardToNumber()).getAmount().setValue(balanceTo.add(operation.getAmount().getValue()));
                logBuilder.setResult(String.format("ТРАНЗАКЦИЯ ПРОШЛА УСПЕШНО! ВАШ БАЛАНС СОСТАВЛЯЕТ: %.2f %s",
                        transferRepository.getMapStorage().get(operation.getCardFromNumber()).getAmount().getValue().divide(new BigDecimal(100)),
                        operation.getAmount().getCurrency()));
                transferLog.log(logBuilder);
                return "Успешная транзакция №" + verification.getOperationId();
            } else {
                logBuilder.setResult("НЕДОСТАТОЧНО СРЕДСТВ ДЛЯ ОПЕРАЦИИ");
                transferLog.log(logBuilder);
                throw new InvalidTransactionExceptions(logBuilder.getResult());
            }
        } else {
            throw new InvalidTransactionExceptions("Такой операции нет");
        }
    }
}
