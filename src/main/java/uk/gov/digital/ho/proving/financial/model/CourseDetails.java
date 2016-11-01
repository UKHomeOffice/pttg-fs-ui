package uk.gov.digital.ho.proving.financial.model;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Author Home Office Digital
 */
@Constraint(validatedBy = CourseDetailsValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface CourseDetails {
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String message() default "Invalid Course Specification";
}
