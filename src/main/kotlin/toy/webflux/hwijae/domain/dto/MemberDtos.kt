package toy.webflux.hwijae.domain.dto

/**
 * Client에게 Member 리스트를 넘겨줄때 사용하는 Dto
 */
data class MemberDtos(val count: Int, val data: List<MemberDto>) {
}