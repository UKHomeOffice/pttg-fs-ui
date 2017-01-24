package uk.gov.digital.ho.proving.financial.model

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import spock.lang.Specification

import java.time.LocalDate

/**
 * @Author Home Office Digital
 */
class CourseSpec extends Specification {

    def "generates meaningful toString instead of just a hash"() {

        given:
        def aDate = LocalDate.of(2019, 1, 1)
        def instance = new Course(true, aDate, aDate, "doctorate", "main")

        when:
        def output = instance.toString()

        then:
        output.contains("courseStartDate=$instance.courseStartDate")

        and:
        !output.contains('Course@')
    }

    def 'has valid hashcode and equals'() {

        when:
        EqualsVerifier.forClass(Course).suppress(Warning.NONFINAL_FIELDS).verify()

        then:
        noExceptionThrown()
    }
}
