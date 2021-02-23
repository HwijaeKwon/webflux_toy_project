package toy.webflux.develop.handler

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBodyOrNull
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import toy.webflux.develop.domain.document.Member
import toy.webflux.develop.domain.dto.MemberCreate
import toy.webflux.develop.domain.dto.MemberDto
import toy.webflux.develop.domain.dto.MemberDtos
import toy.webflux.develop.domain.dto.MemberUpdate
import toy.webflux.develop.service.MemberService
import toy.webflux.develop.validator.MemberCreateValidator
import toy.webflux.develop.validator.MemberUpdateValidator
import kotlin.math.E

/**
 * Member 관련 요청을 처리하는 handler function
 */
@Component
class MemberHandler(private val memberService: MemberService) {

    /**
     * 새로운 member를 db에 생성한다
     */
    @Operation(
            operationId = "create",
            description = "Create member to DB",
            requestBody = RequestBody(content = [Content(mediaType = "application/json", schema = Schema(implementation = MemberCreate::class))]),
            responses = [
                ApiResponse(responseCode = "200", description = "Success", content = [Content(mediaType = "application/json", schema = Schema(implementation = MemberDto::class, required = true))]),
                ApiResponse(responseCode = "400", description = "Invalid request body", content = [Content(mediaType = "text_plain", schema = Schema(implementation = String::class), examples = [ExampleObject(value = "Invalid request body: Request body is not valid.")])]),
                ApiResponse(responseCode = "500", description = "Fail", content = [Content(mediaType = "text_plain", schema = Schema(implementation = String::class), examples = [ExampleObject(value = "Create member failed: Reason")])]),
            ]
    )
    suspend fun create(request: ServerRequest): ServerResponse {
        val validator = MemberCreateValidator()

        val memberCreate = try { request.awaitBodyOrNull<MemberCreate>() } catch (e: Exception) { null } ?: run {
            val message = "Invalid request body: Request body is not valid."
            return ServerResponse.status(400).contentType(MediaType.TEXT_PLAIN).bodyValueAndAwait(message)
        }

        val errors = BeanPropertyBindingResult(memberCreate, MemberCreate::class.java.name)
        validator.validate(memberCreate, errors)
        if(errors.allErrors.isNotEmpty()) {
            var message = "Invalid request body: "
            errors.allErrors.forEach { error -> message += error.defaultMessage + " "}
            return ServerResponse.status(400).contentType(MediaType.TEXT_PLAIN).bodyValueAndAwait(message)
        }

        return try {
            val member = Member(memberCreate.name, memberCreate.age)
            val result = memberService.create(member)
            ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(MemberDto(result.getId(), result.getName(), result.getAge()))
        } catch (e: IllegalArgumentException) {
            val message = "Create member failed: " + (e.message?: "")
            ServerResponse.status(500).contentType(MediaType.TEXT_PLAIN).bodyValueAndAwait(message)
        }
    }

    /**
     *  member를 조회한다
     */
    @Operation(
            operationId = "findOne",
            description = "find one member from DB",
            parameters = [Parameter(name = "memberId", description = "member id", required = true)],
            responses = [
                ApiResponse(responseCode = "200", description = "Success", content = [Content(mediaType = "application/json", schema = Schema(implementation = MemberDto::class, required = true))]),
                ApiResponse(responseCode = "404", description = "Not Found", content = [Content(mediaType = "text_plain", schema = Schema(implementation = String::class), examples = [ExampleObject(value = "Find member failed: Member not found")])])
            ]
    )
    suspend fun findOne(request: ServerRequest): ServerResponse {
        val memberId = request.pathVariable("memberId")

        return try {
            val result = memberService.findOne(memberId)?: throw IllegalArgumentException("Member not found")
            ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(MemberDto(result.getId(), result.getName(), result.getAge()))
        } catch (e: IllegalArgumentException) {
            val message = "Find member failed: " + (e.message?: "")
            ServerResponse.status(404).contentType(MediaType.TEXT_PLAIN).bodyValueAndAwait(message)
        }
    }

    /**
     *  모든 member를 조회한다
     */
    @Operation(
            operationId = "findAll",
            description = "Find all members from DB",
            responses = [
                ApiResponse(responseCode = "200", description = "Success", content = [Content(mediaType = "application/json", schema = Schema(implementation = MemberDtos::class))]),
            ]
    )
    suspend fun findAll(request: ServerRequest): ServerResponse {
        val members = memberService.findAll()
        val memberLists = members.map { MemberDto(it.getId(), it.getName(), it.getAge()) }
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(MemberDtos(memberLists.size, memberLists))
    }

    /**
     *  member를 업데이트 한다
     */
    @Operation(
            operationId = "update",
            description = "Update member to DB",
            parameters = [Parameter(name = "memberId", description = "member id", required = true)],
            requestBody = RequestBody(content = [Content(mediaType = "application/json", schema = Schema(implementation = MemberUpdate::class))]),
            responses = [
                ApiResponse(responseCode = "200", description = "Success", content = [Content(mediaType = "application/json", schema = Schema(implementation = MemberDto::class))]),
                ApiResponse(responseCode = "400", description = "Invalid Request body", content = [Content(mediaType = "text_plain", schema = Schema(implementation = String::class), examples = [ExampleObject(value = "Invalid request body: Request body is not valid.")])]),
                ApiResponse(responseCode = "404", description = "Not found", content = [Content(mediaType = "text_plain", schema = Schema(implementation = String::class), examples = [ExampleObject(value = "Update member failed: Member not found")])])
            ]
    )
    suspend fun update(request: ServerRequest): ServerResponse {
        val validator = MemberUpdateValidator()
        val memberId = request.pathVariable("memberId")

        val memberUpdate = try { request.awaitBodyOrNull<MemberUpdate>() } catch (e: Exception) { null } ?: run {
            val message = "Invalid request body: Request body is not valid."
            return ServerResponse.status(400).contentType(MediaType.TEXT_PLAIN).bodyValueAndAwait(message)
        }

        val errors = BeanPropertyBindingResult(memberUpdate, MemberCreate::class.java.name)
        validator.validate(memberUpdate, errors)
        if(errors.allErrors.isNotEmpty()) {
            var message = "Invalid request body: "
            errors.allErrors.forEach { error -> message += error.defaultMessage + " "}
            return ServerResponse.status(400).contentType(MediaType.TEXT_PLAIN).bodyValueAndAwait(message)
        }

        return try {
            val result = memberService.update(memberId, memberUpdate)
            ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(MemberDto(result.getId(), result.getName(), result.getAge()))
        } catch (e: IllegalArgumentException) {
            val message = "Update member failed: " + (e.message?: "")
            ServerResponse.status(404).contentType(MediaType.TEXT_PLAIN).bodyValueAndAwait(message)
        }
    }

    /**
     *  특정 member를 삭제한다
     */
    @Operation(
            operationId = "deleteOne",
            description = "Delete one member from DB",
            parameters = [Parameter(name = "memberId", description = "member id", required = true)],
            responses = [
                ApiResponse(responseCode = "200", description = "Success", content = [Content(mediaType = "text_plain", schema = Schema(implementation = String::class), examples = [ExampleObject(value = "Delete success")])]),
            ]
    )
    suspend fun deleteOne(request: ServerRequest): ServerResponse {
        val memberId = request.pathVariable("memberId")
        memberService.deleteById(memberId)
        return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).bodyValueAndAwait("Delete success")
    }

    /**
     *  모든 member를 삭제한다
     */
    @Operation(
            operationId = "deleteAll",
            description = "Delete all members from DB",
            responses = [
                ApiResponse(responseCode = "200", description = "Success", content = [Content(mediaType = "text_plain", schema = Schema(implementation = String::class), examples = [ExampleObject(value = "Delete success")])]),
            ]
    )
    suspend fun deleteAll(request: ServerRequest): ServerResponse {
        memberService.deleteAll()
        return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).bodyValueAndAwait("Delete success")
    }
}