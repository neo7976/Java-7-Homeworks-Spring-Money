package sobinda.moneybysobin.service;

import org.springframework.stereotype.Service;
import sobinda.moneybysobin.repository.TransferRepository;

@Service
public class TransferService {

    TransferRepository transferRepository;

    public TransferService(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }
}
