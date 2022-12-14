package sobinda.moneybysobin.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sobinda.moneybysobin.model.Card;
import sobinda.moneybysobin.model.Currency;
import sobinda.moneybysobin.service.TransferService;

@RestController
public class TransferController {
    TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transfer")
    public void transferMoneyCardToCard(@RequestBody @Validated Card cardFrom, @RequestBody @Validated Card cardTo, Currency currency) {
        //выбрать расширение и что возвращает при переводе с карты на карту
        //Принимать перевод на карту, возможно, требуется только номер карты и переделать на @RequestParam("cardNumber")
        transferService.transferMoneyCardToCard(cardFrom, cardTo, currency);
    }

    @PostMapping("/confirmOperation")
    public void ConfirmOperation() {
        //подтверждение нашей операции после успешной проверки карт
        //выбрать расширение и что возвращает
    }
}
