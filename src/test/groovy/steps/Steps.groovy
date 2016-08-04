package steps

import com.jayway.restassured.response.Response
import cucumber.api.DataTable
import cucumber.api.Scenario
import cucumber.api.java.After
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import net.thucydides.core.annotations.Managed
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.web.WebAppConfiguration
import uk.gov.digital.ho.proving.financial.ServiceRunner
import uk.gov.digital.ho.proving.financial.exception.ServiceExceptionHandler

import static com.jayway.restassured.RestAssured.given
import static java.util.concurrent.TimeUnit.SECONDS
import static steps.UtilitySteps.clickRadioButton
import static steps.UtilitySteps.toCamelCase

/**
 * @Author Home Office Digital
 */
@SpringApplicationConfiguration([ServiceRunner.class, ServiceExceptionHandler.class])
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("test")
//@ActiveProfiles("endtoend")
class Steps {

    private static Logger LOGGER = LoggerFactory.getLogger(Steps.class);

    def testDataLoader

    @Value('${wiremock}')
    private Boolean wiremock;

    @Managed
    WebDriver driver;

    def defaultTimeout = 2000

    def delay = 500

    def uiHost = "localhost"
    def uiPort = 8001
    def uiRoot = "http://$uiHost:$uiPort/"

    def healthUriRegex = "/health"

    def wiremockPort = 8080

    def pageUrls = [
        'studentType'       : uiRoot,
        'doctorateQuery'    : uiRoot + '#/financial-status-query-doctorate',
        'non-doctorateQuery': uiRoot + '#/financial-status-query-non-doctorate',
        'pgddQuery'         : uiRoot + '#/financial-status-query-pgdd',
        'ssoQuery'         : uiRoot + '#/financial-status-query-sso'
    ]

    def pageLocations = [
        'studentType'       : '#/financial-status-student-type',
        'doctorateQuery'    : '#/financial-status-query-doctorate',
        'pgddQuery'         : '#/financial-status-query-pgdd',
        'ssoQuery'         : '#/financial-status-query-sso',
        'non-doctorateQuery': '#/financial-status-query-non-doctorate',
        'accountNotFound'   : '#/financial-status-no-record'
    ]

    def thresholdUrlRegex = "/pttg/financialstatusservice/v1/maintenance/threshold*"
    def balanceCheckUrlRegex = "/pttg/financialstatusservice/v1/accounts.*"

    def sortCodeParts = ["First", "Second", "Third"]
    def sortCodeDelimiter = "-"

    def dateParts = ["Day", "Month", "Year"]
    def dateDelimiter = "/"

    def inLondonRadio = new UtilitySteps.RadioButtonConfig()
        .withOption('yes', 'inLondon-1')
        .withOption('no', 'inLondon-2')

    def studentTypeRadio = new UtilitySteps.RadioButtonConfig()
        .withOption('non-doctorate', 'studentType-1')
        .withOption('doctorate', 'studentType-2')
        .withOption('pgdd', 'studentType-3')
        .withOption('sso', 'studentType-4')

    def studentType

    @Before
    def setUp(Scenario scenario) {
        if (wiremock) {
            testDataLoader = new WireMockTestDataLoader(wiremockPort)
        }
    }

    @After
    def tearDown() {
        testDataLoader?.stop()
    }


    def sendKeys(WebElement element, String v) {
        element.clear();
        if (v != null && v.length() != 0) {
            element.sendKeys(v);
        }
    }

    private def fillOrClearBySplitting(String key, String input, List<String> partNames, String delimiter) {

        if (input != null && input.length() != 0) {
            fillPartsBySplitting(key, input, delimiter, partNames)

        } else {
            clearParts(key, partNames)
        }
    }

    private def fillPartsBySplitting(String key, String value, String delimiter, List<String> partNames) {

        String[] parts = value.split(delimiter)

        parts.eachWithIndex { part, index ->
            sendKeys(driver.findElement(By.id(key + partNames[index])), part)
        }
    }

    private def clearParts(String key, List<String> partNames) {
        partNames.each { part ->
            driver.findElement(By.id(key + part)).clear()
        }
    }

    private def assertCurrentPage(String location) {

        driver.sleep(200)

        def expected = pageLocations[location]
        def actual = driver.currentUrl

        assert actual.contains(expected): "Expected current page location to contain text: '$expected' but actual page location was '$actual' - Something probably went wrong earlier"
    }

    private void verifyTableRowHeadersInOrder(DataTable expectedResult, tableId) {

        WebElement tableElement = driver.findElement(By.id(tableId))

        def entriesAsList = expectedResult.asList(String.class)

        entriesAsList.eachWithIndex { v, index ->
            def oneBasedIndex = index + 1;
            def result = tableElement.findElements(By.xpath(".//tbody/tr[$oneBasedIndex]/th[contains(., '$v')]"))
            assert result: "Could not find header [$v] for Results table row, [$oneBasedIndex] "
        }
    }

    private void assertTextFieldEqualityForTable(DataTable expectedResult) {
        Map<String, String> entries = expectedResult.asMap(String.class, String.class)
        assertTextFieldEqualityForMap(entries)
    }

    private Map<String, String> assertTextFieldEqualityForMap(Map<String, String> entries) {

        entries.each { k, v ->
            String fieldName = toCamelCase(k);
            WebElement element = driver.findElement(By.id(fieldName))

            assert element.getText() == v
        }
    }

    private void submitEntries(Map<String, String> entries) {
        entries.each { k, v ->
            String key = toCamelCase(k)

            if (key.endsWith("Date")) {
                fillOrClearBySplitting(key, v, dateParts, dateDelimiter)

            } else if (key.equals("sortCode")) {
                fillOrClearBySplitting(key, v, sortCodeParts, sortCodeDelimiter)

            } else {
                def element = driver.findElement(By.id(key))

                if (key == "inLondon") {
                    clickRadioButton(driver, inLondonRadio, v)

                } else if (key == "studentType") {
                    clickRadioButton(driver, studentTypeRadio, v)
                } else {
                    sendKeys(element, v)
                }
            }
        }

        driver.sleep(delay)
        driver.findElement(By.className("button")).click()
    }

    private void chooseAndSubmitStudentType(String type) {
        selectStudentType(type)
        submitStudentTypeChoice()
    }

    private void selectStudentType(String type) {
        clickRadioButton(driver, studentTypeRadio, type)
    }

    private void submitStudentTypeChoice() {
        driver.findElement(By.className("button")).click()
    }

    def responseStatusFor(String url) {
        Response response = given()
            .get(url)
            .then().extract().response();

        return response.getStatusCode();
    }

    @Given("^(?:caseworker|user) is using the financial status service ui\$")
    public void user_is_using_the_financial_status_service_ui() throws Throwable {
        driver.get(uiRoot)
        assertCurrentPage('studentType')
    }

    @Given("^the (.*) student type is chosen\$")
    public void the_student_type_is_chosen(String type) {
        studentType = type
        chooseAndSubmitStudentType(type)
    }

    @Given("^the account has sufficient funds\$")
    public void the_account_has_sufficient_funds() {
        testDataLoader.stubTestData("dailyBalancePass", balanceCheckUrlRegex)
        testDataLoader.stubTestData("threshold", thresholdUrlRegex)
    }

    @Given("^the account does not have sufficient funds\$")
    public void the_account_does_not_have_sufficient_funds() {
        testDataLoader.stubTestData("threshold", thresholdUrlRegex)
        testDataLoader.stubTestData("dailyBalanceFail-low-balance", balanceCheckUrlRegex)
    }

    @Given("^the account does not have sufficient records\$")
    public void the_account_does_not_have_sufficient_records() {
        testDataLoader.stubTestData("threshold", thresholdUrlRegex)
        testDataLoader.stubTestData("dailyBalanceFail-record-count", balanceCheckUrlRegex)
    }

    @Given("^the api response is delayed for (\\d+) seconds\$")
    public void the_api_response_is_delayed_for_seconds(int delay) throws Throwable {
        testDataLoader.withDelayedResponse(thresholdUrlRegex, delay)
    }

    @Given("^the api response is garbage\$")
    public void the_api_response_is_garbage() throws Throwable {
        testDataLoader.withGarbageResponse(thresholdUrlRegex)
    }

    @Given("^the api response is empty\$")
    public void the_api_response_is_empty() throws Throwable {
        testDataLoader.withEmptyResponse(thresholdUrlRegex)
    }

    @Given("^the api response has status (\\d+)\$")
    public void the_api_response_has_status(int status) throws Throwable {
        testDataLoader.withResponseStatus(thresholdUrlRegex, status)
    }

    @Given("^the api health check response has status (\\d+)\$")
    public void the_api_healthcheck_response_has_status(int status) throws Throwable {
        testDataLoader.withResponseStatus(healthUriRegex, status)
    }

    @Given("^the api is unreachable\$")
    public void the_api_is_unreachable() throws Throwable {
        testDataLoader.withServiceDown()
    }

    @Given("^the api response is a validation error - (.*) parameter\$")
    public void the_api_response_is_a_validation_error(String type) throws Throwable {
        testDataLoader.stubErrorData("validation-error-$type", thresholdUrlRegex, 400)
    }

    @Given("^no record for the account\$")
    public void no_record_for_the_account() throws Throwable {
        testDataLoader.stubTestData("threshold", thresholdUrlRegex)
        testDataLoader.withResponseStatus(balanceCheckUrlRegex, 404)
    }

    @When("^the student type choice is submitted\$")
    public void the_student_type_choice_is_submitted() {
        submitStudentTypeChoice()
    }

    @When("^the financial status check is performed\$")
    public void the_financial_status_check_is_performed() throws Throwable {

        Map<String, String> validDefaultEntries = [
            'End date'                       : '30/05/2016',
            'In London'                      : 'Yes',
            'Accommodation fees already paid': '0',
            'Number of dependants'           : '1',
            'Sort code'                      : '11-11-11',
            'Account number'                 : '11111111',
        ]

        if (studentType.equalsIgnoreCase('non-doctorate')) {
            validDefaultEntries['Total tuition fees'] = '1';
            validDefaultEntries['Tuition fees already paid'] = '0';
        }

        if (!studentType.equalsIgnoreCase('doctorate')) {
            validDefaultEntries['Course start date'] = '30/05/2016';
            validDefaultEntries['Course end date'] = '30/06/2016';
        }

        submitEntries(validDefaultEntries)
    }

    @When("^the financial status check is performed with\$")
    public void the_financial_status_check_is_performed_with(DataTable arg1) throws Throwable {
        Map<String, String> entries = arg1.asMap(String.class, String.class)
        submitEntries(entries)
    }

    @When("^the caseworker views the (.*) page\$")
    public void the_caseworker_views_the_query_page(String pageName) throws Throwable {
        def url = pageUrls[toCamelCase(pageName)]
        driver.get(url)
        assertCurrentPage(toCamelCase(pageName))
    }

    @When("^after at least (\\d+) seconds\$")
    def after_at_least_x_seconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (Exception e) {
            assert false: 'Sleep interrupted'
        }
    }

    @Then("^the service displays the following message\$")
    public void the_service_displays_the_following_message(DataTable arg1) throws Throwable {
        Map<String, String> entries = arg1.asMap(String.class, String.class)
        assertTextFieldEqualityForMap(entries)
    }

    @Then("^the service displays the (.*) page\$")
    public void the_service_displays_the_named_page(String pageName) throws Throwable {
        assertCurrentPage(toCamelCase(pageName))
    }

    @Then("^the service displays the (.*) page sub heading\$")
    public void the_service_displays_the_page_sub_heading(String pageSubHeading) throws Throwable {
        assertTextFieldEqualityForMap(['page sub heading': pageSubHeading])
    }

    @Then("^the service displays the following your search data\$")
    public void the_service_displays_the_following_your_search_date(DataTable expectedResult) throws Throwable {
        assertTextFieldEqualityForTable(expectedResult)
    }

    @Then("^the service displays the following result\$")
    public void the_service_displays_the_following_result(DataTable expectedResult) throws Throwable {
        assertTextFieldEqualityForTable(expectedResult)
    }

    @Then("^the service displays the following page content\$")
    public void the_service_displays_the_following_page_content(DataTable expectedResult) throws Throwable {
        assertTextFieldEqualityForTable(expectedResult)
    }

    @Then("^the service displays the following page content within (\\d+) seconds\$")
    public void the_service_displays_the_following_page_content_within_seconds(long timeout, DataTable expectedResult) throws Throwable {
        driver.manage().timeouts().implicitlyWait(timeout, SECONDS)
        assertTextFieldEqualityForTable(expectedResult)
        driver.manage().timeouts().implicitlyWait(defaultTimeout, SECONDS)
    }

    @Then("^the service displays the following (.*) headers in order\$")
    public void the_service_displays_the_following_your_search_headers_in_order(String tableName, DataTable expectedResult) throws Throwable {
        def tableId = toCamelCase(tableName) + "Table"
        verifyTableRowHeadersInOrder(expectedResult, tableId)
    }

    @Then("^the error summary list contains the text\$")
    public void the_error_summary_list_contains_the_text(DataTable expectedText) {

        List<String> errorSummaryTextItems = expectedText.asList(String.class)

        WebElement errorSummaryList = driver.findElement(By.id("error-summary-list"))
        def errorText = errorSummaryList.text

        errorSummaryTextItems.each {
            assert errorText.contains(it): "Error text did not contain: $it"
        }
    }

    @Then("^the connection attempt count should be (\\d+)\$")
    def the_connection_attempt_count_should_be_count(int count) {
        testDataLoader.verifyGetCount(count, thresholdUrlRegex)
    }

    @Then("^the health check response status should be (\\d+)\$")
    def the_response_status_should_be(int expected) {

        def result = getHealthCheckStatus()

        // Sometimes needs a retry, not sure why
        2.times {
            if (result != expected) {
                sleep(500)
                result = getHealthCheckStatus()
            }
        }

        assert result == expected
    }

    private int getHealthCheckStatus() {
        responseStatusFor(uiRoot + "health")
    }
}
