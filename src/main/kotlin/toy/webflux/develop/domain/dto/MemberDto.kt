package toy.webflux.develop.domain.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * Member 생성, 업데이트, 조회시에 사용되는 Dto
 */
data class MemberDto(
    @Schema(description = "Unique identifier of the Member", nullable = false)
    val id: String,
    @Schema(description = "Name of the member", nullable = false, minLength = 1)
    val name: String,
    @Schema(description = "Age of the member", nullable = false, minimum = "0")
    val age: Number) {
}