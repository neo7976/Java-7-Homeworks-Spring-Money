package sobinda.moneybysobin.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sobinda.moneybysobin.model.Amount;
import sobinda.moneybysobin.model.CardTransfer;
import sobinda.moneybysobin.model.Verification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class DemoApplicationTest {
//    final String NAME_LOCAL = "http://192.168.99.100:";
  final String NAME_LOCAL = "http://localhost:";

    @Autowired
    TestRestTemplate restTemplate;

    @Container
    private final GenericContainer<?> appMoney = new GenericContainer<>("moneyapp:latest")
            .withExposedPorts(5500);

    @Test
    void contextLoads() {
        CardTransfer request = new CardTransfer(
                "4558445885584747",
                "08/23",
                "351",
                "4558445885585555",
                new Amount(50000, "RUR")
        );

        Verification verification = new Verification("0000", "1");

        ResponseEntity<String> forEntity = restTemplate.postForEntity(
                NAME_LOCAL + appMoney.getMappedPort(5500) + "/transfer", request, String.class);
        System.out.println(forEntity.getBody());
        Assertions.assertEquals("Ожидаем подтверждение на перевод операции №1", forEntity.getBody());

        ResponseEntity<String> forEntitySecond = restTemplate.postForEntity(
                NAME_LOCAL + appMoney.getMappedPort(5500) + "/confirmOperation", verification, String.class);
        System.out.println(forEntitySecond.getBody());
        Assertions.assertEquals("Успешная транзакция №1", forEntitySecond.getBody());
    }
}
