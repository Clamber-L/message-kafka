package com.tr.message.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Value("${app.kafka.dlt-topic}")
	private String dltTopic;

	@Bean
	public ConsumerFactory<String, String> consumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
		props.put("spring.deserializer.key.delegate.class", StringDeserializer.class);
		props.put("spring.deserializer.value.delegate.class", StringDeserializer.class);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "my-group");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
		props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000);
		return new DefaultKafkaConsumerFactory<>(props);
	}

	@Bean
	public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {
		DeadLetterPublishingRecoverer recovered = new DeadLetterPublishingRecoverer(
				kafkaTemplate,
				(record, ex) -> new TopicPartition(dltTopic, record.partition()));

		// 增加重试次数到5次，间隔3秒
		FixedBackOff backOff = new FixedBackOff(3000L, 5L);

		DefaultErrorHandler handler = new DefaultErrorHandler(recovered, backOff);
		// 添加更多不可重试的异常类型
		handler.addNotRetryableExceptions(IllegalArgumentException.class);
		handler.addNotRetryableExceptions(IllegalStateException.class);
		handler.setCommitRecovered(true);

		return handler;
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
			ConsumerFactory<String, String> consumerFactory,
			DefaultErrorHandler errorHandler) {

		ConcurrentKafkaListenerContainerFactory<String, String> factory =
				new ConcurrentKafkaListenerContainerFactory<>();

		factory.setConsumerFactory(consumerFactory);

		// 手动 ack
		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

		// 并发
		factory.setConcurrency(3);

		// 统一异常处理（重试 + DLT）
		factory.setCommonErrorHandler(errorHandler);

		// 如需批处理可启用：
		factory.setBatchListener(true);

		return factory;
	}
}