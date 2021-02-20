package toy.webflux.develop.repository.mongo

import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate

@Configuration
class ReactiveMongoConfig: AbstractReactiveMongoConfiguration() {

    private val host = "mongodb://localhost"
    private val port = 27017
    private val databaseName = "test"

    override fun reactiveMongoClient(): com.mongodb.reactivestreams.client.MongoClient {
        return MongoClients.create("$host:$port")
    }

    @Bean
    fun reactiveMongoTemplate(): ReactiveMongoTemplate {
        return ReactiveMongoTemplate(reactiveMongoClient(), databaseName)
    }

    override fun getDatabaseName(): String {
        return databaseName
    }
}
