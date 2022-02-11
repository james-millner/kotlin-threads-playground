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
import org.springframework.stereotype.Component
import java.util.UUID.randomUUID

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

    private val logger = KotlinLogging.logger {  }

    @KafkaListener(topics = ["test-topic"], groupId = "test-consumer-group")
    fun listener(payload: List<String>) = runBlocking {
        val deferreds: List<Deferred<Int>> = (1..3).map {
            async {
                delay(1000L * it)
                println("Loading $it")
                it
            }
        }
        val sum = deferreds.awaitAll().sum()
        println("$sum")
    }

    suspend fun loadData(data: String): String {
        logger.info { "Processed $data" }
        delay(5)
        return data
    }
}