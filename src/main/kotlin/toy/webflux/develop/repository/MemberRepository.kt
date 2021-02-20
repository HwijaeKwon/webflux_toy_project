package toy.webflux.develop.repository

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import toy.webflux.develop.domain.document.Member

/**
 * 실제 DB와 관계없이 repository를 사용하기 위한 repository interface
 */
interface MemberRepository {
    fun save(member: Member): Mono<Member>
    fun findById(id: String): Mono<Member>
    fun findAll(): Flux<Member>
    fun deleteById(id: String): Mono<Void>
    fun deleteAll(): Mono<Void>
}