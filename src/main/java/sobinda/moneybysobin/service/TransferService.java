package sobinda.moneybysobin.service;

import org.springframework.stereotype.Service;
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

    public void transferMoneyCardToCard1(Card cardFrom, Card cardTo, Amount currency) {
        // Написать логику обработки и проверки карты
        // выбрать, что в итоге возвращаем
        transferRepository.transferMoneyCardToCard(cardFrom, cardTo.getCardNumber(), currency);
    }

    public void transferMoneyCardToCard(CardTransfer cardTransfer) {
        // Написать логику обработки и проверки карты
        // выбрать, что в итоге возвращаем
//        transferRepository.transferMoneyCardToCard(cardFrom, cardTo.getCardNumber(), currency);
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
