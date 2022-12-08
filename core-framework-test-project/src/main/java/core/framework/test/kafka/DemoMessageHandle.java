package core.framework.test.kafka;

import core.framework.kafka.annotation.KafkaMessageHandler;
import core.framework.kafka.consumer.MessageHandler;
import core.framework.test.hibernate.domain.TestDomain;
import core.framework.test.hibernate.domain.TestDomainRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author ebin
 */
@KafkaMessageHandler(topic = "tp1")
public class DemoMessageHandle implements MessageHandler<DemoMessage> {
    private final Logger logger = LoggerFactory.getLogger(DemoMessageHandle.class);
    @Autowired
    TestDomainRepo repo;

    @Transactional
    @Override
    public void handle(Message<DemoMessage> message) throws Exception {
        TestDomain testDomain = new TestDomain("DemoMessage");
        repo.persist(testDomain);
        logger.info("persist");
    }
}
