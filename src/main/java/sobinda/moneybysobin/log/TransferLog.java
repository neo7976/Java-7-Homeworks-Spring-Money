package sobinda.moneybysobin.log;

import java.io.BufferedWriter;
import java.io.File;
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

    File file = new File("src/main/resources/log/logCardTransactions.log");

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

    public String log(LogBuilder logBuilder) {
        cardTransactions.put(logBuilder.getCardNumberFrom(), cardTransactions.getOrDefault(logBuilder.getCardNumberFrom(), 0) + 1);
        String s = String.format(
                "[%s]\n" +
                        "Операция в системе: №%d\n" +
                        "Операция по карте: №%d\n" +
                        "Номер карты списания: %s\n" +
                        "Номер карты зачисления: %s\n" +
                        "Сумма списания: %s\n" +
                        "Комиссия за перевод: %s\n" +
                        "Результат операции: %s\n\n",
                dtf.format(LocalDateTime.now()),
                num.incrementAndGet(),
                cardTransactions.get(logBuilder.getCardNumberFrom()),
                logBuilder.getCardNumberFrom(),
                logBuilder.getCardNumberTo(),
                logBuilder.getAmount(),
                logBuilder.getCommission(),
                logBuilder.getResult()
        );
        writeLog(s);
        return s;
    }

    public void writeLog(String s) {
        try {
            if (!file.exists()) {
                boolean folder = new File("src/main/resources/log").mkdir();
                //создаем файл
                file.createNewFile();
            }
            BufferedWriter bf = new BufferedWriter(new FileWriter(
                    file, true));
            bf.write(s);
            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
