package sobinda.moneybysobin.service;

import org.springframework.stereotype.Service;
import sobinda.moneybysobin.exceptions.InvalidTransactionExceptions;
import sobinda.moneybysobin.log.TransferLog;
import sobinda.moneybysobin.model.Card;
import sobinda.moneybysobin.model.CardTransfer;
import sobinda.moneybysobin.model.Amount;
import sobinda.moneybysobin.model.Verification;
import sobinda.moneybysobin.repository.TransferRepository;

@Service
public class TransferService {
    TransferRepository transferRepository;
    //1%
    private final int COMMISSION = 100;
    private final String SECRET_CODE = "0000";
    private TransferLog transferLog;

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
        return transferRepository.transferMoneyCardToCard(cardFrom, cardToNumber, amount);
    }

    public String confirmOperation(Verification verification) throws InvalidTransactionExceptions {
        return transferRepository.confirmOperation(verification);
    }
}
