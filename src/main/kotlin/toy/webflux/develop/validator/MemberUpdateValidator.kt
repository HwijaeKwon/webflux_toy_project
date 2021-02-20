package toy.webflux.develop.validator

import org.springframework.validation.Errors
import org.springframework.validation.Validator
import toy.webflux.develop.domain.dto.MemberUpdate

class MemberUpdateValidator : Validator {
    override fun supports(clazz: Class<*>): Boolean {
        return MemberUpdate::class.java.isAssignableFrom(clazz)
    }

    override fun validate(target: Any, errors: Errors) {
        val memberUpdate: MemberUpdate = target as MemberUpdate

        if(memberUpdate.name !== null && memberUpdate.name.isBlank()) errors.rejectValue("name", "field.empty", "The name must not be empty.")
        if(memberUpdate.age !== null && memberUpdate.age.toInt() < 0) errors.rejectValue("age", "field.invalid", "The age must be positive number")
    }
}