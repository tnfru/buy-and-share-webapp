package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Offer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import({Offer.class, Contract.class, ContractService.class})
public class ContractServiceTest {

    @Before
    public void setUpTests() {
    }

    @Test
    public void createTest() {
    }

    @Test
    public void endContractTest() {
    }

    @Test
    public void calcPriceTest() {
    }
}