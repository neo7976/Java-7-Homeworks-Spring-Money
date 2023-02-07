package sobinda.moneybysobin.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sobinda.moneybysobin.repository.TransferRepository;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    TransferRepository transferRepository;

    @InjectMocks
    TransferService transferService;

    @BeforeEach
    void setUp() {
        System.out.println("Начало теста");
    }

    @AfterEach
    void tearDown() {
        System.out.println("Окончание теста");
    }

    @Test
    void transferMoneyCardToCard() {
    }

    @Test
    void confirmOperation() {
    }
}