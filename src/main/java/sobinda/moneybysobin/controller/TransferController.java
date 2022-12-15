package sobinda.moneybysobin.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sobinda.moneybysobin.exceptions.InvalidTransactionExceptions;
import sobinda.moneybysobin.model.CardTransfer;
import sobinda.moneybysobin.service.TransferService;

@RestController
public class TransferController {
    TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transfer")
    public void transferMoneyCardToCard(@RequestBody @Validated CardTransfer cardTransfer) throws InvalidTransactionExceptions {
        //выбрать расширение и что возвращает при переводе с карты на карту
        transferService.transferMoneyCardToCard(cardTransfer);
    }

    @PostMapping("/confirmOperation")
    public void ConfirmOperation() {
        //подтверждение нашей операции после успешной проверки карт
        //выбрать расширение и что возвращает
    }
}
