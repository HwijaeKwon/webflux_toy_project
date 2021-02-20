package toy.webflux.develop.service

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import toy.webflux.develop.ToyApplication
import toy.webflux.develop.domain.document.Member
import toy.webflux.develop.repository.MemberRepository

@SpringBootTest(classes = [ToyApplication::class])
internal class MemberServiceTest {

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var memberService: MemberService

    @AfterEach
    fun close() {
        memberRepository.deleteAll().block()
    }

    @Test
    fun createTest() {
        val member = Member("member", 10)
        val savedMember = runBlocking { memberService.create(member) }
        Assertions.assertThat(savedMember).isEqualTo(member)
    }

    @Test
    fun findOneTest() {
        val savedMember = runBlocking { memberRepository.save(Member("member", 10)).block()!! }
        val findMember = runBlocking { memberService.findOne(savedMember.getId()) }
        Assertions.assertThat(findMember!!.getId()).isEqualTo(savedMember.getId())
    }

    @Test
    fun findNotExistOneTest() {
        val findMember = runBlocking { memberService.findOne("not_exist_one") }
        Assertions.assertThat(findMember).isNull()
    }

    @Test
    fun findAllTest() {
        runBlocking { memberRepository.save(Member("member1", 10)).block()!! }
        runBlocking { memberRepository.save(Member("member2", 10)).block()!! }
        val findMembers = runBlocking { memberService.findAll() }
        Assertions.assertThat(findMembers.size).isEqualTo(2)
    }

    @Test
    fun updateTeat() {
        val savedMember = runBlocking { memberRepository.save(Member("member", 10)).block()!! }
        savedMember.update("updatedMember", null)
        val updatedMember = runBlocking { memberService.update(savedMember) }
        Assertions.assertThat(updatedMember).isNotNull
        Assertions.assertThat(updatedMember!!.getName()).isEqualTo("updatedMember")
        Assertions.assertThat(updatedMember.getAge()).isEqualTo(10)
    }

    @Test
    fun deleteTest() {
        val savedMember = runBlocking { memberRepository.save(Member("member", 10)).block()!! }
        runBlocking { memberService.deleteById(savedMember.getId()) }
        val findMember = runBlocking { memberRepository.findById(savedMember.getId()).block() }
        Assertions.assertThat(findMember).isNull()
    }

    @Test
    fun deleteAllTest() {
        runBlocking { memberRepository.save(Member("member1", 10)).block()!! }
        runBlocking { memberRepository.save(Member("member2", 10)).block()!! }
        runBlocking { memberService.deleteAll() }
        val findMembers = runBlocking { memberService.findAll() }
        Assertions.assertThat(findMembers.size).isEqualTo(0)
    }
}