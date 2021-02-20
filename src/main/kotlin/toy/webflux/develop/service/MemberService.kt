package toy.webflux.develop.service

import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactive.awaitSingleOrNull
import org.springframework.stereotype.Service
import toy.webflux.develop.domain.document.Member
import toy.webflux.develop.domain.dto.MemberUpdate
import toy.webflux.develop.repository.MemberRepository

/**
 * Member 관련 비즈니스 로직을 처리하는 클래스
 */
@Service
class MemberService(private val memberRepository: MemberRepository) {

    suspend fun create(member: Member): Member {
        return memberRepository.save(member).awaitSingle()
    }

    suspend fun findOne(id: String): Member? {
        return memberRepository.findById(id).awaitSingleOrNull()
    }

    suspend fun findAll(): List<Member> {
        return memberRepository.findAll().collectList().awaitLast()
    }

    suspend fun update(id: String, memberUpdate: MemberUpdate): Member {
        val member = memberRepository.findById(id).awaitSingleOrNull()?: throw IllegalArgumentException("Member not found")
        member.update(memberUpdate.name, memberUpdate.age)
        return memberRepository.save(member).awaitSingle()
    }

    suspend fun deleteById(id: String): Void? {
        return memberRepository.deleteById(id).awaitSingleOrNull()
    }

    suspend fun deleteAll(): Void? {
        return memberRepository.deleteAll().awaitSingleOrNull()
    }
}