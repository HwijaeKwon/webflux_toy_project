package toy.webflux.develop.router

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter
import toy.webflux.develop.handler.MemberHandler

/**
 * RestAPI 요청을 처리하는 router function 모음
 */
@Configuration
class RouterConfig {

    @Autowired
    private lateinit var memberHandler: MemberHandler

    @Bean
    fun memberRouter(): RouterFunction<ServerResponse> = coRouter {

        "/toy/members".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                "/{memberId}".nest {
                    GET("", memberHandler::findOne)
                    PATCH("", memberHandler::update)
                    DELETE("", memberHandler::deleteOne)
                }
                POST("", memberHandler::create)
                GET("", memberHandler::findAll)
                DELETE("", memberHandler::deleteAll)
            }
        }
    }
}