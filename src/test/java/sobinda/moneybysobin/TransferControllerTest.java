//todo Сделать в будущем на примере реализации

package sobinda.moneybysobin;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import sobinda.moneybysobin.controller.TransferController;
import sobinda.moneybysobin.entity.Amount;
import sobinda.moneybysobin.exceptions.InvalidTransactionExceptions;
import sobinda.moneybysobin.model.CardTransfer;
import sobinda.moneybysobin.service.TransferService;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransferControllerTest {
    @Mock
    TransferService transferService;
    @InjectMocks
    TransferController transferController;
    //
//    private Set<Card> cards = Set.of(
//            new Card("4558445885584747", "08/23", "351",
//                    new Amount(
//                            BigDecimal.valueOf(50000),
//                            "RUR")),
//            new Card("4558445885585555", "08/23", "352",
//                    new Amount(
//                            BigDecimal.valueOf(250000),
//                            "RUR"))
//    );
    private final CardTransfer cardTransfer = new CardTransfer(
            "4558445885584747", "08/23", "350", "4558445885585555",
            new Amount(BigDecimal.valueOf(50000), "RUR"));

    @BeforeEach
    void setUp() throws InvalidTransactionExceptions {
        when(transferService.transferMoneyCardToCard(Mockito.any())).thenReturn("1");
    }

    @Test
    void transferMoneyCardToCard() throws InvalidTransactionExceptions {
        var result = transferController.transferMoneyCardToCard(cardTransfer);
        var expected = "1";
        Assertions.assertEquals(expected, result, "Ожидаем получить id операции");
    }
}
