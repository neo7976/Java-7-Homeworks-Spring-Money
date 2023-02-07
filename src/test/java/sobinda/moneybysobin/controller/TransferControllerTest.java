package sobinda.moneybysobin.controller;

import lombok.SneakyThrows;
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
import sobinda.moneybysobin.model.Verification;
import sobinda.moneybysobin.service.TransferService;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransferControllerTest {
    @Mock
    TransferService transferService;
    @InjectMocks
    TransferController transferController;

    @BeforeEach
    void setUp() {
        System.out.println("Начало теста");
    }

    @AfterEach
    void tearDown() {
        System.out.println("Окончание теста");
    }

    @SneakyThrows
    @Test
    void transferMoneyCardToCardTest() {
        CardTransfer cardTransfer = new CardTransfer(
                "4558445885584747", "08/23", "350", "4558445885585555",
                new Amount(BigDecimal.valueOf(50000), "RUR"));
        when(transferService.transferMoneyCardToCard(Mockito.any())).thenReturn("Ожидаем подтверждение на перевод операции №1");
        var result = transferController.transferMoneyCardToCard(cardTransfer);
        var expected = "Ожидаем подтверждение на перевод операции №1";
        Assertions.assertEquals(expected, result, "Ожидаем получить id операции");
    }

    @SneakyThrows
    @Test
    void confirmOperationTest() {
        var verification = new Verification("0000", "1");
        when(transferService.confirmOperation(Mockito.any())).thenReturn("Успешная транзакция №" + verification.getOperationId());
        var result = transferService.confirmOperation(verification);
        var expected = "Успешная транзакция №" + verification.getOperationId();
        Assertions.assertEquals(expected, result);

    }
}
