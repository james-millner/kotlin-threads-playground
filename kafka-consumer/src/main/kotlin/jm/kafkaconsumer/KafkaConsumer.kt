package jm.kafkaconsumer

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.*
import mu.KotlinLogging
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
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
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
            VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            MAX_POLL_RECORDS_CONFIG to 10
        )
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {

        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory("spring")
        factory.isBatchListener = true
        return factory
    }
}

data class Data(
    val message: String
)

@Component
class KafkaConsumerService(
    val mapper: ObjectMapper
) {

    private val logger = KotlinLogging.logger { }

    @KafkaListener(topics = ["test-topic"], groupId = "test-consumer-group")
    fun listener(
        payload: List<String>,
        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) partition: Int) {
        coroutineProcessing(payload, partition)
    }

    private fun noneCoroutineProcessing(payload: List<String>) {
        payload.forEach {
            logger.info { "Message: $it" }

        }

        logger.info { "Consumed ${payload.size} from Kafka" }
    }

    private fun coroutineProcessing(payload: List<String>, partition: Int) {
        runBlocking {
            payload.forEach {
                launch(CoroutineName("kafka-consumer")) {
                    delay(2000L)
                    logger.info { "Message (Partition = $partition): $it" }
                }
            }

            logger.info { "Consumed ${payload.size} from Kafka" }
        }
    }
}