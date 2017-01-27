package uk.gov.digital.ho.proving.financial.api

import spock.lang.Specification
import uk.gov.digital.ho.proving.financial.integration.ThresholdResult
import uk.gov.digital.ho.proving.financial.model.CappedValues
import uk.gov.digital.ho.proving.financial.model.ResponseDetails

import java.time.LocalDate

/**
 * @Author Home Office Digital
 */
class ThresholdResponseSpec extends Specification {

    ResponseDetails details = new ResponseDetails("200", "OK")
    CappedValues cappedValues = new CappedValues(100.01, 9)

    def 'Creating a new ThresholdResponse from a ThresholdResult'() {
        given:
        def thresholdResult = new ThresholdResult(2.0, LocalDate.of(2001, 2, 2), cappedValues, details)

        when:
        def thresholdResponse = new ThresholdResponse(thresholdResult)

        then:
        thresholdResponse.getThreshold() == 2.0
        thresholdResponse.getLeaveEndDate() == LocalDate.of(2001, 2, 2)
        thresholdResponse.getCappedValues() == cappedValues

    }

}
