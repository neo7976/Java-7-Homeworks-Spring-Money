package sobinda.moneybysobin.service;

import org.springframework.stereotype.Service;
import sobinda.moneybysobin.exceptions.InvalidTransactionExceptions;
import sobinda.moneybysobin.model.Card;
import sobinda.moneybysobin.model.CardTransfer;
import sobinda.moneybysobin.model.Amount;
import sobinda.moneybysobin.repository.TransferRepository;

@Service
public class TransferService {

    TransferRepository transferRepository;

    public TransferService(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    public void transferMoneyCardToCard(CardTransfer cardTransfer) throws InvalidTransactionExceptions {
        // Написать логику обработки и проверки карты
        // выбрать, что в итоге возвращаем
        Card cardFrom = new Card(
                cardTransfer.getCardFromNumber(),
                cardTransfer.getCardFromValidTill(),
                cardTransfer.getCardFromCVV()
        );

        String cardTo = cardTransfer.getCardToNumber();
        Amount amount = new Amount(cardTransfer.getAmount().getValue(),
                                    cardTransfer.getAmount().getCurrency());

        transferRepository.transferMoneyCardToCard(cardFrom, cardTo, amount);
    }
}
