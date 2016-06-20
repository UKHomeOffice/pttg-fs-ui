package steps

import cucumber.api.DataTable
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import groovy.json.JsonOutput
import groovyx.net.http.ContentType
import groovyx.net.http.URIBuilder
import net.thucydides.core.annotations.Managed
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.text.WordUtils
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import groovyx.net.http.RESTClient

import java.text.SimpleDateFormat

/**
 * @Author Home Office Digital
 */
class Steps {

    def host = "localhost"
    def port = 8001

    def rootContextUrl(){
        return new URIBuilder("/").setHost(host).setPort(port).setScheme("http").toString()
    }

    def queryPageLocation = '#/financial-status-query'
    def resultsPageLocation = '#/financial-status-result'

    @Managed
    public WebDriver driver;

    private int delay = 500

    def sortCodeParts = ["First", "Second", "Third"]
    def sortCodeDelimiter = "-"

    def dateParts = ["Day", "Month", "Year"]
    def dateDelimiter = "/"

    def String toCamelCase(String s) {
        String allUpper = StringUtils.remove(WordUtils.capitalizeFully(s), " ")
        String camelCase = allUpper[0].toLowerCase() + allUpper.substring(1)
        camelCase

//        if(s.isEmpty()) return ""
//        def words = s.tokenize(" ")*.toLowerCase()*.capitalize().join("")
//        words[0].toLowerCase() + words.substring(1)
    }

    def parseDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")
        Date date = sdf.parse(dateString)
        date
    }

    def sendKeys(WebElement element, String v) {
        element.clear();
        if (v != null && v.length() != 0) {
            element.sendKeys(v);
        }
    }

    private void fillOrClearBySplitting(String key, String input, List<String> partNames, String delimiter) {

        if (input != null && input.length() != 0) {
            fillPartsBySplitting(key, input, delimiter, partNames)

        } else {
            clearParts(key, partNames)
        }
    }

    private void fillPartsBySplitting(String key, String value, String delimiter, List<String> partNames) {

        String[] parts = value.split(delimiter)

        parts.eachWithIndex { part, index ->
            sendKeys(driver.findElement(By.id(key + partNames[index])), part)
        }
    }

    private void clearParts(String key, List<String> partNames) {
        partNames.each { part ->
            driver.findElement(By.id(key + part)).clear()
        }
    }

    @Given("^(?:caseworker|user) is using the financial status service ui\$")
    public void user_is_using_the_financial_status_service_ui() throws Throwable {
        driver.get(rootContextUrl());
    }

    @When("^the financial status check is performed with\$")
    public void the_financial_status_check_is_performed_with(DataTable arg1) throws Throwable {

        assert driver.currentUrl.contains(queryPageLocation)

        Map<String, String> entries = arg1.asMap(String.class, String.class)

        entries.each { k, v ->
            String key = toCamelCase(k)

            if (key.endsWith("Date")) {
                fillOrClearBySplitting(key, v, dateParts, dateDelimiter)

            } else if (key.equals("sortCode")) {
                fillOrClearBySplitting(key, v, sortCodeParts, sortCodeDelimiter)

            } else {
                sendKeys(driver.findElement(By.id(key)), v)

            }
        }

        driver.sleep(delay)
        driver.findElement(By.className("button")).click();
    }

    @Then("^the service displays the following message\$")
    public void the_service_displays_the_following_message(DataTable arg1) throws Throwable {

        assert driver.currentUrl.contains(queryPageLocation)

        Map<String, String> entries = arg1.asMap(String.class, String.class)

        assert driver.findElement(By.id(entries.get("Error Field"))).getText() == entries.get("Error Message")
    }

    @Then("^the service displays the following result\$")
    public void the_service_displays_the_following_result(DataTable expectedResult) throws Throwable {

        assert driver.currentUrl.contains(resultsPageLocation)

        Map<String, String> entries = expectedResult.asMap(String.class, String.class)

        entries.each { k, v ->

            String fieldName = toCamelCase(k);

            WebElement element = driver.findElement(By.id(fieldName))

            assert v.contains(element.getText())
        }
    }
}
