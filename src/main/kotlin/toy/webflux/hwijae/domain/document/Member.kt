package toy.webflux.hwijae.domain.document

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * 사용자 document
 */

@Document
class Member (private var name: String, private var age: Int) : BaseDocument() {

    @Id
    private lateinit var _id : ObjectId

    fun getId() = this._id

    fun getName() = this.name

    fun getAge() = this.age

    fun update(name: String?, age: Int?) {
        name?.let { this.name = it }
        age?.let { this.age = it }
    }
}