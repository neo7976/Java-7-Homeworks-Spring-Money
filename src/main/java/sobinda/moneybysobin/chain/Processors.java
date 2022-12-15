package sobinda.moneybysobin.chain;

@FunctionalInterface
public interface Processors {
    boolean process(String msg);
}
