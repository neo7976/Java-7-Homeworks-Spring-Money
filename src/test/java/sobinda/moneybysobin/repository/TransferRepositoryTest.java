package sobinda.moneybysobin.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import sobinda.moneybysobin.exceptions.InvalidTransactionExceptions;
import sobinda.moneybysobin.model.Amount;
import sobinda.moneybysobin.model.Card;
import sobinda.moneybysobin.model.Verification;

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
                        new Amount(500_00, "RUR"), "1", new Verification("0000", "1")),
                Arguments.of(card2,
                        card1.getCardNumber(),
                        new Amount(1000_00, "RUR"), "3", new Verification("0000", "3"))
        );
    }

    @ParameterizedTest
    @MethodSource("sourceTransfer")
    void transferMoneyCardToCardTest(Card cardFrom, String cardNumberTo, Amount amount, String number, Verification verification) throws InvalidTransactionExceptions {
        Assertions.assertEquals("Ожидаем подтверждение на перевод операции №" + number,
                transferRepository.transferMoneyCardToCard(cardFrom, cardNumberTo, amount));
        Assertions.assertEquals("Успешная транзакция №" + verification.getOperationId(), transferRepository.confirmOperation(verification));
    }
//
//    @Test
//    void
}