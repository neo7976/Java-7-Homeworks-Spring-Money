package sobinda.moneybysobin.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.shaded.org.hamcrest.MatcherAssert;
import org.testcontainers.shaded.org.hamcrest.Matchers;
import sobinda.moneybysobin.exceptions.InvalidTransactionExceptions;
import sobinda.moneybysobin.model.Amount;
import sobinda.moneybysobin.model.Card;
import sobinda.moneybysobin.model.Verification;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.stream.Stream;

class TransferRepositoryTest {
    TransferRepository transferRepository;
    public static Card card1 = new Card(
            "4558445885584747",
            "08/23",
            "351");

    public static Card card2 = new Card(
            "4558445885585555",
            "08/23",
            "352");


    @BeforeEach
    void setUp() {
        transferRepository = new TransferRepository();
    }

    @AfterEach
    void tearDown() {
        transferRepository = null;
    }

    public static Stream<Arguments> sourceTransfer() {
        return Stream.of(
                Arguments.of(card1,
                        card2.getCardNumber(),
                        new Amount(new BigDecimal(500_00), "RUR"), "1", new Verification("0000", "1")),
                Arguments.of(card2,
                        card1.getCardNumber(),
                        new Amount(new BigDecimal(1000_00), "RUR"), "3", new Verification("0000", "3"))
        );
    }

    @ParameterizedTest
    @MethodSource("sourceTransfer")
    void transferMoneyCardToCardTest(Card cardFrom, String cardNumberTo, Amount amount, String number, Verification verification) throws InvalidTransactionExceptions {
        Assertions.assertEquals("Ожидаем подтверждение на перевод операции №" + number,
                transferRepository.transferMoneyCardToCard(cardFrom, cardNumberTo, amount));
        Assertions.assertEquals("Успешная транзакция №" + verification.getOperationId(), transferRepository.confirmOperation(verification));
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