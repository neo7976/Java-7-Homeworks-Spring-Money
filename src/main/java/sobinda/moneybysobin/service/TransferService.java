package sobinda.moneybysobin.service;

import org.springframework.stereotype.Service;
import sobinda.moneybysobin.model.Card;
import sobinda.moneybysobin.model.Currency;
import sobinda.moneybysobin.repository.TransferRepository;

@Service
public class TransferService {

    TransferRepository transferRepository;

    public TransferService(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    public void transferMoneyCardToCard(Card cardFrom, Card cardTo, Currency currency) {
        // Написать логику обработки и проверки карты
        // выбрать, что в итоге возвращаем
        transferRepository.transferMoneyCardToCard(cardFrom, cardTo.getCardNumber(), currency);

    }
}
