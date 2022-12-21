package sobinda.moneybysobin.log;

import sobinda.moneybysobin.model.Operation;

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
        String operationId = String.valueOf(num.incrementAndGet());
        cardTransactions.put(logBuilder.getCardNumberFrom(), cardTransactions.getOrDefault(logBuilder.getCardNumberFrom(), 0) + 1);
        String s = String.format(
                "[%s]\n" +
                        "Операция в системе: №%s\n" +
                        "Операция по карте: №%d\n" +
                        "Номер карты списания: %s\n" +
                        "Номер карты зачисления: %s\n" +
                        "Сумма списания: %.2f %s\n" +
                        "Комиссия за перевод: %.2f %s\n" +
                        "Результат операции: %s\n\n",
                dtf.format(LocalDateTime.now()),
                operationId,
                cardTransactions.get(logBuilder.getCardNumberFrom()),
                logBuilder.getCardNumberFrom(),
                logBuilder.getCardNumberTo(),
                (double) logBuilder.getAmount().getValue() / 100,
                logBuilder.getAmount().getCurrency(),
                (double) logBuilder.getCommission().getValue() / 100,
                logBuilder.getCommission().getCurrency(),
                logBuilder.getResult()
        );
        writeLog(s);
        System.out.println(s);
        return operationId;
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
