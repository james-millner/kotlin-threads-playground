package jm.kafkaconsumer

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerConfig.*
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.stereotype.Component

@SpringBootApplication
class KafkaConsumer

fun main(args: Array<String>) {
    runApplication<KafkaConsumer>(*args)
}

@EnableKafka
@Configuration
class ConsumerConfig {

    @Value("\${kafka.bootstrap-servers}")
    private val bootstrapServers: String? = null

    fun consumerFactory(groupId: String): ConsumerFactory<String, String> {
        val props = mapOf(BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            GROUP_ID_CONFIG to StringSerializer::class.java,
            KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java
        )
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {

        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory("spring")
        return factory
    }
}


@Component
class KafkaConsumerService(
    val mapper: ObjectMapper
) {

    @KafkaListener(topics = ["test-topic"], groupId = "test-consumer-group")
    fun listener(payload: String) {
            println(payload)
    }
}