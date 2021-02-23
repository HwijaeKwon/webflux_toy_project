package toy.webflux.develop.domain.dto

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotBlank

/**
 * Member 생성 요청에 대한 DTO
 */
data class MemberCreate(
    @Schema(description = "Name of the member", example = "toy", required = true, nullable = false, minLength = 1)
    val name: String,
    @Schema(description = "Age of the member", example = "10", required = true, nullable = false, minimum = "0")
    val age: Number) {
}