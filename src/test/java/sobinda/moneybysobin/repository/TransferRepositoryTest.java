package sobinda.moneybysobin.repository;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import sobinda.moneybysobin.exceptions.InvalidTransactionExceptions;
import sobinda.moneybysobin.log.LogBuilder;
import sobinda.moneybysobin.entity.Amount;
import sobinda.moneybysobin.entity.Card;
import sobinda.moneybysobin.entity.Operation;
import sobinda.moneybysobin.model.Verification;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

class TransferRepositoryTest {
    TransferRepository transferRepository;
    private final CardRepository cardRepository;
    public static Card card1 = new Card(
            "4558445885584747",
            "08/23",
            "351");

    public static Card card2 = new Card(
            "4558445885585555",
            "08/23",
            "352");

    TransferRepositoryTest(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }


    @BeforeEach
    void setUp() {
        transferRepository = new TransferRepository(cardRepository);
    }

    @AfterEach
    void tearDown() {
        transferRepository = null;
    }

    public static Stream<Arguments> sourceTransfer() {
        return Stream.of(
                Arguments.of(card1,
                        card2.getCardNumber(),
                        new Amount(new BigDecimal(500_00), "RUR")),
                Arguments.of(card2,
                        card1.getCardNumber(),
                        new Amount(new BigDecimal(1000_00), "RUR"))
        );
    }

    @ParameterizedTest
    @MethodSource("sourceTransfer")
    void transferMoneyCardToCardTest(Card cardFrom, String cardNumberTo, Amount amount) throws InvalidTransactionExceptions {
        Assertions.assertEquals("1",
                transferRepository.transferMoneyCardToCard(cardFrom, cardNumberTo, amount));
    }

    public static Stream<Arguments> confirmOperation() {
        return Stream.of(
                Arguments.of(new LogBuilder().setOperationId("1")
                        .setAmount(new Amount(BigDecimal.valueOf(500_00), "RUR"))
                        .setCommission(new Amount(BigDecimal.valueOf(5_00), "RUR"))
                        .setCardNumberFrom("4558445885584747")
                        .setCardNumberTo("4558445885585555")
                ),
                Arguments.of(new LogBuilder().setOperationId("1")
                        .setAmount(new Amount(BigDecimal.valueOf(5_000_00), "RUR"))
                        .setCommission(new Amount(BigDecimal.valueOf(50_00), "RUR"))
                        .setCardNumberTo("4558445885584747")
                        .setCardNumberFrom("4558445885585555")
                )
        );
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("confirmOperation")
    void confirmOperationTest(LogBuilder logBuilder) {
        transferRepository.setCardTransactionsWaitConfirmOperation(logBuilder.getOperationId(), new Operation(logBuilder));
        Assertions.assertEquals(List.of(new Operation(logBuilder)),
                transferRepository.confirmOperation(new Verification("0000", "1")));
    }


    @Test
    void validCardToBaseTestFirst() {
        InvalidTransactionExceptions thrown = Assertions.assertThrows(InvalidTransactionExceptions.class, () -> {
            transferRepository.validCardToBase(card1, "4554444444444422");
        });
        Assertions.assertEquals("Одной из карт нет в базе данных", thrown.getMessage());
    }

    public static Stream<Arguments> validCardToBase() {
        return Stream.of(
                Arguments.of(card1.getCardNumber(), "11/22", card1.getCardCVV(), card2.getCardNumber()),
                Arguments.of(card1.getCardNumber(), card1.getCardValidTill(), "111", card2.getCardNumber())
        );
    }

    @ParameterizedTest
    @MethodSource("validCardToBase")
    void validCardToBaseTestSecond(String cardFromNumber, String cardFromTill, String cardFromCVV, String cardToNumber) {
        InvalidTransactionExceptions thrown = Assertions.assertThrows(InvalidTransactionExceptions.class, () -> {
            transferRepository.validCardToBase(new Card(cardFromNumber, cardFromTill, cardFromCVV), cardToNumber);
        });
        Assertions.assertEquals("Ошибка в доступе к карте списания", thrown.getMessage());
    }
}