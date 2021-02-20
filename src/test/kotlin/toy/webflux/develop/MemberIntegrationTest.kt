package toy.webflux.develop

import com.google.gson.GsonBuilder
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import net.minidev.json.JSONObject
import org.assertj.core.api.Assertions
import org.junit.After
import org.junit.Before
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import toy.webflux.develop.domain.document.Member
import toy.webflux.develop.domain.dto.MemberCreate
import toy.webflux.develop.domain.dto.MemberDto
import toy.webflux.develop.domain.dto.MemberDtos
import toy.webflux.develop.domain.dto.MemberUpdate
import toy.webflux.develop.repository.MemberRepository

/**
 * Member 요청 통합 테스트
 */

@SpringBootTest(classes = [ToyApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberIntegrationTest {

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @LocalServerPort
    private var port: Int = 0

    private var member: Member = Member("member", 25)

    @BeforeEach
    fun init() {
        memberRepository.save(member).block()
    }

    @AfterEach
    fun close() {
        memberRepository.deleteAll().block()
    }

    @Test
    fun createTest() {
        val memberCreate = MemberCreate("create_test_member", 28)
        val result = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:$port")
                .build()
                .post()
                .uri("/toy/members")
                .bodyValue(memberCreate)
                .exchange()
                .expectStatus()
                .isOk
                .expectBody(MemberDto::class.java)
                .returnResult()

        assertNotNull(result.responseBody)
        val gson = GsonBuilder().setPrettyPrinting().create()
        println(gson.toJson(result.responseBody))
    }

    @Test
    fun createWithWrongReuqestTest() {
        val result = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:$port")
                .build()
                .post()
                .uri("/toy/members")
                .exchange()
                .expectStatus()
                .is4xxClientError
                .expectBody(String::class.java)
                .returnResult()

        assertNotNull(result.responseBody)
        Assertions.assertThat(result.responseBody!!).isEqualTo("Invalid request body: Request body is not valid.")
    }

    @Test
    fun createWithNoNameReuqestTest() {
        val create = JSONObject()
        create["age"] = 15
        val result = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:$port")
                .build()
                .post()
                .uri("/toy/members")
                .bodyValue(create)
                .exchange()
                .expectStatus()
                .is4xxClientError
                .expectBody(String::class.java)
                .returnResult()

        assertNotNull(result.responseBody)
        Assertions.assertThat(result.responseBody!!).isEqualTo("Invalid request body: Request body is not valid.")
    }

    @Test
    fun createWithNoAgeReuqestTest() {
        val create = JSONObject()
        create["name"] = "test"
        create["age"] = "test"
        val result = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:$port")
                .build()
                .post()
                .uri("/toy/members")
                .bodyValue(create)
                .exchange()
                .expectStatus()
                .is4xxClientError
                .expectBody(String::class.java)
                .returnResult()

        assertNotNull(result.responseBody)
        Assertions.assertThat(result.responseBody!!).isEqualTo("Invalid request body: Request body is not valid.")
    }

    @Test
    fun createWithEmptyNameReuqestTest() {
        val create = MemberCreate("", 25)
        val result = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:$port")
                .build()
                .post()
                .uri("/toy/members")
                .bodyValue(create)
                .exchange()
                .expectStatus()
                .is4xxClientError
                .expectBody(String::class.java)
                .returnResult()

        assertNotNull(result.responseBody)
        Assertions.assertThat(result.responseBody!!).isEqualTo("Invalid request body: The name must not be empty. ")
    }

    @Test
    fun createWithInvalidAgeReuqestTest() {
        val create = MemberCreate("test", -25)
        val result = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:$port")
                .build()
                .post()
                .uri("/toy/members")
                .bodyValue(create)
                .exchange()
                .expectStatus()
                .is4xxClientError
                .expectBody(String::class.java)
                .returnResult()

        assertNotNull(result.responseBody)
        Assertions.assertThat(result.responseBody!!).isEqualTo("Invalid request body: The age must be positive number ")
    }


    @Test
    fun findOneTest() {
        val result = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:$port")
                .build()
                .get()
                .uri("/toy/members/{memberId}", member.getId())
                .exchange()
                .expectStatus()
                .isOk
                .expectBody(MemberDto::class.java)
                .returnResult()

        Assertions.assertThat(result).isNotNull
        Assertions.assertThat(result.responseBody!!.id).isEqualTo(member.getId())
    }

    @Test
    fun findNotExistOneTest() {
        val result = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:$port")
                .build()
                .get()
                .uri("/toy/members/{memberId}", "not_exist_member")
                .exchange()
                .expectStatus()
                .isNotFound
                .expectBody(String::class.java)
                .returnResult()

        val findMember = runBlocking { memberRepository.findById("not_exist_name").block() }
        Assertions.assertThat(findMember).isNull()
        Assertions.assertThat(result.responseBody!!).isEqualTo("Find member failed: Member not found")
    }

    @Test
    fun findAllTest() {
        val result = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:$port")
                .build()
                .get()
                .uri("/toy/members")
                .exchange()
                .expectStatus()
                .isOk
                .expectBody(MemberDtos::class.java)
                .returnResult()

        val findMembers = runBlocking { memberRepository.findAll().collectList().awaitSingle() }
        Assertions.assertThat(result.responseBody!!.count).isEqualTo(findMembers.size)
    }

    @Test
    fun updateTeat() {
        val update = MemberUpdate("update", null)
        val result = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:$port")
                .build()
                .patch()
                .uri("/toy/members/{memberId}", member.getId())
                .bodyValue(update)
                .exchange()
                .expectStatus()
                .isOk
                .expectBody(MemberDto::class.java)
                .returnResult()

        Assertions.assertThat(result.responseBody!!.name).isEqualTo("update")
    }

    @Test
    fun updateWithWrongReuqestTest() {
        val result = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:$port")
                .build()
                .patch()
                .uri("/toy/members/{memberId}", member.getId())
                .exchange()
                .expectStatus()
                .is4xxClientError
                .expectBody(String::class.java)
                .returnResult()

        assertNotNull(result.responseBody)
        Assertions.assertThat(result.responseBody!!).isEqualTo("Invalid request body: Request body is not valid.")
    }

    @Test
    fun updateWithEmptyNameReuqestTest() {
        val update = MemberCreate("", 25)
        val result = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:$port")
                .build()
                .patch()
                .uri("/toy/members/{memberId}", member.getId())
                .bodyValue(update)
                .exchange()
                .expectStatus()
                .is4xxClientError
                .expectBody(String::class.java)
                .returnResult()

        assertNotNull(result.responseBody)
        Assertions.assertThat(result.responseBody!!).isEqualTo("Invalid request body: The name must not be empty. ")
    }

    @Test
    fun updateWithInvalidAgeReuqestTest() {
        val update = MemberCreate("test", -25)
        val result = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:$port")
                .build()
                .patch()
                .uri("/toy/members/{memberId}", member.getId())
                .bodyValue(update)
                .exchange()
                .expectStatus()
                .is4xxClientError
                .expectBody(String::class.java)
                .returnResult()

        assertNotNull(result.responseBody)
        Assertions.assertThat(result.responseBody!!).isEqualTo("Invalid request body: The age must be positive number ")
    }



    @Test
    fun deleteTest() {
        val result = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:$port")
                .build()
                .delete()
                .uri("/toy/members/{memberId}", member.getId())
                .exchange()
                .expectStatus()
                .isOk
                .expectBody(String::class.java)
                .returnResult()

        val findMember = runBlocking { memberRepository.findById(member.getId()).block() }
        Assertions.assertThat(findMember).isNull()
    }

    @Test
    fun deleteAllTest() {
        val result = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:$port")
                .build()
                .delete()
                .uri("/toy/members")
                .exchange()
                .expectStatus()
                .isOk
                .expectBody(String::class.java)
                .returnResult()

        val findMembers = runBlocking { memberRepository.findAll().collectList().block() }
        Assertions.assertThat(findMembers!!.size).isEqualTo(0)
    }
}