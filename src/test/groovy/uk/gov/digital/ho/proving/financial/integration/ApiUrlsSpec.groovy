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
    static final String consentEndpoint = '/consent-endpoint/{sortCode}/{accountNumber}/'

    ApiUrls apiUrls

    def aDate = LocalDate.of(2016, 1, 1)

    def setup() {

        apiUrls = new ApiUrls()

        apiUrls.apiRoot = root
        apiUrls.apiThresholdT4Endpoint = thresholdEndpoint
        apiUrls.apiDailyBalanceEndpoint = balanceEndpoint
        apiUrls.apiConsentEndpoint = consentEndpoint
    }


    def "generates threshold url"() {

        given:
        def course = new Course(true, aDate, aDate, "doctorate", "main")
        def maintenance = new Maintenance(ONE, ONE, ONE, 1)

        when:
        def url = apiUrls.t4ThresholdUrlFor(course, maintenance)

        then:
        url.host == 'localhost'
        url.port == 8080

        url.path == thresholdEndpoint

        url.query == 'inLondon=true&studentType=doctorate&courseStartDate=2016-01-01&courseEndDate=2016-01-01&originalCourseStartDate&tuitionFees=1&tuitionFeesPaid=1&accommodationFeesPaid=1&dependants=1&courseType=main'
    }

    def "generates daily balance url"() {

        given:
        Account account = new Account('11-22-33', '12345678', LocalDate.of(1980, 1, 1))
        BigDecimal totalFundsRequired = ONE
        LocalDate from = LocalDate.of(2016, 1, 1)
        LocalDate to = LocalDate.of(2016, 1, 28)

        when:
        def url = apiUrls.dailyBalanceStatusUrlFor(account, totalFundsRequired, from, to)

        then:
        url.host == 'localhost'
        url.port == 8080

        url.path == balanceEndpoint

        url.query == 'dob=1980-01-01&minimum=1&fromDate=2016-01-01&toDate=2016-01-28'
    }

    def "generates consent url"() {
        given:
        def account = new Account("12-34-56", '12345678', LocalDate.of(2017, 1, 1))

        when:
        def url = apiUrls.consentUrlFor(account)

        then:
        url.host == 'localhost'
        url.port == 8080

        def consentEndpointWithAccountDetails = '/consent-endpoint/12-34-56/12345678/'
        url.path == consentEndpointWithAccountDetails
        url.query == 'dob=2017-01-01'
    }
}
