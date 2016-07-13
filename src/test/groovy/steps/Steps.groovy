package steps

import cucumber.api.DataTable
import cucumber.api.Scenario
import cucumber.api.java.After
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import groovy.json.JsonSlurper
import net.thucydides.core.annotations.Managed
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static java.util.concurrent.TimeUnit.SECONDS
import static steps.UtilitySteps.clickRadioButton
import static steps.UtilitySteps.toCamelCase

/**
 * @Author Home Office Digital
 */
class Steps {

    private static Logger LOGGER = LoggerFactory.getLogger(Steps.class);

    @Managed
    WebDriver driver;

    def defaultTimeout = 2000

    def delay = 500

    def uiHost = "localhost"
    def uiPort = 8001
    def uiUrl = "http://$uiHost:$uiPort/"

    def pageUrls = [
        'non-doctorate query': uiUrl + '?',  // todo what is the url?
        'student type'       : uiUrl
    ]

    def barclaysStubHost = "localhost"
    def barclaysStubPort = 8082
    def testDataLoader

    def pageLocations = [
        'studentType'    : '#/financial-status-query', // todo update this
        'accountNotFound': '#/financial-status-no-record'
    ]

    def thresholdUrlRegex = "/pttg/financialstatusservice/v1/maintenance/threshold*"
    def balanceCheckUrlRegex = "/pttg/financialstatusservice/v1/accounts.*"

    def sortCodeParts = ["First", "Second", "Third"]
    def sortCodeDelimiter = "-"

    def dateParts = ["Day", "Month", "Year"]
    def dateDelimiter = "/"

    def innerLondonRadio = new UtilitySteps.RadioButtonConfig()
        .withOption('Yes', 'innerLondonBorough-1')
        .withOption('No', 'innerLondonBorough-2')

    def studentTypeRadio = new UtilitySteps.RadioButtonConfig()
        .withOption('doctorate', 'studentType-1')
        .withOption('non-doctorate', 'studentType-2')

    @Before
    def setUp(Scenario scenario) {

        def isWireMock = scenario.getSourceTagNames().find {
            it.startsWith("@wiremock")
        }
        if (isWireMock) {
            testDataLoader = new WireMockTestDataLoader(barclaysStubHost, barclaysStubPort)
            testDataLoader.prepareFor(scenario)
        } else {
            testDataLoader = new TestDataLoader(barclaysStubHost, barclaysStubPort)
            testDataLoader.prepareFor(scenario)
        }
    }

    @After
    def tearDown() {
        testDataLoader?.clearTestData()
    }

    private def checkPrerequisites() {

        String healthCheckText = readHealthCheck()
        def health = new JsonSlurper().parseText(healthCheckText)

        assert health.status == "UP": "Health check failure. Are the UI, API and STUB all running? Healthcheck said: $healthCheckText"
    }

    private String readHealthCheck() {

        try {
            def healthCheckUrl = uiUrl + "health"
            return healthCheckUrl.toURL().text
        } catch (Exception e) {
            e.printStackTrace()
            assert false: "Could not connect to UI server at $healthCheckUrl. Is it running?"
        }
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

    private void assertTextFieldEqualityForMap(DataTable expectedResult) {
        Map<String, String> entries = expectedResult.asMap(String.class, String.class)

        entries.each { k, v ->

            String fieldName = toCamelCase(k);
            WebElement element = driver.findElement(By.id(fieldName))

            assert v.contains(element.getText())
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

                if (key == "innerLondonBorough") {
                    clickRadioButton(driver, innerLondonRadio, v)

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

    private void selectStudentType(String type){
        clickRadioButton(driver, studentTypeRadio, type)
    }

    private void submitStudentTypeChoice(){
        driver.findElement(By.className("button")).click()
    }

    @Given("^(?:caseworker|user) is using the financial status service ui\$")
    public void user_is_using_the_financial_status_service_ui() throws Throwable {
        driver.get(uiUrl)
        assertCurrentPage('studentType')
    }

    @Given("^the (.*) student type is chosen\$")
    public void the_student_type_is_chosen(String type) {
        chooseAndSubmitStudentType(type)
    }

    @Given("^the test data for account (.+)\$")
    public void the_test_data_for_account_number(String fileName) {
        testDataLoader.loadTestData(fileName)
    }

    @Given("^the account has sufficient funds\$")
    public void the_account_has_sufficient_funds() {
        testDataLoader.stubTestData("dailyBalancePass", balanceCheckUrlRegex)
        testDataLoader.stubTestData("threshold", thresholdUrlRegex)
    }

    @Given("^the account does not have sufficient funds\$")
    public void the_account_does_not_have_sufficient_funds() {
        testDataLoader.stubTestData("dailyBalanceFail", balanceCheckUrlRegex)
        testDataLoader.stubTestData("threshold", thresholdUrlRegex)
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

    @Given("^the api is unreachable\$")
    public void the_api_is_unreachable() throws Throwable {
        testDataLoader.withServiceDown()
    }

    @Given("^no record for the account\$")
    public void no_record_for_the_account() throws Throwable {
        testDataLoader.stubTestData("threshold", thresholdUrlRegex)
        testDataLoader.withResponseStatus(balanceCheckUrlRegex, 404)
    }

    @When("^the student type choice is submitted\$")
    public void the_student_type_choice_is_submitted(){
        submitStudentTypeChoice()
    }

    @When("^the financial status check is performed\$")
    public void the_financial_status_check_is_performed() throws Throwable {

        Map<String, String> validDefaultEntries = [
            'End date'                       : '30/05/2016',
            'Inner London borough'           : 'Yes',
            'Course length'                  : '1',
            'Total tuition fees'             : '1',
            'Tuition fees already paid'      : '0',
            'Accommodation fees already paid': '0',
            'Sort code'                      : '11-11-11',
            'Account number'                 : '11111111',
        ]

        submitEntries(validDefaultEntries)
    }

    @When("^the financial status check is performed with\$")
    public void the_financial_status_check_is_performed_with(DataTable arg1) throws Throwable {
        Map<String, String> entries = arg1.asMap(String.class, String.class)
        submitEntries(entries)
    }

    @When("^the caseworker views the (.*) page\$")
    public void the_caseworker_views_the_query_page(String pageName) throws Throwable {
        driver.get(pageUrls[pageName])
        assertCurrentPage(pageName + 'Page')
    }

    @Then("^the service displays the following message\$")
    public void the_service_displays_the_following_message(DataTable arg1) throws Throwable {

        Map<String, String> entries = arg1.asMap(String.class, String.class)

        entries.each { k, v ->
            LOGGER.debug("\nChecking {}:{}", toCamelCase(k), v)
            assert driver.findElement(By.id(toCamelCase(k))).getText() == v
        }
    }

    @Then("^the service displays the (.*) page\$")
    public void the_service_displays_the_named_page(String pageName) throws Throwable {
        assertCurrentPage(toCamelCase(pageName))
    }

    @Then("^the service displays the (.*) page heading\$")
    public void the_service_displays_the_page_heading(String pageHeading) throws Throwable {
        assertTextFieldEqualityForMap(['pageHeading' : pageHeading])
    }

    @Then("^the service displays the following your search data\$")
    public void the_service_displays_the_following_your_search_date(DataTable expectedResult) throws Throwable {
        assertTextFieldEqualityForMap(expectedResult)
    }

    @Then("^the service displays the following result\$")
    public void the_service_displays_the_following_result(DataTable expectedResult) throws Throwable {
        assertTextFieldEqualityForMap(expectedResult)
    }

    @Then("^the service displays the following page content\$")
    public void the_service_displays_the_following_page_content(DataTable expectedResult) throws Throwable {
        assertTextFieldEqualityForMap(expectedResult)
    }

    @Then("^the service displays the following page content within (\\d+) seconds\$")
    public void the_service_displays_the_following_page_content_within_seconds(long timeout, DataTable expectedResult) throws Throwable {
        driver.manage().timeouts().implicitlyWait(timeout, SECONDS)
        assertTextFieldEqualityForMap(expectedResult)
        driver.manage().timeouts().implicitlyWait(defaultTimeout, SECONDS)
    }


    @Then("^the service displays the following (.*) headers in order\$")
    public void the_service_displays_the_following_your_search_headers_in_order(String tableName, DataTable expectedResult) throws Throwable {

        def tableId = toCamelCase(tableName) + "Table"

        verifyTableRowHeadersInOrder(expectedResult, tableId)
    }

}
