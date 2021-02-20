package toy.webflux.hwijae.repository

import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import toy.webflux.hwijae.domain.document.Member

/**
 * 실제 DB와 관계없이 repository를 사용하기 위한 repository interface
 */
interface MemberRepository {
    fun save(member: Member): Mono<Member>
    fun findById(id: ObjectId): Mono<Member>
    fun findAll(): Flux<Member>
    fun deleteDyId(id: ObjectId): Mono<Void>
}