package com.tr.message.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tr.message.model.AlarmSendDataModel;
import com.tr.message.websocket.WebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MyKafkaConsumer {

	private final WebSocketHandler webSocketHandler;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public MyKafkaConsumer(WebSocketHandler webSocketHandler) {
		this.webSocketHandler = webSocketHandler;
	}

	@KafkaListener(
			topics = TopicConstants.WS_SEND_TOPIC,
			containerFactory = "kafkaListenerContainerFactory"
	)
	public void consume(ConsumerRecord<String, String> record, Acknowledgment ack) {
		try {
			log.info("Received Kafka Message <<<<<<<<<<<<<<<<<<<< \ntopic:{}\ndeviceCode:{}\nmessage: {}",
					record.topic(), record.key(), record.value());

			// 手动反序列化
			AlarmSendDataModel msg = objectMapper.readValue(record.value(), AlarmSendDataModel.class);

			log.info("Received message: {}", msg);

			// 你的业务逻辑
			handleMessage(msg);

			// 手动提交 offset
			ack.acknowledge();

			log.info("topic: {}, 分区: {}, 偏移量: {}\n消息处理完成并确认 >>>>>>>>>>>>>>>>>>>>",
					record.topic(), record.partition(), record.offset());
		} catch (Exception e) {
			// 抛出异常由 DefaultErrorHandler 管理（重试 → DLT）
			log.error("error consume: {}", e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	private void handleMessage(AlarmSendDataModel msg) throws JsonProcessingException {
		// 业务逻辑
		log.info("Got message: {}", msg);
		this.webSocketHandler.sendMessageToMass(objectMapper.writeValueAsString(msg));
	}

//	@KafkaListener(
//			topics = TopicConstants.WS_DLT_TOPIC,
//			containerFactory = "kafkaListenerContainerFactory"
//	)
//	public void consumeDtl(ConsumerRecord<String, String> record, Acknowledgment ack) {
//		try {
//			log.info("DLT Received message: {}", record.value());
//			log.info("Topic: {}, Partition: {}, Offset: {}", record.topic(), record.partition(), record.offset());
//
//			// 对于死信队列的消息，我们只记录日志，不再发送到WebSocket
//			// 可以在这里添加告警通知或人工处理逻辑
//
//			// 记录详细的错误信息（如果在header中有）
//			if (record.headers().lastHeader("kafka_dlt-original-topic") != null) {
//				String originalTopic = new String(record.headers().lastHeader("kafka_dlt-original-topic").value());
//				log.info("Original topic: {}", originalTopic);
//			}
//
//			if (record.headers().lastHeader("kafka_dlt-exception-message") != null) {
//				String exceptionMessage = new String(record.headers().lastHeader("kafka_dlt-exception-message").value());
//				log.info("Exception that caused DLT: {}", exceptionMessage);
//			}
//
//			// 手动提交 offset
//			ack.acknowledge();
//			log.info("DLT message acknowledged");
//		} catch (Exception e) {
//			log.error("Error processing DLT message: {}", e.getMessage(), e);
//			// 在DLT消费者中发生的异常不应再发送到DLT，避免无限循环
//			// 可以记录到专门的日志或监控系统
//
//			// 即使处理失败也手动确认，防止阻塞
//			try {
//				ack.acknowledge();
//				log.info("DLT message acknowledged despite processing error");
//			} catch (Exception ackException) {
//				log.error("Failed to acknowledge DLT message: {}", ackException.getMessage(), ackException);
//			}
//		}
//	}
}