package toy.webflux.develop.domain.document

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * 사용자 document
 */

@Document
class Member (private var name: String, private var age: Number) : BaseDocument() {

    @Id
    private lateinit var _id : String

    fun getId() = this._id

    fun getName() = this.name

    fun getAge() = this.age

    fun update(name: String?, age: Number?) {
        name?.let { this.name = it }
        age?.let { this.age = it }
    }
}