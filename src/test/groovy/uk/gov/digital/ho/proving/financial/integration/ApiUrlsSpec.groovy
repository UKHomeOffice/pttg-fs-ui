package uk.gov.digital.ho.proving.financial.integration

import spock.lang.Specification
import uk.gov.digital.ho.proving.financial.model.Account
import uk.gov.digital.ho.proving.financial.model.Course
import uk.gov.digital.ho.proving.financial.model.Maintenance

import java.time.LocalDate

import static java.math.BigDecimal.ONE

/**
 * @Author Home Office Digital
 */
class ApiUrlsSpec extends Specification {

    static final String root = 'http://localhost:8080'
    static final String thresholdEndpoint = '/threshold-endpoint'
    static final String balanceEndpoint = '/balance-endpoint'

    ApiUrls apiUrls

    def setup() {

        apiUrls = new ApiUrls()

        apiUrls.apiRoot = root
        apiUrls.apiThresholdEndpoint = thresholdEndpoint
        apiUrls.apiDailyBalanceEndpoint = balanceEndpoint
    }


    def "generates threshold url"() {

        given:
        def course = new Course(true, 1, "doctorate")
        def maintenance = new Maintenance(ONE, ONE, ONE)

        when:
        def url = apiUrls.thresholdUrlFor(course, maintenance)

        then:
        url.host == 'localhost'
        url.port == 8080

        url.path == thresholdEndpoint

        url.query == 'innerLondon=true&studentType=doctorate&courseLength=1&tuitionFees=1&tuitionFeesPaid=1&accommodationFeesPaid=1'
    }

    def "generates daily balance url"() {

        given:
        Account account = new Account('11-22-33', '12345678')
        BigDecimal totalFundsRequired = ONE
        LocalDate from = LocalDate.of(2016, 1, 1)
        LocalDate to = LocalDate.of(2016, 1, 28)

        when:
        def url = apiUrls.dailyBalanceStatusUrlFor(account, totalFundsRequired, from, to)

        then:
        url.host == 'localhost'
        url.port == 8080

        url.path == balanceEndpoint

        url.query == 'minimum=1&fromDate=2016-01-01&toDate=2016-01-28'
    }
}
