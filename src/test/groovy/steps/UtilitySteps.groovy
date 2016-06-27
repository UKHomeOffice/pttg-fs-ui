package steps

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

import java.text.SimpleDateFormat

/**
 * @Author Home Office Digital
 */
class UtilitySteps {

    // todo pull in similar stuff from other projects
    // todo move this into a common jar

    def static toCamelCase(String s) {
        if (s.isEmpty()) return ""
        def words = s.tokenize(" ")*.toLowerCase()*.capitalize().join("")
        words[0].toLowerCase() + words.substring(1)
    }

    def static parseDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")
        Date date = sdf.parse(dateString)
        date
    }

    // todo parseIsoDate

    def static clickRadioButton(WebDriver driver, RadioButtonConfig radioConfig, String value) {

        if (radioConfig.options.containsKey(value)) {

            String id = radioConfig.options.get(value)

            By byCss = By.cssSelector("[id='$id'][type='radio']")
            driver.findElement(byCss).click()
        }
    }

    def static class RadioButtonConfig {
        def options = [:]

        def withOption(String choice, String id) {
            options.put(choice, id)
            return this
        }
    }
}
