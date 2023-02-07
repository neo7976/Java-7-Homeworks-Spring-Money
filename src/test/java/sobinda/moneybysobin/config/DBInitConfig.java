package sobinda.moneybysobin.config;

import org.dbunit.DBTestCase;
import org.dbunit.dataset.IDataSet;

//наш конфигурационный класс, который даст нам возможность быстрее создавать тесты
public class DBInitConfig extends DBTestCase {
    @Override
    protected IDataSet getDataSet() throws Exception {
        return null;
    }
}
