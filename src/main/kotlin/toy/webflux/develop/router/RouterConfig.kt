package toy.webflux.develop.router

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter
import toy.webflux.develop.domain.dto.MemberDto
import toy.webflux.develop.handler.MemberHandler

/**
 * RestAPI 요청을 처리하는 router function 모음
 */
@Configuration
class RouterConfig {

    @Autowired
    private lateinit var memberHandler: MemberHandler

    @Bean
    @RouterOperations(
            RouterOperation(path = "/toy/members/{memberId}", method = [RequestMethod.GET], beanClass = MemberHandler::class, beanMethod = "findOne"),
            RouterOperation(path = "/toy/members/{memberId}", method = [RequestMethod.PATCH], beanClass = MemberHandler::class, beanMethod = "update"),
            RouterOperation(path = "/toy/members/{memberId}", method = [RequestMethod.DELETE], beanClass = MemberHandler::class, beanMethod = "deleteOne"),
            RouterOperation(path = "/toy/members", method = [RequestMethod.POST], beanClass = MemberHandler::class, beanMethod = "create"),
            RouterOperation(path = "/toy/members", method = [RequestMethod.GET], beanClass = MemberHandler::class, beanMethod = "findAll"),
            RouterOperation(path = "/toy/members", method = [RequestMethod.DELETE], beanClass = MemberHandler::class, beanMethod = "deleteAll"),
    )
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