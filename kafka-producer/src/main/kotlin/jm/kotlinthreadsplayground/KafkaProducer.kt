package jm.kotlinthreadsplayground

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.io.Serializable
import java.util.UUID.randomUUID

@SpringBootApplication
class KafkaProducer

fun main(args: Array<String>) {
    runApplication<KafkaProducer>(*args)
}

@Configuration
class SenderConfig {

    @Value("\${kafka.bootstrap-servers}")
    private val bootstrapServers: String? = null

    @Bean
    fun producerConfigs(): Map<String, Serializable?> {
        return mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java
        )
    }

    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        return DefaultKafkaProducerFactory(producerConfigs())
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory())
    }
}

data class Data(
    val message: String
)

@RestController
class KafkaProducerApi(
    val kafkaTemplate: KafkaTemplate<String, String>,
    val objectMapper: ObjectMapper,
) {

    @GetMapping
    fun get(): Map<String, String> = mapOf("hello" to "world")

    @PostMapping("/post")
    fun postData(): Map<String, String> {
        val dataAsString = objectMapper.writeValueAsString(Data(message = randomUUID().toString()))
        kafkaTemplate.send("test-topic", dataAsString)

        return mapOf("response" to "success")
    }

}