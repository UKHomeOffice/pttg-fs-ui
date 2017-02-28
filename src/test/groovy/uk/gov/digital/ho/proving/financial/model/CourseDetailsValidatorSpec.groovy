package uk.gov.digital.ho.proving.financial.model

import spock.lang.Specification

import javax.validation.ConstraintValidatorContext
import java.time.LocalDate

class CourseDetailsValidatorSpec extends Specification {

    def boolean IN_LONDON = true
    def validator = new CourseDetailsValidator()

    def context
    def builder
    def nodeContext

    def aDate = LocalDate.of(2016, 6, 1)

    def setup() {

        nodeContext = Mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext)

        builder = Mock(ConstraintValidatorContext.ConstraintViolationBuilder) {
            addPropertyNode(_) >> nodeContext
        }

        context = Mock(ConstraintValidatorContext) {
            buildConstraintViolationWithTemplate(_) >> builder
        }
    }

    def 'course dates not allowed for doctorate - no dates = valid'() {

        given:
        Course course = aCourseWith(null, null, null, "des", "main")

        expect:
        validator.isValid(course, context)
    }

    def 'course dates not allowed for doctorate - date supplied = invalid'() {

        given:
        Course course = aCourseWith(aDate, null, null, "des", "main")

        expect:
        !validator.isValid(course, context)
    }


    def 'course start date mandatory for non-doctorate'() {

        given:
        Course course = aCourseWith(null, aDate, aDate, "NON-DOCTORATE","main")

        expect:
        !validator.isValid(course, context)
    }

    def 'course end date mandatory for non-doctorate'() {

        given:
        Course course = aCourseWith(aDate, null, aDate, "NON-DOCTORATE","main")

        expect:
        !validator.isValid(course, context)
    }

    def 'continuation end date optional for non-doctorate'() {

        given:
        Course course = aCourseWith(aDate, aDate, null, "NON-DOCTORATE","main")

        expect:
        validator.isValid(course, context)
    }

    Course aCourseWith(LocalDate courseStartDate, LocalDate courseEndDate, LocalDate continuationEndDate, String studentType, String courseType) {
        return new Course(IN_LONDON, courseStartDate, courseEndDate, continuationEndDate, studentType, courseType)
    }
}
