package sobinda.moneybysobin.repository;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.shaded.org.hamcrest.MatcherAssert;
import org.testcontainers.shaded.org.hamcrest.Matchers;
import sobinda.moneybysobin.exceptions.InvalidTransactionExceptions;
import sobinda.moneybysobin.log.LogBuilder;
import sobinda.moneybysobin.entity.Amount;
import sobinda.moneybysobin.entity.Card;
import sobinda.moneybysobin.entity.Operation;
import sobinda.moneybysobin.model.CardTransfer;
import sobinda.moneybysobin.model.Verification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class TransferRepositoryTest {

    TransferRepository transferRepository;
    TestEntityManager testEntityManager;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    OperationRepository operationRepository;

    @BeforeEach
    void setUp() {
        var card1 = Card.builder().cardNumber("4558445885584747")
                .cardValidTill("08/23")
                .cardCVV("351")
                .amount(new Amount(BigDecimal.valueOf(1111111), "RUR")).build();
        //todo java.lang.NullPointerException
        this.testEntityManager.persistAndFlush(card1);
        this.testEntityManager.persistAndFlush(Card.builder()
                .cardNumber("4558445885585555")
                .cardValidTill("08/23")
                .cardCVV("352")
                .amount(new Amount(BigDecimal.valueOf(2222222), "RUR")).build());

        transferRepository = new TransferRepository(cardRepository, operationRepository);
    }

    @SneakyThrows
    @Test
    void transferMoneyCardToCardTest() {
        CardTransfer cardTransfer = new CardTransfer(
                "4558445885584747",
                "08/23",
                "351",
                "4558445885585555",
                new Amount(BigDecimal.valueOf(50000), "RUR")
        );
        var actual = BigDecimal.valueOf(1111111);
        BigDecimal error = new BigDecimal("0.0005");
        var result = cardRepository.findByCardNumberAndAmountValue(cardTransfer.getCardToNumber()).get();
//        MatcherAssert.assertThat(result).extracting(Card::getAmount).extracting(Amount::getValue).compareTo(BigDecimal.valueOf(1111111));
        MatcherAssert.assertThat(actual, Matchers.is(Matchers.not(Matchers.closeTo(result, error))));


    }

//
//    public static Stream<Arguments> sourceTransfer() {
//        return Stream.of(
//                Arguments.of(card1,
//                        card2.getCardNumber(),
//                        new Amount(new BigDecimal(500_00), "RUR")),
//                Arguments.of(card2,
//                        card1.getCardNumber(),
//                        new Amount(new BigDecimal(1000_00), "RUR"))
//        );
//    }
//
//    @ParameterizedTest
//    @MethodSource("sourceTransfer")
//    void transferMoneyCardToCardTest(Card cardFrom, String cardNumberTo, Amount amount) throws InvalidTransactionExceptions {
//        Assertions.assertEquals("1",
//                transferRepository.transferMoneyCardToCard(cardFrom, cardNumberTo, amount));
//    }
//
//    public static Stream<Arguments> confirmOperation() {
//        return Stream.of(
//                Arguments.of(new LogBuilder().setOperationId("1")
//                        .setAmount(new Amount(BigDecimal.valueOf(500_00), "RUR"))
//                        .setCommission(new Amount(BigDecimal.valueOf(5_00), "RUR"))
//                        .setCardNumberFrom("4558445885584747")
//                        .setCardNumberTo("4558445885585555")
//                ),
//                Arguments.of(new LogBuilder().setOperationId("1")
//                        .setAmount(new Amount(BigDecimal.valueOf(5_000_00), "RUR"))
//                        .setCommission(new Amount(BigDecimal.valueOf(50_00), "RUR"))
//                        .setCardNumberTo("4558445885584747")
//                        .setCardNumberFrom("4558445885585555")
//                )
//        );
//    }
//
//    @SneakyThrows
//    @ParameterizedTest
//    @MethodSource("confirmOperation")
//    void confirmOperationTest(LogBuilder logBuilder) {
//        transferRepository.saveOperationRepository(logBuilder);
//        Assertions.assertEquals(List.of(new Operation(logBuilder)),
//                transferRepository.confirmOperation(new Verification("0000", "1")));
//    }
//
//
//    @Test
//    void validCardToBaseTestFirst() {
//        InvalidTransactionExceptions thrown = Assertions.assertThrows(InvalidTransactionExceptions.class, () -> {
//            transferRepository.validCardToBase(card1, "4554444444444422");
//        });
//        Assertions.assertEquals("Одной из карт нет в базе данных", thrown.getMessage());
//    }
//
//    public static Stream<Arguments> validCardToBase() {
//        return Stream.of(
//                Arguments.of(card1.getCardNumber(), "11/22", card1.getCardCVV(), card2.getCardNumber()),
//                Arguments.of(card1.getCardNumber(), card1.getCardValidTill(), "111", card2.getCardNumber())
//        );
//    }
//
//    @ParameterizedTest
//    @MethodSource("validCardToBase")
//    void validCardToBaseTestSecond(String cardFromNumber, String cardFromTill, String cardFromCVV, String cardToNumber) {
//        InvalidTransactionExceptions thrown = Assertions.assertThrows(InvalidTransactionExceptions.class, () -> {
//            transferRepository.validCardToBase(new Card(cardFromNumber, cardFromTill, cardFromCVV), cardToNumber);
//        });
//        Assertions.assertEquals("Ошибка в доступе к карте списания", thrown.getMessage());
//    }
}