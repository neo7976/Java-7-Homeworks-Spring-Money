package sobinda.moneybysobin.repository;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import sobinda.moneybysobin.entity.Amount;
import sobinda.moneybysobin.entity.Card;
import sobinda.moneybysobin.entity.Operation;
import sobinda.moneybysobin.exceptions.InvalidTransactionExceptions;
import sobinda.moneybysobin.model.CardTransfer;
import sobinda.moneybysobin.model.Verification;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.stream.Stream;

import static org.testcontainers.shaded.org.hamcrest.MatcherAssert.assertThat;
import static org.testcontainers.shaded.org.hamcrest.Matchers.closeTo;
import static org.testcontainers.shaded.org.hamcrest.Matchers.is;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class TransferRepositoryTest {
    TransferRepository transferRepository;
    @Autowired
    TestEntityManager testEntityManager;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    OperationRepository operationRepository;
    private CardTransfer cardTransfer;
    private static Card card1;
    private static Card card2;
    private static Operation operation;

    @BeforeEach
    void setUp() {
        card1 = Card.builder().cardNumber("1158445885584747")
                .cardValidTill("08/23")
                .cardCVV("351")
                .amount(new Amount(BigDecimal.valueOf(1111111), "RUR")).build();
        card2 = Card.builder().cardNumber("1158445885585555")
                .cardValidTill("08/23")
                .cardCVV("352")
                .amount(new Amount(BigDecimal.valueOf(2222222), "RUR")).build();
        this.testEntityManager.persistAndFlush(card1);
        this.testEntityManager.persistAndFlush(card2);

        transferRepository = new TransferRepository(cardRepository, operationRepository);
        cardTransfer = new CardTransfer(
                "1158445885584747",
                "08/23",
                "351",
                "1158445885585555",
                new Amount(BigDecimal.valueOf(50000), "RUR")
        );

        operation = Operation.builder()
                .cardFromNumber(cardTransfer.getCardFromNumber())
                .cardToNumber(cardTransfer.getCardToNumber())
                .commission(new Amount(BigDecimal.valueOf(5000), cardTransfer.getAmount().getCurrency()))
                .amount(cardTransfer.getAmount())
                .secretCode("0000")
                .build();
        this.testEntityManager.persistAndFlush(operation);
    }

    @SneakyThrows
    @Test
    void findByCardNumberAndAmountValueTest() {
        var actual = BigDecimal.valueOf(1111111);
        BigDecimal error = new BigDecimal("0.0005");
        var result = cardRepository.findByCardNumberAndAmountValue(cardTransfer.getCardFromNumber()).get();
        assertThat(actual, is((closeTo(result, error))));
    }

    @SneakyThrows
    @Test
    void findByCardNumberTest() {
        var result1 = cardRepository.findByCardNumber(cardTransfer.getCardFromNumber()).get();
        var result2 = cardRepository.findByCardNumber(cardTransfer.getCardToNumber()).get();
        assertThat(card1, is(result1));
        assertThat(card2, is(result2));
    }

    @SneakyThrows
    @Test
    void transferMoneyCardToCard() {
        Card cardTest = Card.builder().cardNumber(cardTransfer.getCardFromNumber())
                .cardCVV(cardTransfer.getCardFromCVV())
                .cardValidTill(cardTransfer.getCardFromValidTill()).build();

        transferRepository.transferMoneyCardToCard(cardTest, cardTransfer.getCardToNumber(), cardTransfer.getAmount());
    }

    @Test
    void validCardToBaseTestFirst() {
        InvalidTransactionExceptions thrown = Assertions.assertThrows(InvalidTransactionExceptions.class, () -> {
            transferRepository.validCardToBase(card1, "4554444444444422");
        });
        Assertions.assertEquals("Одной из карт нет в базе данных", thrown.getMessage());
    }

    public static Stream<Arguments> setBalanceTrue() {
        return Stream.of(
                Arguments.of(card1.getCardNumber(), BigDecimal.valueOf(5000)),
                Arguments.of(card1.getCardNumber(), BigDecimal.valueOf(0)),
                Arguments.of(card2.getCardNumber(), BigDecimal.valueOf(5000)),
                Arguments.of(card2.getCardNumber(), BigDecimal.valueOf(0))
        );
    }

    @ParameterizedTest()
    @MethodSource("setBalanceTrue")
    void setBalanceCardTrueTest(String card, BigDecimal bigDecimal) {
        var result = transferRepository.setBalanceCard(card, bigDecimal);
        Assertions.assertTrue(result, "Получаем true, когда баланс изменился");
    }

    public static Stream<Arguments> setBalanceFalse() {
        return Stream.of(
                Arguments.of(card1.getCardNumber(), BigDecimal.valueOf(-5000)),
                Arguments.of(card2.getCardNumber(), BigDecimal.valueOf(-5000))
        );
    }

    @ParameterizedTest()
    @MethodSource("setBalanceFalse")
    void setBalanceCardFalseTest(String card, BigDecimal bigDecimal) {
        var result = transferRepository.setBalanceCard(card, bigDecimal);
        Assertions.assertFalse(result, "Получаем False, когда баланс не изменился");
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

    // todo потом повторить
//    @Test
//    void setOperationConfirmTest() {
//        var result = transferRepository.setOperationConfirm(operation.getId());
//        Assertions.assertTrue(result, "Ожидаем изменение confirm на true");
//    }
//
    @SneakyThrows
    @Test
    void confirmOperationTest() {
        var resultList = transferRepository.confirmOperation(new Verification("0000", String.valueOf(operation.getId())));
        assertThat((resultList), is(Collections.singletonList(operation)));
    }

    @SneakyThrows
    @Test
    void confirmOperationThrowTest() {
        InvalidTransactionExceptions exceptions = Assertions.assertThrows(InvalidTransactionExceptions.class, () -> {
            transferRepository.confirmOperation(new Verification("0001", String.valueOf(operation.getId())));
        });
        assertThat(exceptions.getMessage(), is("Ошибочка, такого мы не предвидели!"));
    }

}