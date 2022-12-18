package sobinda.moneybysobin.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sobinda.moneybysobin.exceptions.InvalidTransactionExceptions;
import sobinda.moneybysobin.model.CardTransfer;
import sobinda.moneybysobin.model.Verification;
import sobinda.moneybysobin.service.TransferService;

@RestController
public class TransferController {
    TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transfer")
    public String transferMoneyCardToCard(@RequestBody @Validated CardTransfer cardTransfer) throws InvalidTransactionExceptions {
        //выбрать расширение и что возвращает при переводе с карты на карту
        return transferService.transferMoneyCardToCard(cardTransfer);
    }

    @PostMapping("/confirmOperation")
    public String confirmOperation(@RequestBody @Validated Verification verification) {
        //подтверждение нашей операции после успешной проверки карт
        //выбрать расширение и что возвращает
        return transferService.confirmOperation(verification);
    }
}
