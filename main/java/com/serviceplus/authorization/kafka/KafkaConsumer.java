package com.serviceplus.authorization.kafka;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.serviceplus.authorization.service.KafkaService;

@Service
public class KafkaConsumer {
	
	private static final Logger KafkaLogger = LogManager.getLogger("KafkaLogger");
	
	public static final String RECEIVED_MESSAGE_KEY = "kafka_receivedMessageKey";
	
	@Autowired
	private KafkaService kafkaService;
	
    @KafkaListener(topics = "${auth.meta.topic}" , groupId = "${auth.meta.topic.grp}")
    public void spDashboard(@Header(RECEIVED_MESSAGE_KEY) String key,String message) {
    	KafkaLogger.info("Recieved message for auth meta data with message {}",message);
    	kafkaService.updateAuthMeta(key,message);
    }
}
