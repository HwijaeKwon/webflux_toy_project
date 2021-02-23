package toy.webflux.develop.domain.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * Client에게 Member 리스트를 넘겨줄때 사용하는 Dto
 */
data class MemberDtos(
    @Schema(description = "Number of members", minimum = "0")
    val count: Int,
    @Schema(description = "List of MemberDto")
    val data: List<MemberDto>) {
}