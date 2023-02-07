package sobinda.moneybysobin.entity;

import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Before;
import sobinda.moneybysobin.config.DBUnitConfig;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import static org.junit.jupiter.api.Assertions.*;

class CardTest extends DBUnitConfig {
    private EntityManager entityManager = Persistence.createEntityManagerFactory("DBUnitEx").createEntityManager();

    @Before
    public void setUp() throws Exception {

    }

    public CardTest(String name) {
        super(name);
    }
}