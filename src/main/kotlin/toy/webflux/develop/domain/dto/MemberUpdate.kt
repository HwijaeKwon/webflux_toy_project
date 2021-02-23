package toy.webflux.develop.domain.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * Member 업데이트 요청을 위한 Dto
 */
data class MemberUpdate(
    @Schema(description = "Name of the member", required = false, nullable = true, minLength = 1, defaultValue = "null")
    val name: String? = null,
    @Schema(description = "Age of the member", required = false, nullable = true, minimum = "0", defaultValue = "null")
    val age: Number? = null) {
}