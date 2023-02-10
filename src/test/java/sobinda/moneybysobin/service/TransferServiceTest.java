package sobinda.moneybysobin.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.hamcrest.MatcherAssert;
import org.testcontainers.shaded.org.hamcrest.Matchers;
import sobinda.moneybysobin.entity.Amount;
import sobinda.moneybysobin.log.LogBuilder;
import sobinda.moneybysobin.log.TransferLog;
import sobinda.moneybysobin.model.CardTransfer;
import sobinda.moneybysobin.repository.TransferRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    TransferRepository transferRepository;

    @Mock
    TransferLog transferLog;

    @InjectMocks
    TransferService transferService;
    private final String ID = "1";
    private CardTransfer cardTransfer;

    @BeforeEach
    void setUp() {
        System.out.println("Начало теста");

        cardTransfer = new CardTransfer(
                "1158445885584747",
                "08/23",
                "351",
                "1158445885585555",
                new Amount(BigDecimal.valueOf(50000), "RUR"));
    }

    @AfterEach
    void tearDown() {
        System.out.println("Окончание теста");
    }

    @SneakyThrows
    @Test
    void transferMoneyCardToCard() {
        Mockito.when(transferRepository.transferMoneyCardToCard(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn("Карты имеются в базе");
        Mockito.when(transferRepository.findByCardNumberAndAmountValue(Mockito.any()))
                .thenReturn(Optional.of(new BigDecimal(5000_00)));
        Mockito.when(transferRepository.saveOperationRepository(Mockito.any(LogBuilder.class)))
                .thenReturn(ID);
        //todo Сделать проверку на void
//        Mockito.when(transferLog.log(Mockito.any(LogBuilder.class)))
//                .thenReturn("1");

        var result = transferService.transferMoneyCardToCard(cardTransfer);
        MatcherAssert.assertThat(result, Matchers.is(("Ожидаем подтверждение на перевод операции №") + ID));
    }

    @Test
    void confirmOperation() {
    }
}