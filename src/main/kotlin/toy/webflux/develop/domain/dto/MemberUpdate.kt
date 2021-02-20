package toy.webflux.develop.domain.dto

/**
 * Member 업데이트 요청을 위한 Dto
 */
data class MemberUpdate(val name: String?, val age: Number?) {
}