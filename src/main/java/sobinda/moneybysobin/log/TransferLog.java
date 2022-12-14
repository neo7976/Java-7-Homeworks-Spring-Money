package sobinda.moneybysobin.log;

import sobinda.moneybysobin.model.Amount;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TransferLog {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    protected AtomicInteger num = new AtomicInteger(0);
    private final ConcurrentHashMap<String, Integer> cardTransactions = new ConcurrentHashMap<>();
    private static volatile TransferLog INSTANCE = null;


    private TransferLog() {
    }

    public static TransferLog getInstance() {
        if (INSTANCE == null) {
            synchronized (TransferLog.class) {
                if (INSTANCE == null)
                    INSTANCE = new TransferLog();
            }
        }
        return INSTANCE;
    }

    public String log(String cardNumberFrom, String cardNumberTo, Amount amount, Amount commission, String result) {
        cardTransactions.put(cardNumberFrom, cardTransactions.getOrDefault(cardNumberFrom, 0) + 1);
        String s = String.format(
                "[%s]\n" +
                        "Операция в системе: №%d\n" +
                        "Операция по карте: №%d\n" +
                        "Номер карты списания: %s\n" +
                        "Номер карты зачисления: %s\n" +
                        "Сумма списания: %s\n" +
                        "Комиссия за перевод: %s\n" +
                        "Результат операции: %s\n",
                dtf.format(LocalDateTime.now()),
                num.incrementAndGet(),
                cardTransactions.get(cardNumberFrom),
                cardNumberFrom,
                cardNumberTo,
                amount,
                commission,
                result
        );
        writeLog(s);
        return s;
    }

    public void writeLog(String s) {
        try {
            BufferedWriter bf = new BufferedWriter(new FileWriter(
                    "src/main/resources/log/logCardTransactions.log", true));
            bf.write(s);
            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
