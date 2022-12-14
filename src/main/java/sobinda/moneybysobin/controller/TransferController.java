package sobinda.moneybysobin.controller;

import org.springframework.web.bind.annotation.RestController;
import sobinda.moneybysobin.service.TransferService;

@RestController
public class TransferController {
    TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }
}
