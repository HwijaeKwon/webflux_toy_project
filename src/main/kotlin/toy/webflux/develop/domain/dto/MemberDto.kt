package toy.webflux.develop.domain.dto

/**
 * Member 생성, 업데이트, 조회시에 사용되는 Dto
 */
data class MemberDto(val id: String, val name: String, val age: Number) {
}