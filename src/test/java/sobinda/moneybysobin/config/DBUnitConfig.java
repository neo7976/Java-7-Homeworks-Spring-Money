package sobinda.moneybysobin.config;

import org.dbunit.DBTestCase;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;

import java.io.IOException;
import java.util.Properties;

//наш конфигурационный класс, который даст нам возможность быстрее создавать тесты
public class DBUnitConfig extends DBTestCase {

    protected IDatabaseTester tester;
    protected Properties properties;
    protected IDataSet beforeData;

    @Before
    public void setUp() throws Exception {
        tester = new JdbcDatabaseTester(
                properties.getProperty("db.driver"),
                properties.getProperty("db.url"),
                properties.getProperty("db.username"),
                properties.getProperty("db.password"));
    }

    public DBUnitConfig(String name) {
        super(name);
        properties = new Properties();
        try {
            properties.load(Thread.currentThread()
                    .getContextClassLoader().getResourceAsStream("db.config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, properties.getProperty("db.driver"));
            System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, properties.getProperty("db.url"));
            System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, properties.getProperty("db.username"));
            System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, properties.getProperty("db.password"));
            System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_SCHEMA, properties.getProperty(""));
        }
    }

    @Override
    protected IDataSet getDataSet() throws Exception {
        return beforeData;
    }

    @Override
    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.DELETE_ALL;
    }
}
