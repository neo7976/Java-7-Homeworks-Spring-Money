package sobinda.moneybysobin.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.hamcrest.MatcherAssert;
import org.testcontainers.shaded.org.hamcrest.Matchers;
import sobinda.moneybysobin.entity.Amount;
import sobinda.moneybysobin.entity.Card;
import sobinda.moneybysobin.entity.Operation;
import sobinda.moneybysobin.log.LogBuilder;
import sobinda.moneybysobin.log.TransferLog;
import sobinda.moneybysobin.model.CardTransfer;
import sobinda.moneybysobin.model.Verification;
import sobinda.moneybysobin.repository.TransferRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    private static Operation operation;

    @BeforeEach
    void setUp() {
        System.out.println("Начало теста");

        cardTransfer = new CardTransfer(
                "1158445885584747",
                "08/23",
                "351",
                "1158445885585555",
                new Amount(BigDecimal.valueOf(50000), "RUR"));

        operation = Operation.builder()
                .cardFromNumber(cardTransfer.getCardFromNumber())
                .cardToNumber(cardTransfer.getCardToNumber())
                .commission(new Amount(BigDecimal.valueOf(5000), cardTransfer.getAmount().getCurrency()))
                .amount(cardTransfer.getAmount())
                .secretCode("0000")
                .build();
    }

    @AfterEach
    void tearDown() {
        System.out.println("Окончание теста");
    }

    @SneakyThrows
    @Test
    void transferMoneyCardToCard() {
        when(transferRepository.transferMoneyCardToCard(any(), any(), any()))
                .thenReturn("Карты имеются в базе");
        when(transferRepository.findByCardNumberAndAmountValue(any()))
                .thenReturn(Optional.of(new BigDecimal(5000_00)));
        when(transferRepository.saveOperationRepository(any(LogBuilder.class)))
                .thenReturn(ID);
        var result = transferService.transferMoneyCardToCard(cardTransfer);
        MatcherAssert.assertThat(result, Matchers.is(("Ожидаем подтверждение на перевод операции №") + ID));
    }

    @SneakyThrows
    @Test
    void confirmOperation() {
        when(transferRepository.confirmOperation(Mockito.any(Verification.class)))
                .thenReturn(new ArrayList<>(Collections.singletonList(operation)));
        when(transferRepository.findByCardNumberAndAmountValue(any()))
                .thenReturn(Optional.of(new BigDecimal(5000_00)));
        when(transferRepository.setBalanceCard(Mockito.any(), Mockito.any(BigDecimal.class)))
                .thenReturn(true);
        var result = transferService.confirmOperation(new Verification("0000", ID));
        Assertions.assertEquals("Успешная транзакция №" + ID, result);
    }
}