package toy.webflux.hwijae.repository.mongo

import org.bson.types.ObjectId
import org.springframework.context.annotation.Primary
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import toy.webflux.hwijae.domain.document.Member

/**
 * ReactiveMongoRepository를 사용하는 repository
 */

@Repository
@Primary
interface ReactiveMongoMemberRepository : ReactiveMongoRepository<Member, ObjectId> {
}