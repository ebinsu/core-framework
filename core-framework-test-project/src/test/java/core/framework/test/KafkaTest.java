package core.framework.test;

import core.framework.kafka.publisher.MessagePublisher;
import core.framework.test.hibernate.domain.TestDomain;
import core.framework.test.hibernate.domain.TestDomainRepo;
import core.framework.test.kafka.DemoMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.Optional;

/**
 * @author ebin
 */
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class KafkaTest {
    @Autowired
    MessagePublisher messagePublisher;
    @Autowired
    TestDomainRepo testDomainRepo;

    @Test
    public void testPublisher() throws InterruptedException {
        DemoMessage demoMessage = new DemoMessage();
        demoMessage.id = "1";
        messagePublisher.publish("tp1", null, demoMessage);
        Thread.sleep(10000L);
        Optional<TestDomain> test = testDomainRepo.findByQueryString("TestDomainFinder.selectByName", "DemoMessage");
        Assertions.assertTrue(test.isPresent());
    }
}
