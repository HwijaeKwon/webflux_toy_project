package toy.webflux.develop.validator

import org.springframework.validation.Errors
import org.springframework.validation.Validator
import toy.webflux.develop.domain.dto.MemberCreate

class MemberCreateValidator : Validator {
    override fun supports(clazz: Class<*>): Boolean {
        return MemberCreate::class.java.isAssignableFrom(clazz)
    }

    override fun validate(target: Any, errors: Errors) {
        val memberCreate: MemberCreate = target as MemberCreate

        if(memberCreate.name.isBlank()) errors.rejectValue("name", "field.empty", "The name must not be empty.")
        if(memberCreate.age.toInt() < 0) errors.rejectValue("age", "field.invalid", "The age must be positive number")
    }
}