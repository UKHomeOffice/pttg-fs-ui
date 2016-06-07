package steps

import cucumber.api.DataTable
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import net.thucydides.core.annotations.Managed
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.text.WordUtils
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

import java.text.SimpleDateFormat

/**
 * @Author Home Office Digital
 */
class Steps {

    @Managed
    public WebDriver driver;

    private int delay = 500

    def String toCamelCase(String s) {
        String allUpper = StringUtils.remove(WordUtils.capitalizeFully(s), " ")
        String camelCase = allUpper[0].toLowerCase() + allUpper.substring(1)
        camelCase
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

    @Given("^a student with\$")
    public void a_student_with(DataTable arg1) throws Throwable {

        println "Setting up student"
    }

    @Given("^a course with\$")
    public void a_course_with(DataTable arg1) throws Throwable {

        println "Setting up course"
    }

    @When("^the financial status check is performed\$")
    public void the_financial_status_check_is_performed() throws Throwable {

        driver.get("http://localhost:8000");

        println "Doing it"
    }

    @Then("^The service provides the following result\$")
    public void the_service_provides_the_following_result(DataTable arg1) throws Throwable {

        println "Confirming it"
    }

    @Given("^using the financial status service ui\$")
    public void using_the_financial_status_service_ui() throws Throwable {

        driver.get("http://localhost:8001");

    }

    @When("^the financial status check is performed with\$")
    public void the_financial_status_check_is_performed_with(DataTable arg1) throws Throwable {

        Map<String, String> entries = arg1.asMap(String.class, String.class)

        println driver.currentUrl

        List<String, String> entriesList = arg1.asList(String.class)

        entries.each { k, v ->
            String key = toCamelCase(k)

            if (key.endsWith("Date")) {
                if (v != null && v.length() != 0) {

                    String day = v.substring(0, v.indexOf("/"))
                    String month = v.substring(v.indexOf("/") + 1, v.lastIndexOf("/"))
                    String year = v.substring(v.lastIndexOf("/") + 1)

                    sendKeys(driver.findElement(By.id(key + "Day")), day)
                    sendKeys(driver.findElement(By.id(key + "Month")), month)
                    sendKeys(driver.findElement(By.id(key + "Year")), year)

                } else {
                    driver.findElement(By.id(key + "Day")).clear()
                    driver.findElement(By.id(key + "Month")).clear()
                    driver.findElement(By.id(key + "Year")).clear()
                }
            } else {
                sendKeys(driver.findElement(By.id(key)), v)
            }
        }

        driver.sleep(delay)
        driver.findElement(By.className("button")).click();
    }

    @Then("^the service displays the following message\$")
    public void the_service_displays_the_following_message(DataTable arg1) throws Throwable {

        Map<String, String> entries = arg1.asMap(String.class, String.class)

        assert driver.findElement(By.id(entries.get("Error Field"))).getText() == entries.get("Error Message")

    }


}
