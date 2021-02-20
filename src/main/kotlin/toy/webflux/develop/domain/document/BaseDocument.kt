package toy.webflux.develop.domain.document

import java.util.*

/**
 * 모든 document가 상속받는 추상 클래스
 * createdAt 값을 세팅한다
 */
abstract class BaseDocument {

    private var createdAt: Date = Date()

    fun getCreatedAt(): Date = this.createdAt
}