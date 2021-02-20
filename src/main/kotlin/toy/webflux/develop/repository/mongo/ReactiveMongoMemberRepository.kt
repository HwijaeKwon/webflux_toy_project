package toy.webflux.develop.repository.mongo

import org.springframework.context.annotation.Primary
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import toy.webflux.develop.domain.document.Member
import toy.webflux.develop.repository.MemberRepository

/**
 * ReactiveMongoRepository를 사용하는 repository
 */

@Repository
@Primary
interface ReactiveMongoMemberRepository : ReactiveMongoRepository<Member, String>, MemberRepository {
}