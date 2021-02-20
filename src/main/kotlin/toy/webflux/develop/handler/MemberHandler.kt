package toy.webflux.develop.handler

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

/**
 * Member 관련 요청을 처리하는 handler function
 */
@Component
class MemberHandler(private val memberService: MemberService) {

    /**
     * 새로운 member를 db에 생성한다
     */
    suspend fun create(request: ServerRequest): ServerResponse {
        val validator = MemberCreateValidator()

        val memberCreate = try { request.awaitBodyOrNull<MemberCreate>() } catch (e: Exception) { null } ?: run {
            val message = "Invalid request body: Request body is not valid."
            return ServerResponse.status(400).contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(message)
        }

        val errors = BeanPropertyBindingResult(memberCreate, MemberCreate::class.java.name)
        validator.validate(memberCreate, errors)
        if(errors.allErrors.isNotEmpty()) {
            var message = "Invalid request body: "
            errors.allErrors.forEach { error -> message += error.defaultMessage + " "}
            return ServerResponse.status(400).contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(message)
        }

        return try {
            val member = Member(memberCreate.name, memberCreate.age)
            val result = memberService.create(member)
            ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(MemberDto(result.getId(), result.getName(), result.getAge()))
        } catch (e: IllegalStateException) {
            val message = "Create member failed: " + (e.message?: "")
            ServerResponse.status(500).contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(message)
        }
    }

    /**
     *  member를 조회한다
     */
    suspend fun findOne(request: ServerRequest): ServerResponse {
        val memberId = request.pathVariable("memberId")

        return try {
            val result = memberService.findOne(memberId)?: throw IllegalArgumentException("Member not found")
            ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(MemberDto(result.getId(), result.getName(), result.getAge()))
        } catch (e: IllegalStateException) {
            val message = "Find member failed: " + (e.message?: "")
            ServerResponse.status(404).contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(message)
        }
    }

    /**
     *  모든 member를 조회한다
     */
    suspend fun findAll(request: ServerRequest): ServerResponse {
        val members = memberService.findAll()
        val memberLists = members.map { it -> MemberDto(it.getId(), it.getName(), it.getAge()) }
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(MemberDtos(memberLists.size, memberLists))
    }

    /**
     *  member를 업데이트 한다
     */
    suspend fun update(request: ServerRequest): ServerResponse {
        val memberId = request.pathVariable("memberId")

        val memberUpdate = try { request.awaitBodyOrNull<MemberUpdate>() } catch (e: Exception) { null } ?: run {
            val message = "Invalid request body: Request body is not valid."
            return ServerResponse.status(400).contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(message)
        }

        return try {
            val result = memberService.update(memberId, memberUpdate)
            ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(MemberDto(result.getId(), result.getName(), result.getAge()))
        } catch (e: IllegalStateException) {
            val message = "Update member failed: " + (e.message?: "")
            ServerResponse.status(404).contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(message)
        }
    }

    /**
     *  특정 member를 삭제한다
     */
    suspend fun deleteOne(request: ServerRequest): ServerResponse {
        val memberId = request.pathVariable("memberId")
        memberService.deleteById(memberId)
        return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).bodyValueAndAwait("Delete success")
    }

    /**
     *  모든 member를 삭제한다
     */
    suspend fun deleteAll(request: ServerRequest): ServerResponse {
        memberService.deleteAll()
        return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).bodyValueAndAwait("Delete success")
    }
}