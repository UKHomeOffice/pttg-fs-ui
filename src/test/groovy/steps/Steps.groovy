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
import org.openqa.selenium.*
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

    def healthUriRegex = "/healthz"

    def wiremockPort = 8080

    def pageUrls = [
        'root'              : uiRoot + '#!/fs',
        'tier2'             : uiRoot + '#!/fs/t2',
        'tier4'             : uiRoot + '#!/fs/t4',
        'tier5'             : uiRoot + '#!/fs/t5',
        'studentType'       : uiRoot,
        'doctorate'         : uiRoot + '#!/fs/t4/doctorate',
        'non-doctorate'     : uiRoot + '#!/fs/t4/non-doctorate',
        'pgdd'              : uiRoot + '#!/financial-status/pgdd',
        'sso'               : uiRoot + '#!/financial-status/sso',
        't2main'            : uiRoot + '#!/financial-status/t2main',
        't2dependant'       : uiRoot + '#!/financial-status/t2dependant',
        'studentTypeCalc'   : uiRoot + '#!/financial-status-calc',
    ]

     def applicantType = [
            'mainApplicant'          : 'applicant-type-main-label',
            'dependentOnly'         :  'applicant-type-dependant-label',
            'nonDoctorate'           :  'applicant-type-nondoctorate-label'
    ]

    def pageObjects = [
            'continueButtonClass'       : 'button',
            'yesCheckBarclays'          : 'doCheck-yes-label',
             'no'                       : 'doCheck-no-label'
    ]

//    def pageLocations = [
//        'studentType'       : '#!/financial-status',
//        'doctorateQuery'    : '#!/financial-status/doctorate',
//        'pgddQuery'         : '#!/financial-status/pgdd',
//        'ssoQuery'          : '#!/financial-status/sso',
//        'non-doctorateQuery': '#!/financial-status/non-doctorate',
//        'accountNotFound'   : '#!/financial-status/no-record',
//
//        'studentTypeCalc'       : '#!/financial-status-calc-student-type',
//    ]

//                             /pttg/financialstatus/v1/t4/threshold?accommodationFeesAlreadyPaid=0&applicantType=nondoctorate&applicationRaisedDate=2016-06-05&continuationCourse=no&courseEndDate=2016-11-30&courseStartDate=2016-05-30&courseType=main&dependants=1&endDate=2016-05-30&inLondon=yes&originalCourseStartDate=&studentType=nondoctorate&totalTuitionFees=8500.00&tuitionFeesAlreadyPaid=0
    def thresholdUrlRegex = "/pttg/financialstatus/v1/t4/maintenance/threshold*"
    def consentCheckUrlRegex = "/pttg/financialstatus/v1/accounts/\\d{6}/\\d{8}/consent*"
    def balanceCheckUrlRegex = "/pttg/financialstatus/v1/accounts/\\d{6}/\\d{8}/dailybalancestatus*"


    def sortCodeParts = ["Part1", "Part2", "Part3"]
    def sortCodeDelimiter = "-"

    def dateParts = ["Day", "Month", "Year"]
    def dateDelimiter = "/"

    def inLondonRadio = new UtilitySteps.RadioButtonConfig()
        .withOption('yes', 'inLondon-yes-label')
        .withOption('no', 'inLondon-no-label')

    def studentTypeRadio = new UtilitySteps.RadioButtonConfig()
        .withOption('non-doctorate', 'applicant-type-nondoctorate-label')
        .withOption('doctorate', 'applicant-type-doctorate-label')
        .withOption('pgdd', 'applicant-type-pgdd-label')
        .withOption('sso', 'applicant-type-sso-label')
        .withOption('t2main', 'applicant-type-t2main')
        .withOption('t2dependant', 'applicant-type-t2dependant')
        .withOption('t5main', 'applicant-type-t5main')
        .withOption('t5dependant', 'applicant-type-t5dependant')

    def courseTypeRadio = new UtilitySteps.RadioButtonConfig()
        .withOption('pre-sessional', 'courseType-presessional-label')
        .withOption('main', 'courseType-main-label')

    def continuationCourseRadio = new UtilitySteps.RadioButtonConfig()
        .withOption('yes', 'continuationCourse-yes-label')
        .withOption('no', 'continuationCourse-no-label')

    def studentType

    def defaultFields

    @Before
    def setUp(Scenario scenario) {
        if (wiremock) {
            testDataLoader = new WireMockTestDataLoader(wiremockPort)
        }
    }

    @After
    def tearDown() {
        testDataLoader.stop()
    }


    def sendKeys(WebElement element, String v) {
        element.clear();
        if (v != null && v.length() != 0) {
            element.sendKeys(v);
        }
    }

    private def assertRadioSelection(radioConfig, String v) {
        String choice = v.toLowerCase()
        if (radioConfig.options.containsKey(choice)) {
            String id = radioConfig.options.get(choice)
            WebElement element = driver.findElement(By.cssSelector("[id='$id'][type='radio']"))
            assert element.isSelected() == true
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
            if(driver.findElement(By.id(key + partNames[index])).isDisplayed()) {
                sendKeys(driver.findElement(By.id(key + partNames[index])), part)
            }
        }
    }

    private def clearParts(String key, List<String> partNames) {
        partNames.each { part ->
            driver.findElement(By.id(key + part)).clear()
        }
    }

    private def assertCurrentPage(String location) {

        driver.sleep(200)

        def expected = pageUrls[location]
        def actual = driver.currentUrl

        assert actual.contains(expected): "Expected current page location to contain text: '$expected' but actual page location was '$actual' - Something probably went wrong earlier"
    }

    private def assertDate(String fieldName, String v) {
        String fieldval = ''
        dateParts.each { part ->
            fieldval += '/' + driver.findElement(By.id(fieldName + part)).getAttribute("value").padLeft(2, '0')
        }
        assert fieldval.substring(1) == v
    }

    private assertSortcode(String fieldName, String v) {
        String fieldval = '';
        sortCodeParts.each { part ->
            fieldval += '-' + driver.findElement(By.id(fieldName + part)).getAttribute("value").padLeft(2, '0')
        }
        assert fieldval.substring(1) == v
    }

    private void verifyTableRowHeadersInOrder(DataTable expectedResult, tableId) {

        WebElement tableElement = driver.findElement(By.id(tableId))

        def entriesAsList = expectedResult.asList(String.class)

        entriesAsList.eachWithIndex { v, index ->
            def oneBasedIndex = index + 1;
            def result = tableElement.findElements(By.xpath(".//tbody/tr[$oneBasedIndex]/th[contains(., '$v')]"))
            assert result: "Could not find header [$v] for $tableId table row, [$oneBasedIndex] "
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

    private void assertInputValueEqualityForTable(DataTable expectedResult) {
        Map<String, String> entries = expectedResult.asMap(String.class, String.class)
        assertInputValueEqualityForMap(entries)
    }

    private Map<String, String> assertInputValueEqualityForMap(Map<String, String> entries) {

        entries.each { k, v ->
            String fieldName = toCamelCase(k);
            if (fieldName.endsWith("Date") || fieldName.equals("dob")) {
                assertDate(fieldName, v)

            } else if (fieldName.equals("sortCode")) {
                assertSortcode(fieldName, v)

            } else if (fieldName == "inLondon") {
                assertRadioSelection(inLondonRadio, v)

            } else if (fieldName == "studentType") {
                assertRadioSelection(studentTypeRadio, v)

            } else if (fieldName == "continuationCourse") {
                assertRadioSelection(continuationCourseRadio, v)

            } else if (fieldName == "courseType") {
                assertRadioSelection(courseTypeRadio, v)

            } else {
                assert driver.findElement(By.id(fieldName)).getAttribute("value") == v
            }
        }
    }

    private void makeEntries(Map<String, String> entries) {
        entries.each { k, v ->
            String key = toCamelCase(k)

            if (key.endsWith("Date") || key.equals("dob")) {
                fillOrClearBySplitting(key, v, dateParts, dateDelimiter)

            } else if (key.equals("sortCode")) {
                fillOrClearBySplitting(key, v, sortCodeParts, sortCodeDelimiter)

            } else {

                if (key == "inLondon") {
                    clickRadioButton(driver, inLondonRadio, v)

                } else if (key == "studentType") {
                    clickRadioButton(driver, studentTypeRadio, v)

                } else if (key == "continuationCourse") {
                    clickRadioButton(driver, continuationCourseRadio, v)

                }
                else if(key == "accountNumber"){
                    driver.findElement(By.id(key)).clear()
                    driver.findElement(By.id(key)).sendKeys(v)
                }else if (key == "courseType") {
                    clickRadioButton(driver, courseTypeRadio, v)

                } else {
                    def element = driver.findElement(By.id(key))
                    if(element.isDisplayed() == true) {
                        sendKeys(element, v)
                    }
                }
            }
        }
    }



    private void submitEntries(Map<String, String> entries) {
        makeEntries(entries)

        driver.sleep(delay)
        driver.findElement(By.className("button")).click()
    }

    public void chooseAndSubmitStudentType(String type) {
        selectStudentType(type)
        submitStudentTypeChoice()
    }

    public void selectStudentType(String type) {
        clickRadioButton(driver, studentTypeRadio, type)
    }

    public void submitStudentTypeChoice() {
        driver.findElement(By.className("button")).click()
    }

    def responseStatusFor(String url) {
        Response response = given()
            .get(url)
            .then().extract().response();

        return response.getStatusCode();
    }

    @Given("^(?:caseworker|user) is using the ([a-zA-Z ]*)ui\$")
    public void user_is_using_the_ui(String service) throws Throwable {
        if (service.trim() == 'financial status calculator service') {
            driver.get(pageUrls['studentTypeCalc'])
            driver.navigate().refresh()
            assertCurrentPage('studentTypeCalc')
        } else if (service.trim() == 'financial status service') {
            driver.get(pageUrls['root'])
            driver.navigate().refresh()
            assertCurrentPage('root')
        } else {
            assert false
        }
    }

    @Given("^the (.*) student type is chosen\$")
    public void the_student_type_is_chosen(String type) {
        studentType = type
        chooseAndSubmitStudentType(type)
    }

    @Given("^the default details are\$")
    public void the_default_details_are(DataTable arg1) throws Throwable {
        defaultFields = arg1
    }



    @Given("^the api consent response will be (FAILURE|SUCCESS|PENDING)\$")
    public void the_api_consent_response_will_be(String consentValue) throws Throwable {
        testDataLoader.stubTestData("consentcheckresponse-" + consentValue, consentCheckUrlRegex)
    }

    @Given("^the api daily balance response will(.+)\$")
    public void the_api_daily_balance_reponse_will_be_for_account(String ref) throws Throwable {
        testDataLoader.stubTestData('dailyBalance' + ref.trim(), balanceCheckUrlRegex)
    }

    @Given("^the api threshold response will be (.+)\$")
    public void the_api_threshold_response_will_be(String ref) throws Throwable {
        testDataLoader.stubTestData('threshold-' +ref, thresholdUrlRegex)
    }




    @Given("^the account has sufficient funds for tier (\\d)\$")
    public void the_account_has_sufficient_funds_for_tier(int tier) {
        println "tier " + tier
        testDataLoader.stubTestData("dailyBalancePass", balanceCheckUrlRegex)
        testDataLoader.stubTestData("threshold-t" + tier, thresholdUrlRegex)
    }

    @Given("^the account has sufficient funds\$")
    public void the_account_has_sufficient_funds() {
        testDataLoader.stubTestData("dailyBalancePass", balanceCheckUrlRegex)
        testDataLoader.stubTestData("threshold", thresholdUrlRegex)
    }

    @Given("^the account does not have sufficient funds for tier (\\d)\$")
    public void the_account_does_not_have_sufficient_funds_for_tier(int tier) {
        testDataLoader.stubTestData("threshold-t" + tier, thresholdUrlRegex)
        testDataLoader.stubTestData("dailyBalanceFail-low-balance-t" + tier, balanceCheckUrlRegex)
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

    @Given("^the api is back online\$")
    public void the_api_is_back_online() throws Throwable {
        testDataLoader.withServiceUp();
        driver.sleep(3000)
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

    @Given("^consent is sought for the following:\$")
    public void consent_is_sought_for_the_following(DataTable arg1){
        Map<String, String> entries = arg1.asMap(String.class, String.class)
        makeEntries(entries)
        driver.findElement(By.className(pageObjects['continueButtonClass'])).click()

    }
    @Given("^the caseworker selects the (.*) radio button\$")
    public void the_caseworker_selects_the_Yes_check_Barclays_radio_button(String bankRadioButton) {
Thread.sleep(4000)
        if(bankRadioButton == "Yes, check Barclays") {
            driver.findElement(By.id(pageObjects['yesCheckBarclays'])).click()
            driver.findElement(By.className(pageObjects['continueButtonClass'])).click()
        }

        if(bankRadioButton == "No, check Barclays") {
            driver.findElement(By.id(pageObjects['no'])).click()
            driver.findElement(By.className(pageObjects['continueButtonClass'])).click()
        }

    }

    @Given("^the caseworker selects (Tier.*)\$")
    public void the_caseworker_selects_Tier(String tierType) throws Throwable {

        if(tierType == "Tier two") {
            driver.get(pageUrls['tier2'])
        }

        if(tierType == "Tier four"){
            driver.get(pageUrls['tier4'])
        }

        if(tierType == "Tier five"){

            driver.get(pageUrls['tier5'])
        }

    }

    @Given("^(.*) type is selected\$")
    public void main_type_is_selected(String applicant) throws Throwable {
        if(applicant == "Main"){

            driver.findElement(By.id(applicantType['mainApplicant'])).click()
            driver.findElement(By.className(pageObjects['continueButtonClass'])).click()

        }
        if(applicant == "Non Doctorate"){

            driver.findElement(By.id(applicantType['nonDoctorate'])).click()
            driver.findElement(By.className(pageObjects['continueButtonClass'])).click()

        }
        if(applicant == "Dependent"){

            driver.findElement(By.id(applicantType['dependentOnly'])).click()
            driver.findElement(By.className(pageObjects['continueButtonClass'])).click()

        }
    }

    @When("^the student type choice is submitted\$")
    public void the_student_type_choice_is_submitted() {
        submitStudentTypeChoice()
    }

    @When("^the financial status check is performed\$")
    public void the_financial_status_check_is_performed() throws Throwable {
        Map<String, String> validDefaultEntries
        if (defaultFields) {
            validDefaultEntries = defaultFields.asMap(String.class, String.class)
        } else {
            validDefaultEntries = [
                'Application raised date'        : '29/06/2016',
                'End date'                       : '30/05/2016',
                'In London'                      : 'Yes',
                'Accommodation fees already paid': '0',
                'Dependants'                     : '1',
                'Sort code'                      : '11-11-11',
                'Account number'                 : '11111111',
                'dob'                            : '29/07/1978',
                'Continuation course'            : 'No'
            ]

            if (studentType.equalsIgnoreCase('non-doctorate')) {
                validDefaultEntries['Total tuition fees'] = '1';
                validDefaultEntries['Tuition fees already paid'] = '0';
            }

            if (!studentType.equalsIgnoreCase('doctorate')) {
                validDefaultEntries['Course start date'] = '30/05/2016';
                validDefaultEntries['Course end date'] = '30/06/2016';
            }
        }
        submitEntries(validDefaultEntries)
    }

    @When("^these fields are updated with\$")
    public void these_field_are_updated_with(DataTable arg1) throws Throwable {
        Map<String, String> entries = arg1.asMap(String.class, String.class)
        makeEntries(entries)
    }

    @When("^the financial status check is performed with\$")
    public void the_financial_status_check_is_performed_with(DataTable arg1) throws Throwable {
        Map<String, String> entries = arg1.asMap(String.class, String.class)
        if (defaultFields) {
            Map<String, String> defaultEntries = defaultFields.asMap(String.class, String.class)
            submitEntries(defaultEntries + entries)
        } else {
            submitEntries(entries)
        }
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

    @When("^the submit button is clicked\$")
    public void the_submit_button_is_clicked() throws Throwable {
        driver.sleep(delay)
        driver.findElement(By.className("button")).click()
    }


    @When("^the new search button is clicked\$")
    public void the_new_search_button_is_clicked() {
        driver.sleep(delay)
        driver.findElement(By.className("newsearch")).click()
        //assertTextFieldEqualityForTable(expectedResult)
    }

    @When("^the edit search button is clicked\$")
    public void the_edit_search_button_is_clicked() {
        driver.sleep(delay)
        driver.findElement(By.className("yoursearch--edit")).click()
    }

    @When("^the copy button is clicked\$")
    public void the_copy_button_is_clicked() {
        driver.sleep(delay)
        driver.findElement(By.className("button--copy")).click()
    }
    @When("^the Consent API is invoked\$")
    public void the_Consent_API_is_invoked() {

    }


    @When("^the progress bar is displayed\$")
    public void the_progress_bar_is_displayed() throws Throwable {
        assert driver.findElement(By.xpath("//*[@id=\"content\"]/div[2]/div[1]/fs-timer/div")).isDisplayed()
    }

    @Then("^the service displays the following message\$")
    public void the_service_displays_the_following_message(DataTable arg1) {
        Map<String, String> entries = arg1.asMap(String.class, String.class)
        assertTextFieldEqualityForMap(entries)
    }

    @Then("^the service displays the following error message\$")
    public void the_service_displays_the_following_error_message(DataTable arg1) throws Throwable {
        Map<String, String> entries = arg1.asMap(String.class, String.class)
        assertTextFieldEqualityForMap(['validation-error-summary-heading': 'There\'s some invalid information']);
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

//    @Then("^the service displays the following your search data\$")
//    public void the_service_displays_the_following_your_search_date(DataTable expectedResult) throws Throwable {
//        assertTextFieldEqualityForTable(expectedResult)
//    }

    @Then("^the service displays the following result\$")
    public void the_service_displays_the_following_result(DataTable expectedResult) throws Throwable {
        driver.sleep(200)
        def actual = driver.currentUrl
        assert actual.contains('result'): "Expected current page location to be a result page but actual page location was '$actual' - Something probably went wrong earlier"
        assertTextFieldEqualityForTable(expectedResult)
    }


    @Then("^the result table contains the following\$")
    public void the_result_table_contains_the_following(DataTable arg1) throws Throwable {
        Map<String,String> entries = arg1.asMap(String.class,String.class)

        ArrayList<String> scenarioTable = new ArrayList<>()
        String[] resultTable = entries.keySet()

        for(String s:resultTable){
            scenarioTable.add(entries.get(s))
        }

        for (int j = 0; j < resultTable.size(); j++) {
            assert scenarioTable.contains(driver.findElement(By.id(toCamelCase(resultTable[j]))).getText())
        }


        int numRows = driver.findElements(By.xpath('//*[@id="resultsTable"]/tbody/tr')).size()

        for(int i=1; i <= numRows; i++) {

//            if (tr) {
                if (driver.findElement(By.id("resultTimestamp")).getText() != driver.findElement(By.xpath('//*[@id="resultsTable"]/tbody/tr[' + i + ']/td')).getText()) {
                    if (driver.findElement(By.xpath('//*[@id="resultsTable"]/tbody/tr[' + i + ']/td')).getText() == null) {
                        break;
                    }
                    assert scenarioTable.contains(driver.findElement(By.xpath('//*[@id="resultsTable"]/tbody/tr[' + i + ']/td')).getText())
                }
//            }
        }
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

    @Then("^the availability warning box should not be shown\$")
    public void the_availability_warning_box_should_not_be_shown() throws Throwable {
        driver.manage().timeouts().implicitlyWait(1, SECONDS)
        assert(driver.findElements(By.cssSelector(".availability")).isEmpty())
        driver.manage().timeouts().implicitlyWait(defaultTimeout, SECONDS)
    }

    @Then("^the inputs will be populated with\$")
    public void the_inputs_will_be_populated_with(DataTable expectedResult) {
        assertInputValueEqualityForTable(expectedResult)
    }

    @Then("^the connection attempt count should be (\\d+)\$")
    def the_connection_attempt_count_should_be_count(int count) {
        testDataLoader.verifyGetCount(count, thresholdUrlRegex)
    }

    @Then("^the readiness response status should be (\\d+)\$")
    def the_readiness_response_status_should_be(int expected) {
        assertStatusMatchFor("healthz", expected)
    }

    @Then("^the liveness response status should be (\\d+)\$")
    def the_liveness_response_status_should_be(int expected) {
        assertStatusMatchFor("ping", expected)
    }

    def assertStatusMatchFor(String endpoint, int expected){

        def result = responseStatusFor(uiRoot + endpoint)

        // Sometimes needs a retry, not sure why
        2.times {
            if (result != expected) {
                sleep(500)
                result = responseStatusFor(uiRoot + endpoint)
            }
        }

        assert result == expected
    }

    @Then("^the copy button text is '([^']*)'\$")
    public void the_copy_button_text_is(String expectedText) {
        def Capabilities cap = driver.getCapabilities()
        if (cap.browserName != 'phantomjs') {
            driver.sleep(delay)
            assert (driver.findElement(By.cssSelector('.button--copy')).getAttribute('value') == expectedText)
        } else {
            println("\n\nSKIPPED: WARNING COPY FUNCTION DOES NOT WORK IN PHANTOM JS\nTest step: 'the copy button text is'\n\n")
        }
    }

    @Then("^the copied text includes\$")
    public void the_copied_text_includes(DataTable expectedText) {
        def Capabilities cap = driver.getCapabilities()
        if (cap.browserName == 'firefox') {

            driver.executeScript("document.getElementById('content').appendChild(document.createElement('textarea'))")
            driver.executeScript("document.getElementsByTagName('textarea')[0].focus()")
            def e = driver.findElement(By.cssSelector('textarea'))
            e.sendKeys(Keys.chord(Keys.COMMAND, "v"))

            if (System.getProperty("os.name").toString() == 'Mac OS X') {
                e.sendKeys(Keys.chord(Keys.COMMAND, "v"))
            } else {
                e.sendKeys(Keys.chord(Keys.CONTROL, "v"))
            }

            def String pasted = e.getAttribute('value')
            def Map<String, String> entries = expectedText.asMap(String.class, String.class)
            entries.each { k, v ->
                assert (pasted.contains(k))
                assert (pasted.contains(v))
            }

        } else {
            println("\n\nSKIPPED: TESTING THE CONTENTS OF THE PASTE BUFFER\nONLY WORKS RELIABLY IN FIREFOX\nTest step: 'the copied text includes'\n\n")
        }
    }

    @Then("^the service displays the result page including the results and your search headers\$")
    public void the_service_displays_the_result_page_including_the_results_and_your_search_headers(DataTable arg1) {

    }
    @Then("^The service displays the (.*) output page including the results and your search headers\$")
    public void the_service_displays_the_Consent_has_not_been_given_output_page_including_the_results_and_your_search_headers(String consent) {
        assert driver.findElement(By.xpath("//*[@id=\"content\"]/div[2]/div[1]/h1")).getText() == consent
    }

    @Then("^the (.*) page is displayed\$")
    public void the_Consent_Pending_page_is_displayed(String consentPending) {

        assert driver.findElement(By.xpath("//*[@id=\"content\"]/div[2]/div[1]/h1")).getText() == consentPending
    }
}
