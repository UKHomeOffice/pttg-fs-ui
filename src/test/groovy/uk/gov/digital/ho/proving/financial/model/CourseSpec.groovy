package uk.gov.digital.ho.proving.financial.model

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import spock.lang.Specification

/**
 * @Author Home Office Digital
 */
class CourseSpec extends Specification {

    def "generates meaningful toString instead of just a hash"() {

        given:
        def instance = new Course(true, 1)

        when:
        def output = instance.toString()

        then:
        output.contains("courseLength=$instance.courseLength")

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
