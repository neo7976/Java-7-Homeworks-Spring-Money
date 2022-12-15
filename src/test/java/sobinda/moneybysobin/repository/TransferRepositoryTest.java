package sobinda.moneybysobin.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import sobinda.moneybysobin.exceptions.InvalidTransactionExceptions;
import sobinda.moneybysobin.model.Amount;
import sobinda.moneybysobin.model.Card;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TransferRepositoryTest {
    TransferRepository transferRepository;
    public static Card card1 = new Card(
            "4558 4458 8558 4747",
            "08/23",
            "351");

    public static Card card2 = new Card(
            "4558 4458 8558 5555",
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
                        "4558 4458 8558 5555",
                        new Amount(5_000, "RUR")),
                Arguments.of(card2,
                        "4558 4458 8558 4747",
                        new Amount(10_000, "RUR"))
        );
    }

    @ParameterizedTest
    @MethodSource("sourceTransfer")
    void transferMoneyCardToCard(Card cardFrom, String cardNumberTo, Amount amount) throws InvalidTransactionExceptions {
        transferRepository.transferMoneyCardToCard(cardFrom, cardNumberTo, amount);
        //todo дописать тест, как будет понятно, что мы возвращаем в конечном итоге
    }
}